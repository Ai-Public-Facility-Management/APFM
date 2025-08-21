# app/nodes/draw_report.py
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from pathlib import Path
import re
from datetime import datetime, date
from reportlab.platypus import Table, TableStyle
from reportlab.lib import colors
import textwrap
from io import BytesIO
import base64

def format_inspection_date(inspection_date):
    if isinstance(inspection_date, (datetime, date)):
        return inspection_date.strftime("%Y.%m.%d")
    elif isinstance(inspection_date, str):
        return inspection_date.replace("-", ".").replace("/", ".")
    else:
        return str(inspection_date)
    
def draw_score_table(c, facilities, x, y, width):
    # 헤더 정의
    data = [["시설물", "파손 점수", "방해 점수", "민원 점수", "수리일 점수", "총점"]]

    # 행 추가
    for fac in facilities:
        d = fac["score_detail"]
        row = [
            fac["name"],
            d["damage_score"],
            d["hindrance_score"],
            d["complaint_score"],
            d["repair_score"],
            d["total"]
        ]
        data.append(row)

    # 컬럼 너비 계산
    col_widths = [width * 0.2, width * 0.15, width * 0.15, width * 0.15, width * 0.2, width * 0.15]

    # 표 객체 생성
    table = Table(data, colWidths=col_widths)
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.lightgrey),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.black),
        ("ALIGN", (0, 0), (-1, -1), "CENTER"),
        ("FONTNAME", (0, 0), (-1, 0), "Myungjo"),
        ("FONTNAME", (0, 1), (-1, -1), "Myungjo"),
        ("FONTSIZE", (0, 0), (-1, -1), 10),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.grey),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]))

    # 표 그리기
    table.wrapOn(c, width, y)
    table.drawOn(c, x, y - table._height)

    # 다음 줄 y 좌표 반환
    return y - table._height - 20

def draw_wrapped_text(c, text, x, y, fontname, fontsize, max_width, line_spacing):
    c.setFont(fontname, fontsize)
    avg_char_width = c.stringWidth("가", fontname, fontsize)  # 한글 평균 글자 폭 대략 계산
    max_chars_per_line = max(int(max_width / avg_char_width), 1) + 8 # 한 줄 최대 글자 수 (1 이상 보장)

    lines = textwrap.wrap(text, width=max_chars_per_line)
    for line in lines:
        if y < BOTTOM_MARGIN:
            c.showPage()
            y = PAGE_HEIGHT - TOP_MARGIN
            c.setFont(fontname, fontsize)
        c.drawString(x, y, line)
        y -= line_spacing
    return y

BASE_DIR = Path(__file__).resolve().parents[1]
FONTS_DIR = BASE_DIR / "assets" / "fonts"

FONTS_DIR = BASE_DIR / "assets"
pdfmetrics.registerFont(TTFont('HeadlineM', str(FONTS_DIR / 'H2HDRM.TTF')))
pdfmetrics.registerFont(TTFont('JungGothic', str(FONTS_DIR / 'H2GTRM.TTF')))
pdfmetrics.registerFont(TTFont('Myungjo', str(FONTS_DIR / 'H2MJSM.TTF')))

LEFT_MARGIN = 50
RIGHT_MARGIN = 50
TOP_MARGIN = 60
BOTTOM_MARGIN = 60
LINE_SPACING = 20
PAGE_WIDTH, PAGE_HEIGHT = A4

def draw_report(parsed_sections, inspection_date, facilities):
    buffer = BytesIO()
    c = canvas.Canvas(buffer, pagesize=A4)
    width, height = PAGE_WIDTH, PAGE_HEIGHT
    y = height - TOP_MARGIN

    formatted_date = format_inspection_date(inspection_date)

    def new_page():
        nonlocal y
        c.showPage()
        y = height - TOP_MARGIN

    # 배경 사각형 (제목 박스용)
    c.setFillColor(colors.lightgrey)
    c.rect(LEFT_MARGIN - 10, y - 15, width - LEFT_MARGIN - RIGHT_MARGIN + 20, 50, fill=1, stroke=1)
    c.setFillColor(colors.black)

    # 제목
    title_text = f"{formatted_date} 정기점검"
    c.setFont("HeadlineM", 22)
    text_width = c.stringWidth(title_text, "HeadlineM", 22)
    x = (width - text_width) / 2
    c.drawString(x, y, title_text)
    y -= 40

    # 구분선
    c.setStrokeColor(colors.grey)
    c.line(LEFT_MARGIN, y, width - RIGHT_MARGIN, y)
    y -= 30

    # 부제목
    c.setFont("JungGothic", 15)
    subtitle_text = f"{formatted_date}에 발견된 공공시설물 이상 상황"
    text_width = c.stringWidth(subtitle_text, "JungGothic", 15)
    x = (width - text_width) / 2
    c.drawString(x, y, subtitle_text)
    y -= 20

    # 구분선
    c.setStrokeColor(colors.grey)
    c.line(LEFT_MARGIN, y, width - RIGHT_MARGIN, y)
    y -= 20

    for section in parsed_sections:
        title = section["title"]
        content_lines = section["content"]

        # 로마 숫자 제목
        y -= 30
        c.setFont("Myungjo", 16)
        c.drawString(LEFT_MARGIN, y, title)
        y -= 20

        for line in content_lines:
            line = line.strip()

            # 표 삽입 조건: 로마 숫자 Ⅲ. 검토 의견 → 1. 사안별 현황 요약
            if title.startswith("Ⅲ.") and re.match(r"^1\. 사안별 현황 요약", line):
                # 텍스트 먼저 출력
                x = LEFT_MARGIN + 10
                fontname = "Myungjo"
                fontsize = 15
                max_width = PAGE_WIDTH - RIGHT_MARGIN - x
                y = draw_wrapped_text(c, line, x, y - 10, fontname, fontsize, max_width, LINE_SPACING)

                # 점수표 삽입 (facilities는 state에서 가져온 정렬된 리스트)
                y = draw_score_table(c, facilities, LEFT_MARGIN, y - 10, PAGE_WIDTH - LEFT_MARGIN - RIGHT_MARGIN)
                continue

            # 들여쓰기 및 폰트 설정 (기존 코드 유지)
            if re.match(r"^\d+\.", line):
                y -= 10
                x = LEFT_MARGIN + 10
                fontname = "Myungjo"
                fontsize = 15
            elif line.startswith("ㅇ "):
                x = LEFT_MARGIN + 30
                fontname = "Myungjo"
                fontsize = 13
            else:
                y -= 10
                x = LEFT_MARGIN + 30
                fontname = "Myungjo"
                fontsize = 14

            max_width = PAGE_WIDTH - RIGHT_MARGIN - x  # 글자가 들어갈 최대 너비 계산
            y = draw_wrapped_text(c, line, x, y, fontname, fontsize, max_width, LINE_SPACING)
    c.save()
    buffer.seek(0)
    return buffer

def draw_report_node(state: dict) -> dict:
    parsed_sections = state.get("parsed_sections")
    date = state.get("inspection_date")
    facilities = state.get("facilities")
    file = draw_report(parsed_sections, date, facilities)
    b64_file = base64.b64encode(file.read()).decode('utf-8')
    state["pdf_report_path"] = b64_file
    return state

from format_inspection_date import format_inspection_date
from draw_wrapped_text import draw_wrapped_text
from draw_score_table import draw_score_table

from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib import colors
import re

pdfmetrics.registerFont(TTFont('HeadlineM', "C:\\Users\\User\\Desktop\\빅프\\APFM\\AI\\H2HDRM.TTF"))
pdfmetrics.registerFont(TTFont('JungGothic', "C:\\Users\\User\\Desktop\\빅프\\APFM\\AI\\H2GTRM.TTF"))
pdfmetrics.registerFont(TTFont('Myungjo', "C:\\Users\\User\\Desktop\\빅프\\APFM\\AI\\H2MJSM.TTF"))

LEFT_MARGIN = 50
RIGHT_MARGIN = 50
TOP_MARGIN = 60
BOTTOM_MARGIN = 60
LINE_SPACING = 20
PAGE_WIDTH, PAGE_HEIGHT = A4

def draw_report(parsed_sections, inspection_date, facilities, output="report.pdf"):
    c = canvas.Canvas(output, pagesize=A4)
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
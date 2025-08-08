from reportlab.platypus import Table, TableStyle
from reportlab.lib import colors

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
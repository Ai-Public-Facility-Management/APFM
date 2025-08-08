import textwrap
from reportlab.lib.pagesizes import A4

LEFT_MARGIN = 50
RIGHT_MARGIN = 50
TOP_MARGIN = 60
BOTTOM_MARGIN = 60
LINE_SPACING = 20
PAGE_WIDTH, PAGE_HEIGHT = A4


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
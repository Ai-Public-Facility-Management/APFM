import re

def parse_report_sections(text: str):
    # 패턴: 로마 숫자 + 마침표 + 제목
    pattern = re.compile(r"(ⅰ{1,3}|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|XI|XII)\. [^\n]+")
    matches = list(pattern.finditer(text))

    sections = []
    for i, match in enumerate(matches):
        start = match.end()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        title = match.group().strip()
        content = text[start:end].strip().split("\n")
        sections.append({"title": title, "content": [line.strip() for line in content if line.strip()]})

    return sections
# app/nodes/parse_report.py
import re

def parse_report_sections(text: str):
    pattern = re.compile(r"(Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ)\. [^\n]+")
    matches = list(pattern.finditer(text))
    sections = []
    for i, match in enumerate(matches):
        start = match.end()
        end = matches[i+1].start() if i+1 < len(matches) else len(text)
        title = match.group().strip()
        content = text[start:end].strip().split("\n")
        sections.append({"title": title, "content": [line.strip() for line in content if line.strip()]})
    return sections

def parse_report_node(state: dict) -> dict:
    text = state.get("final_report", "")
    state["parsed_sections"] = parse_report_sections(text)
    return state

from datetime import datetime, date

def format_inspection_date(inspection_date):
    if isinstance(inspection_date, (datetime, date)):
        return inspection_date.strftime("%Y.%m.%d")
    elif isinstance(inspection_date, str):
        return inspection_date.replace("-", ".").replace("/", ".")
    else:
        return str(inspection_date)
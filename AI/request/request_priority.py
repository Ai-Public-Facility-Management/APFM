import requests

url = "http://localhost:8080/priority/run"

state = {
    "inspection_date": "2025-08-11",
    "facilities": [
        {
            "name": "정류장 쉘터",
            "damage": "파손",
            "estimated_cost": 1200000,
            "hindrance_level": "높음",
            "complaints": 5,
            "last_repair_date": "2022-09-15",
            "cost_basis": "- 정류장 쉘터 수리: 파손 부위 교체 및 보강 필요. 철재 구조물 보강 및 방풍 유리 교체 기준 적용.\n- 인건비: 특수 작업 인부 2명, 일반 인부 1명 기준. 일당 총합 450,000원으로 산정.\n- 자재비: 방풍 유리 교체 및 구조 보강용 자재 약 600,000원.\n- 장비비: 고소작업차 등 장비 임차 150,000원.\n- 기타: 안전 펜스 설치 등 현장 안전조치 비용 100,000원."
        },
        {
            "name": "볼라드",
            "damage": "균열",
            "estimated_cost": 400000,
            "hindrance_level": "보통",
            "complaints": 1,
            "last_repair_date": "2023-11-20",
            "cost_basis": "- 볼라드 교체: 노후화된 볼라드 1개소 철거 후 신규 설치.\n- 인건비: 특별 인부 2명, 일반 인부 1명, 일당 합산 400,000원.\n- 자재비: 스테인리스 볼라드 1개 기준 200,000원.\n- 장비비: 절단기 및 충전해머 등 경장비 사용료 20,000원.\n- 기타: 작업 후 정리 및 보행자 통행 유도 조치 비용 30,000원."
        },
        {
            "name": "가로수 지주대",
            "damage": "변색",
            "estimated_cost": 200000,
            "hindrance_level": "보통",
            "complaints": 0,
            "last_repair_date": "2024-04-10",
            "cost_basis": "- 가로수 지주대 점검: 현재 상태는 양호하나 보완 필요 구간 있음.\n- 예방적 보강작업: 지주대 결속 보강용 로프 및 고정핀 교체 일부 진행.\n- 인건비: 보통 인부 1명 투입, 일당 100,000원 기준.\n- 자재비: 로프, 고정핀 등 소모품 약 50,000원.\n- 기타: 작업 중 보행자 안전조치 및 작업표지판 설치비 50,000원."
        }
    ]
}

resp = requests.post(url, json=state)
print(resp.status_code)
print(resp.json())

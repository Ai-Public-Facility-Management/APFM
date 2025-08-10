# app/services/report_service.py
from priority_graph import build_graph
from concurrent.futures import ThreadPoolExecutor
import uuid, os

_executor = ThreadPoolExecutor(max_workers=2)
JOB_STORE = {}  # 간단한 메모리 job 저장. 실제 운영은 Redis/Celery 권장

def run_inspection_graph_sync(state: dict, llm=None) -> dict:
    graph = build_graph(llm=llm)
    result = graph.invoke(state)  # 사용자의 graph.invoke 패턴 유지
    return result

def run_inspection_background(state: dict, llm=None):
    job_id = str(uuid.uuid4())
    JOB_STORE[job_id] = {"status": "queued", "result": None}
    def _job():
        try:
            JOB_STORE[job_id]["status"] = "running"
            res = run_inspection_graph_sync(state, llm=llm)
            JOB_STORE[job_id]["status"] = "finished"
            JOB_STORE[job_id]["result"] = res
        except Exception as e:
            JOB_STORE[job_id]["status"] = "error"
            JOB_STORE[job_id]["error"] = str(e)
    _executor.submit(_job)
    return job_id

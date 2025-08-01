from estimate import run_hybrid_rag_query

def rag_cost_estimate_node(state):
    query = state["vision_analysis"]
    vectordb = state["vectordb"]

    result = run_hybrid_rag_query(vectordb, query)

    state["cost_estimate"] = result
    return state
from estimate import run_hybrid_rag_query

def rag_cost_estimate_node(state):
    query = state["vision_analysis"]
    vectordb = state["vectordb"]

    parsed_result, _ = run_hybrid_rag_query(vectordb, query)

    state["estimate"] = parsed_result.get("estimate")
    state["estimate_basis"] = parsed_result.get("estimate_basis")

    return state

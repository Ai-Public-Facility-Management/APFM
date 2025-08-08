from estimate import run_hybrid_rag_query

def rag_cost_estimate_node(state):
    query = state["vision_analysis"]
    vectordb = state["vectordb"]

    answer, meta_docs = run_hybrid_rag_query(vectordb, query)

    state["cost_estimate"] = answer
    state["cost_estimate_docs"] = meta_docs
    return state
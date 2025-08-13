// src/pages/BoardList.tsx
import React, { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import "./BoardList.css";
import { fetchBoards, BoardItem } from "../../api/board";

// 이름 마스킹 함수
const maskName = (name: string) => {
  if (!name) return "";
  if (name.length === 1) return '*';
  return name.slice(0, -1) + '*';
};

const BoardList = () => {
  const [posts, setPosts] = useState<BoardItem[]>([]);
  const [page, setPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [totalPages, setTotalPages] = useState(1);

  // 임시 데이터 로딩
  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchBoards(page - 1, 10, searchTerm);
        setPosts(data.content);
        setTotalPages(data.totalPages);
      } catch (e) {
        console.error(e);
        setPosts([]);
        setTotalPages(1);
      }
    };
    load();
  }, [page, searchTerm]);

  const filteredPosts = posts.filter((post) =>
    post.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Layout>
      <div className="board-page">
        <h1 className="board-title">통합검색</h1>

        {/* 검색창 */}
        <div className="board-search-box">
          <input
            type="text"
            placeholder="검색어를 입력해주세요."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className="search-btn">검색</button>
        </div>

        {/* 테이블 */}
        <table className="board-table">
          <thead>
            <tr>
              <th style={{ width: "80px" }}>번호</th>
              <th>제목</th>
              <th style={{ width: "150px" }}>작성자</th>
              <th style={{ width: "150px" }}>작성일</th>
            </tr>
          </thead>
          <tbody>
            {filteredPosts.length > 0 ? (
              filteredPosts.map((post) => (
                <tr key={post.id}>
                  <td>{post.id}</td>
                  <td className="title-cell">{post.title}</td>
                  <td>{maskName(post.writerName)}</td>
                  <td>{post.createdAt}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="no-data">
                  검색 결과가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        {/* 페이지네이션 */}
        <div className="pagination">
          <button
            onClick={() => setPage((p) => Math.max(1, p - 1))}
            disabled={page === 1}
          >
            이전
          </button>
          {[1, 2, 3, 4, 5].map((n) => (
            <button
              key={n}
              className={page === n ? "active" : ""}
              onClick={() => setPage(n)}
            >
              {n}
            </button>
          ))}
          <button onClick={() => setPage((p) => p + 1)}>다음</button>
        </div>
      </div>
    </Layout>
  );
};

export default BoardList;

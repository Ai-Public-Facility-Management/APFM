// src/pages/BoardList.tsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import "./BoardList.css";
import { fetchBoards, BoardItem } from "../../api/board";

// 부서 코드 → 한글 매핑
const departmentLabels: Record<string, string> = {
  DEVELOPMENT: "개발부서",
  DESIGN: "디자인부서",
  MARKETING: "마케팅부서",
  SALES: "총무과",
  HR: "인사과",
  FINANCE: "재무과",
};

// 이름 마스킹 함수
const maskName = (name: string) => {
  if (!name) return "";
    if (name.includes("@")) {
      // 이메일 마스킹
      const [local, domain] = name.split("@");
      if (local.length <= 2) return local[0] + "*".repeat(local.length - 1) + "@" + domain;
      return local[0] + "*".repeat(local.length - 2) + local[local.length - 1] + "@" + domain;
    } else {
      // 이름 마스킹
      if (name.length <= 2) return name[0] + "*";
      return name[0] + "*".repeat(name.length - 2) + name[name.length - 1];
    }
};

const BoardList = () => {
  const navigate = useNavigate();
//   const [posts, setPosts] = useState<Post[]>([]);
  const [posts, setPosts] = useState<BoardItem[]>([]);
  const [page, setPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [totalPages, setTotalPages] = useState(1);
  const [searchType, setSearchType] = useState("전체");

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

  const filteredPosts = posts.filter((post) => {
    const matchesTitle = post.title.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = searchType === "전체" || post.type === searchType; // type 비교
    return matchesTitle && matchesType;
  });

  // NOTICE 먼저, 그 외 게시글 뒤로 정렬
  const sortedPosts = [...filteredPosts].sort((a, b) => {
    if (a.type === "NOTICE" && b.type !== "NOTICE") return -1;
    if (a.type !== "NOTICE" && b.type === "NOTICE") return 1;
    return 0;
  });


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
          <select
                className="facility-select"
                value={searchType}
                onChange={(e) => setSearchType(e.target.value)}
            >
                <option value="전체">전체</option>
                <option value="NOTICE">공지</option>
                <option value="FREE">자유</option>
            </select>

          <button className="search-btn">검색</button>

          <button
            className="write-btn"
            onClick={() => navigate("/board/write")}
          >
            작성
          </button>
        </div>

        {/* 테이블 */}
        <table className="board-table">
          <thead>
            <tr>
              <th>제목</th>
              <th style={{ width: "100px" }}>작성자</th>
              <th style={{ width: "130px" }}>작성 부서</th>
              <th style={{ width: "150px" }}>작성일</th>
            </tr>
          </thead>
          <tbody>
            {sortedPosts.length > 0 ? (
              sortedPosts.map((post) => (
                <tr key={post.id}
                  className={post.type === "NOTICE" ? "notice-row" : ""}
                    onClick={() => navigate(`/board/${post.id}`)}>
                  <td className="title-cell">{post.title}</td>
                  <td>{maskName(post.authorName)}</td>
                  <td>{departmentLabels[post.authorDepartment] || post.authorDepartment}</td>
                  <td>{post.createdAt ? post.createdAt.slice(0, 10) : ""}</td>
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
          {Array.from({ length: totalPages }, (_, i) => i + 1).map((n) => (
            <button
              key={n}
              className={page === n ? "active" : ""}
              onClick={() => setPage(n)}
            >
              {n}
            </button>
          ))}
          <button
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}>
            다음
          </button>
        </div>
      </div>
    </Layout>
  );
};

export default BoardList;

// src/pages/BoardDetail.tsx
import React, { useEffect, useState } from "react";
import { useParams , useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";

import {
  getPostDetail,
  getComments,
  addComment,
  PostDetail,
  Comment,
} from "../../api/board";
import "./BoardDetail.css";

// ✅ 이름/이메일 마스킹 함수
const maskAuthor = (value: string) => {
  if (!value) return "";
  if (value.includes("@")) {
    // 이메일 마스킹
    const [local, domain] = value.split("@");
    if (local.length <= 2) return local[0] + "*".repeat(local.length - 1) + "@" + domain;
    return local[0] + "*".repeat(local.length - 2) + local[local.length - 1] + "@" + domain;
  } else {
    // 이름 마스킹
    if (value.length <= 2) return value[0] + "*";
    return value[0] + "*".repeat(value.length - 2) + value[value.length - 1];
  }
};

export default function BoardDetail() {
  const { postId } = useParams<{ postId: string }>();
  const navigate = useNavigate();

  const [post, setPost] = useState<PostDetail | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);

//   const currentUserEmail = localStorage.getItem("userEmail"); // 로그인 사용자
//   const isAuthor = post?.authorEmail === currentUserEmail; // 작성자 여부

  // 데이터 로딩
  useEffect(() => {
    if (!postId) return;
    const fetchData = async () => {
      try {
        const [postRes, commentsRes] = await Promise.all([
          getPostDetail(postId),
          getComments(postId),
        ]);
        setPost(postRes.data);
        setComments(commentsRes.data.content);
      } catch (err) {
        console.error("게시글/댓글 로드 실패", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [postId]);

  const handleAddComment = async () => {
    if (!newComment.trim()) return;
    try {
      await addComment(postId!, newComment);
      setNewComment("");
      const commentsRes = await getComments(postId!);
      setComments(commentsRes.data.content);
      // ✅ 댓글 수 즉시 반영
      setPost((prev) =>
        prev ? { ...prev, commentCount: prev.commentCount + 1 } : prev
      );
    } catch (err) {
      console.error("댓글 작성 실패", err);
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (!post) return <div className="error">게시글을 찾을 수 없습니다.</div>;

  return (
    <Layout>
      <div className="board-detail">
        <h2 className="title">{post.title}</h2>

        <div className="meta">
          {/* ✅ 백엔드에서 authorEmail 내려주면 프론트에서 마스킹 */}
          <span>작성자: {maskAuthor(post.authorName || post.authorEmail)}</span>
          <span>부서: {post.department || "없음"}</span>
          <span>작성일: {new Date(post.createdAt).toLocaleString()}</span>
          <span>조회수: {post.viewCount}</span>
        </div>

        <div className="content">{post.content}</div>

        {post.attachments?.length > 0 && (
          <div className="attachments">
            <h4>첨부파일</h4>
            <ul>
              {post.attachments.map((a) => (
                <li key={a.id}>
                  <a href={a.storedUrl} target="_blank" rel="noopener noreferrer">
                    {a.originalName}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        )}

        <div className="comments-section">
          <h3>댓글 ({post.commentCount})</h3>
          <ul className="comment-list">
            {comments.map((c) => (
              <li key={c.id} className="comment-item">
                <div className="comment-meta">
                  {/* ✅ 댓글도 이메일/이름 마스킹 */}
                  <span>{maskAuthor(c.authorName || c.authorEmail)}</span>
                  <span>{new Date(c.createdAt).toLocaleString()}</span>
                  {c.edited && <span className="edited">(수정됨)</span>}
                </div>
                <div className="comment-content">{c.content}</div>
              </li>
            ))}
          </ul>

          <div className="comment-form">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="댓글을 입력하세요"
            />
            <button onClick={handleAddComment}>등록</button>
          </div>
        </div>
      </div>
    </Layout>
  );
}

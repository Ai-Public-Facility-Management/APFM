// src/pages/BoardDetail.tsx
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import {
  fetchBoardDetail,
  fetchComments,
  createComment,
  deleteComment,
  updateComment,
  BoardDetail as PostDetail,
  Comment,
} from "../../api/board";
import "./BoardDetail.css";

const maskAuthor = (value: string) => {
  if (!value) return "";
  if (value.includes("@")) {
    const [local, domain] = value.split("@");
    if (local.length <= 2) return local[0] + "*".repeat(local.length - 1) + "@" + domain;
    return local[0] + "*".repeat(local.length - 2) + local[local.length - 1] + "@" + domain;
  } else {
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
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");

  const reloadComments = async () => {
    const res = await fetchComments(Number(postId));
    setComments(res.content);
  };

  useEffect(() => {
    if (!postId) return;
    const fetchData = async () => {
      try {
        const [postRes, commentsRes] = await Promise.all([
          fetchBoardDetail(Number(postId)),
          fetchComments(Number(postId)),
        ]);
        setPost(postRes);
        setComments(commentsRes.content);
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
      await createComment(Number(postId), newComment);
      setNewComment("");
      await reloadComments();
      setPost((prev) =>
        prev ? { ...prev, commentCount: prev.commentCount + 1 } : prev
      );
    } catch (err) {
      console.error("댓글 작성 실패", err);
    }
  };

  const handleDeleteComment = async (id: number) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await deleteComment(id);
      await reloadComments();
      setPost((prev) =>
        prev ? { ...prev, commentCount: prev.commentCount - 1 } : prev
      );
    } catch (err) {
      console.error("댓글 삭제 실패", err);
    }
  };

  const handleStartEdit = (id: number, content: string) => {
    setEditingCommentId(id);
    setEditContent(content);
  };

  const handleUpdateComment = async (id: number) => {
    if (!editContent.trim()) return;
    try {
      await updateComment(id, editContent);
      setEditingCommentId(null);
      setEditContent("");
      await reloadComments();
    } catch (err) {
      console.error("댓글 수정 실패", err);
    }
  };

  const handleEditPost = () => {
    if (!post) return;
    navigate("/board/write", {
        state: {
          id: post.id,
          title: post.title,
          content: post.content,
          imageUrl: post.imageUrl
        }
      });
  };

  if (loading) {
    return (
      <Layout>
        <div className="loading">로딩 중...</div>
      </Layout>
    );
  }

  if (!post) {
    return (
      <Layout>
        <div className="no-post">게시글을 찾을 수 없습니다.</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="board-detail">
        <h2 className="title">{post.title}</h2>
        <div className="meta">
          <span>작성자: {maskAuthor(post.authorName || post.authorEmail)}</span>
          <span>부서: {post.authorDepartment}</span>
          <span>작성일: {new Date(post.createdAt).toLocaleString()}</span>
          <span>조회수: {post.viewCount}</span>
        </div>
        <hr className="section-divider" />

        <div className="post-content">{post.content}</div>

        <hr className="section-divider" />

        <div className="post-actions">
          {post.isAuthor && (
            <button className="action-btn" onClick={handleEditPost}>수정</button>
          )}
          <button className="action-btn" onClick={() => navigate("/board")}>뒤로가기</button>
        </div>

        <hr className="section-divider" />

        <div className="comments-section">
          <h3>댓글 ({post.commentCount})</h3>
          <ul className="comment-list">
            {comments.map((c) => (
              <li key={c.id} className="comment-item">
                <div className="comment-meta">
                  <span>{maskAuthor(c.authorName || c.authorEmail)}</span>
                  <span>{new Date(c.createdAt).toLocaleString()}</span>
                  {c.edited && <span className="edited">(수정됨)</span>}
                  {c.isAuthor && (
                    <>
                      <button onClick={() => handleStartEdit(c.id, c.content)}>
                        수정
                      </button>
                      <button onClick={() => handleDeleteComment(c.id)}>
                        삭제
                      </button>
                    </>
                  )}
                </div>
                {editingCommentId === c.id ? (
                  <div className="comment-edit">
                    <textarea
                      value={editContent}
                      onChange={(e) => setEditContent(e.target.value)}
                    />
                    <button onClick={() => handleUpdateComment(c.id)}>저장</button>
                    <button
                      onClick={() => {
                        setEditingCommentId(null);
                        setEditContent("");
                      }}
                    >
                      취소
                    </button>
                  </div>
                ) : (
                  <div className="comment-content">{c.content}</div>
                )}
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

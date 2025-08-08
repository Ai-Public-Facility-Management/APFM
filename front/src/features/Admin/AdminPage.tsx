import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import UserCard from "./UserCard";
import { approveUser, getPendingUsers, PendingUser, rejectUser } from "../../api/admin";

export default function AdminPage() {
  const [users, setUsers] = useState<PendingUser[]>([]);

  const fetchUsers = async () => {
    try {
      const res = await getPendingUsers();
      setUsers(res);
    } catch (error) {
      console.error("사용자 목록 불러오기 실패:", error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleApprove = async (email: string) => {
    try {
      await approveUser(email);
      alert(`${email} 승인 완료`);
      fetchUsers(); // 리스트 갱신
    } catch (error) {
      alert("승인 실패");
    }
  };

  const handleReject = async (email: string) => {
    try {
      await rejectUser(email);
      alert(`${email} 거절 완료`);
      fetchUsers(); // 리스트 갱신
    } catch (error) {
      alert("거절 실패");
    }
  };

  return (
    <Layout>
    <div
      style={{
        width: "100%",
        maxWidth: "1200px",   // 페이지 최대 폭
        margin: "0 auto",     // 가운데 배치
        padding: "20px",
        display: "block",     // flex 해제
        textAlign: "left"     // 내부 콘텐츠 좌측 정렬
      }}
    >
      {users.map((u, idx) => (
        <UserCard
          key={idx}
          name={u.username}
          email={u.email}
          department={u.department}
          onApprove={() => handleApprove(u.email)}
          onReject={() => handleReject(u.email)}
        />
      ))}
    </div>
    </Layout>
  );
}

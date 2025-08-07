interface UserCardProps {
  name: string;
  email: string;
  department: string;
  onApprove: () => void;
  onReject: () => void;
}

export default function UserCard({
  name,
  email,
  department,
  onApprove,
  onReject
}: UserCardProps) {
  return (
    <div
      style={{
        width: "100%",
        border: "1px solid #ddd",
        borderRadius: "8px",
        padding: "20px",
        marginBottom: "16px",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center"
      }}
    >
      <div>
        <h3 style={{ margin: "0 0 8px", fontSize: "18px" }}>{name}</h3>
        <p style={{ margin: "4px 0" }}>
          <strong>이메일</strong> {email}
        </p>
        <p style={{ margin: "4px 0" }}>
          <strong>부서</strong> {department}
        </p>
      </div>

      <div style={{ display: "flex", gap: "8px" }}>
        <button
          style={{
            padding: "8px 16px",
            fontSize: "14px",
            borderRadius: "6px",
            border: "1px solid #1976d2",
            backgroundColor: "#fff",
            color: "#1976d2",
            cursor: "pointer"
          }}
          onClick={onApprove}
        >
          승인하기 ▼
        </button>
        <button
          style={{
            padding: "8px 16px",
            fontSize: "14px",
            borderRadius: "6px",
            border: "1px solid #000",
            backgroundColor: "#fff",
            cursor: "pointer"
          }}
          onClick={onReject}
        >
          거절하기
        </button>
      </div>
    </div>
  );
}

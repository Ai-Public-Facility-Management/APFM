interface PaginationProps {
  current: number;
  total: number;
  onChange: (page: number) => void;
  maxButtons?: number;
}

export default function Pagination({
  current,
  total,
  onChange,
  maxButtons = 7
}: PaginationProps) {
  const pages: (number | string)[] = [];

  if (current > 1) pages.push(1);
  if (current > Math.floor(maxButtons / 2) + 1) pages.push("...");

  const start = Math.max(2, current - Math.floor(maxButtons / 2));
  const end = Math.min(total - 1, current + Math.floor(maxButtons / 2));

  for (let i = start; i <= end; i++) pages.push(i);

  if (current < total - Math.floor(maxButtons / 2)) pages.push("...");
  if (current < total) pages.push(total);

  const baseStyle: React.CSSProperties = {
    padding: "6px 12px",
    border: "1px solid #ccc",
    borderRadius: "4px",
    cursor: "pointer",
    backgroundColor: "#fff"
  };

  const activeStyle: React.CSSProperties = {
    ...baseStyle,
    backgroundColor: "#0d47a1",
    color: "#fff",
    fontWeight: "bold",
    borderColor: "#0d47a1"
  };

  const disabledStyle: React.CSSProperties = {
    ...baseStyle,
    backgroundColor: "#f0f0f0",
    color: "#aaa",
    cursor: "default"
  };

  return (
    <div style={{ display: "flex", gap: "4px", justifyContent: "center" }}>
      <button
        disabled={current === 1}
        style={current === 1 ? disabledStyle : baseStyle}
        onClick={() => onChange(current - 1)}
      >
        이전
      </button>

      {pages.map((p, idx) =>
        p === "..." ? (
          <span key={idx} style={{ padding: "6px 12px" }}>
            ...
          </span>
        ) : (
          <button
            key={idx}
            style={p === current ? activeStyle : baseStyle}
            onClick={() => onChange(Number(p))}
          >
            {p}
          </button>
        )
      )}

      <button
        disabled={current === total}
        style={current === total ? disabledStyle : baseStyle}
        onClick={() => onChange(current + 1)}
      >
        다음
      </button>
    </div>
  );
}

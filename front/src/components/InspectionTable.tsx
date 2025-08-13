// [기능 요약] 점검 리스트 테이블 컴포넌트
import React from "react";
import type { InspectionSummary } from "../api/inspection";
import "../features/Inspections/inspection.css";

type Props = {
  rows: InspectionSummary[];
  onRowClick?: (id: number) => void;
};

export default function InspectionTable({ rows, onRowClick }: Props) {
  return (
    <div className="it-table-wrap">
      <table className="it-table">
        <thead>
          <tr>
            <th className="it-th">ID</th>
            <th className="it-th">작성일</th>
            <th className="it-th">상태</th>
            <th className="it-th">수리</th>
            <th className="it-th">철거</th>
            <th className="it-th">이상</th>
            <th className="it-th">보고서</th>
          </tr>
        </thead>
        <tbody>
          {rows.map(r => (
            <tr
              key={r.id}
              className={onRowClick ? "it-row--click" : ""}
              onClick={() => onRowClick?.(r.id)}
            >
              <td className="it-td">{r.id}</td>
              <td className="it-td">{r.createDate}</td>
              <td className="it-td">{r.status}</td>
              <td className="it-td">{r.repairCount}</td>
              <td className="it-td">{r.removalCount}</td>
              <td className="it-td">{r.hasIssue ? "✅" : "—"}</td>
              <td className="it-td">{r.hasReport ? "✅" : "—"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// src/features/schedule/IntervalModal.tsx
import { useState } from "react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";

import { saveInspectionSetting } from "../../api/inspection";
import { useScheduleModal } from "./ScheduleModalProvider";
import "./IntervalModal.css";

// UI용 주기 타입(백엔드엔 inspectionCycle 숫자로 보냄)
type Frequency = "DAILY" | "WEEKLY" | "MONTHLY";

export default function IntervalModal() {
  const { close } = useScheduleModal();

  // 날짜/시간/주소/주기
  const [startObj, setStartObj] = useState<Date>(new Date());
  const [startDate, setStartDate] = useState<string>(() =>
    new Date().toISOString().slice(0, 10)
  );
  const [hour, setHour] = useState(9);
  const [minute, setMinute] = useState(0);
  //const [address, setAddress] = useState("");
  const [freq, setFreq] = useState<Frequency>("DAILY");
  const [loading, setLoading] = useState(false);

  // 드롭다운 옵션
  const hours = Array.from({ length: 24 }, (_, i) => i);      // 0..23
  const minutesArr = Array.from({ length: 60 }, (_, i) => i); // 0..59

  // 달력 선택
  const onPick = (d?: Date) => {
    if (!d) return;
    setStartObj(d);
    setStartDate(d.toISOString().slice(0, 10));
  };

  const pad = (n: number) => String(n).padStart(2, "0");
  // NOTE: 팀 규칙에 맞게 필요하면 조정 (예: MONTHLY=30일 가정)
  const freqToCycle = (f: Frequency) => (f === "DAILY" ? 1 : f === "WEEKLY" ? 7 : 30);

  const onSave = async () => {
    if (!startDate) return alert("시작 날짜를 선택하세요.");
    //if (!address.trim()) return alert("주소를 입력하세요.");

    setLoading(true);
    try {
      const payload = {
        startDate,                                 // "YYYY-MM-DD"
        startTime: `${pad(hour)}:${pad(minute)}`,  // "HH:mm"
        inspectionCycle: freqToCycle(freq),      // 정수(일 단위)
        
      };
      await saveInspectionSetting(payload);
      alert("저장되었습니다.");
      close();
    } catch (e) {
      console.error(e);
      alert("저장 실패: 서버 응답을 확인해주세요.");
    } finally {
      setLoading(false);
    }
  };

  const stop = (e: React.MouseEvent) => e.stopPropagation();

  return (
    <div className="modal-backdrop" onClick={close}>
      <div className="modal" onClick={stop}>
        <button className="modal-close" onClick={close}>✕</button>
        <h2 className="modal-title">점검 주기 설정</h2>

        <div className="modal-grid">
          {/* 왼쪽: 달력 */}
          <div>
            <div className="calendar-card">
              <DayPicker
                mode="single"
                selected={startObj}
                onSelect={onPick}
                weekStartsOn={1}
                captionLayout="dropdown"
              />
              <div className="cal-actions">
                <button
                  className="btn"
                  onClick={() => {
                    const t = new Date();
                    setStartObj(t);
                    setStartDate(t.toISOString().slice(0, 10));
                  }}
                >
                  오늘
                </button>
              </div>
            </div>
          </div>

          {/* 오른쪽: 폼 */}
          <div>
            <div className="form-group">
              <span className="label">시작 날짜</span>
              <input type="text" className="input" value={startDate} readOnly />
            </div>


            <div className="form-group">
              <span className="label">주기</span>
              <select
                className="select"
                value={freq}
                onChange={(e) => setFreq(e.target.value as Frequency)}
              >
                <option value="DAILY">매일</option>
                <option value="WEEKLY">매주</option>
                <option value="MONTHLY">매월</option>
              </select>
            </div>

            <div className="form-group">
              <span className="label">시</span>
              <select
                className="select"
                value={hour}
                onChange={(e) => setHour(Number(e.target.value))}
              >
                {hours.map((h) => (
                  <option key={h} value={h}>
                    {String(h).padStart(2, "0")}시
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <span className="label">분</span>
              <select
                className="select"
                value={minute}
                onChange={(e) => setMinute(Number(e.target.value))}
              >
                {minutesArr.map((m) => (
                  <option key={m} value={m}>
                    {String(m).padStart(2, "0")}분
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        <div className="actions">
          <button className="btn" onClick={close}>취소</button>
          <button className="btn btn-primary" onClick={onSave} disabled={loading}>
            {loading ? "저장 중…" : "저장"}
          </button>
        </div>
      </div>
    </div>
  );
}

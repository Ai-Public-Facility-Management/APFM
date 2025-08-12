import { useState } from "react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";

import { saveInspectionSetting, type Frequency } from "../../api/inspection";
import { useScheduleModal } from "./ScheduleModalProvider";
import "./IntervalModal.css";

export default function IntervalModal() {
  // Provider에서 닫기 함수 사용 (라우터 X)
  const { close } = useScheduleModal();

  // 폼 상태
  const [facilityId, setFacilityId] = useState<number | "">("");
  // 달력(Date) + 서버로 보낼 문자열(YYYY-MM-DD) 동기화
  const [startObj, setStartObj] = useState<Date>(new Date());
  const [startDate, setStartDate] = useState<string>(() =>
    new Date().toISOString().slice(0, 10)
  );

  const [freq, setFreq] = useState<Frequency>("DAILY");
  const [hour, setHour] = useState(9);
  const [minute, setMinute] = useState(0);
  const [dow, setDow] = useState(1);   // WEEKLY: 월=1..일=7
  const [dom, setDom] = useState(1);   // MONTHLY: 1..28
  const [loading, setLoading] = useState(false);

  // 달력에서 날짜 선택
  const onPick = (d?: Date) => {
    if (!d) return;
    setStartObj(d);
    setStartDate(d.toISOString().slice(0, 10));
  };

  const onSave = async () => {
    if (facilityId === "") return alert("시설 ID를 입력하세요.");
    setLoading(true);
    try {
      const payload: any = {
        facilityId: Number(facilityId),
        frequency: freq,
        hour,
        minute,
        startAt: new Date(`${startDate}T00:00:00`).toISOString(),
        enabled: true,
        ...(freq === "WEEKLY" ? { dayOfWeek: dow } : {}),
        ...(freq === "MONTHLY" ? { dayOfMonth: dom } : {}),
      };
      await saveInspectionSetting(payload);
      alert("저장되었습니다.");
      close(); // 모달 닫기
    } catch (e) {
      console.error(e);
      alert("저장 실패: 서버 응답을 확인해주세요.");
    } finally {
      setLoading(false);
    }
  };

  // 배경 클릭 시 닫힘 방지
  const stop = (e: React.MouseEvent) => e.stopPropagation();

  return (
    <div className="modal-backdrop" onClick={close}>
      <div className="modal" onClick={stop}>
        <button className="modal-close" onClick={close}>✕</button>
        <h2 className="modal-title">점검 주기 설정</h2>

        <div className="modal-grid">
          {/* 왼쪽: 큰 달력 */}
          <div>
            <div className="calendar-card">
              <DayPicker
                mode="single"
                selected={startObj}
                onSelect={onPick}
                weekStartsOn={1}          // 월요일 시작
                captionLayout="dropdown"  // 년/월 드롭다운
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
                <div className="spacer" />
                <button className="btn" onClick={close}>취소</button>
                <button className="btn btn-primary" onClick={onSave} disabled={loading}>
                  {loading ? "저장 중…" : "선택"}
                </button>
              </div>
            </div>
          </div>

          {/* 오른쪽: 드롭다운/인풋 */}
          <div>
            <div className="form-group">
              <span className="label">시작 날짜</span>
              <input
                type="text"
                className="input"
                value={startDate}
                readOnly
              />
            </div>

            <div className="form-group">
              <span className="label">시설 ID</span>
              <input
                type="number"
                className="input"
                value={facilityId}
                onChange={(e) =>
                  setFacilityId(e.target.value === "" ? "" : Number(e.target.value))
                }
                placeholder="예: 42"
              />
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

            {freq === "WEEKLY" && (
              <div className="form-group">
                <span className="label">요일</span>
                <select
                  className="select"
                  value={dow}
                  onChange={(e) => setDow(Number(e.target.value))}
                >
                  <option value={1}>월</option>
                  <option value={2}>화</option>
                  <option value={3}>수</option>
                  <option value={4}>목</option>
                  <option value={5}>금</option>
                  <option value={6}>토</option>
                  <option value={7}>일</option>
                </select>
              </div>
            )}

            {freq === "MONTHLY" && (
              <div className="form-group">
                <span className="label">일(1~28)</span>
                <input
                  className="input"
                  type="number"
                  min={1}
                  max={28}
                  value={dom}
                  onChange={(e) => setDom(Number(e.target.value))}
                />
              </div>
            )}

            <div className="form-group">
              <span className="label">시</span>
              <input
                className="input"
                type="number"
                min={0}
                max={23}
                value={hour}
                onChange={(e) => setHour(Number(e.target.value))}
              />
            </div>

            <div className="form-group">
              <span className="label">분</span>
              <input
                className="input"
                type="number"
                min={0}
                max={59}
                value={minute}
                onChange={(e) => setMinute(Number(e.target.value))}
              />
            </div>
          </div>
        </div>

        {/* 하단 버튼 (추가로 원하면 유지) */}
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

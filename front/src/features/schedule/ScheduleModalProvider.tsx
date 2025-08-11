import { createContext, useContext, useState, ReactNode, lazy, Suspense } from "react";
import { createPortal } from "react-dom";

type OpenArgs = { facilityId?: number };
type Ctx = { open: (args?: OpenArgs) => void; close: () => void };

const C = createContext<Ctx | null>(null);
export const useScheduleModal = () => {
  const v = useContext(C);
  if (!v) throw new Error("ScheduleModalProvider missing");
  return v;
};

const IntervalModal = lazy(() => import("./IntervalModal")); // 네가 만든 모달

export function ScheduleModalProvider({ children }: { children: ReactNode }) {
  const [args, setArgs] = useState<OpenArgs | null>(null);
  const open = (a?: OpenArgs) => setArgs(a ?? {});
  const close = () => setArgs(null);

  return (
    <C.Provider value={{ open, close }}>
      {children}
      {args && createPortal(
        <Suspense fallback={null}>
          <IntervalModal />
        </Suspense>,
        document.body
      )}
    </C.Provider>
  );
}

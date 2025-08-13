import { api } from "./http";

/** ✅ 정기점검 대시보드 DTO */
export type InspectionDto = {
  inspectionId: number;
  inspectionDate: string;     // ISO 형식 문자열
  cameraName: string;
  publicFaType: string;
  issueType: string;
};

/** ✅ 시설물 대시보드 DTO */
export type PublicFaDto = {
  publicFaId: number;
  publicFaType: string;
  issueType: string;
  cameraName: string;
  isProcessing: boolean;
};

/** ✅ 정기점검 대시보드 조회 */
export async function fetchDashboardInspections(
  count = 5
): Promise<InspectionDto[]> {
  const { data } = await api.get("/api/inspection/dashboard", {
    params: { count },
  });

  const rows = (data as any)?.data ?? [];

  return Array.isArray(rows)
    ? rows.map((row: any) => ({
        inspectionId: row.inspectionId,
        inspectionDate: row.inspectionDate,
        cameraName: row.cameraName,
        publicFaType: row.publicFaType,
        issueType: row.issueType,
      }))
    : [];
}

/** ✅ 시설물 대시보드 조회 */
export async function fetchDashboardPublicFas(
  count = 5
): Promise<PublicFaDto[]> {
  const { data } = await api.get("/api/publicfa/dashboard", {
    params: { count },
  });

  const rows = (data as any)?.publicFas ?? [];

  return Array.isArray(rows)
    ? rows.map((row: any) => ({
        publicFaId: row.publicFaId,
        publicFaType: row.publicFaType,
        issueType: row.issueType,
        cameraName: row.cameraName,
        isProcessing: row.isProcessing,
      }))
    : [];
}

export interface Camera {
  latitude: number;
  longitude: number;
  location: string;  // ✅ 추가
}

/** ✅ 전체 카메라 목록 조회 */
export async function fetchAllCameras(): Promise<Camera[]> {
  const { data } = await api.get("/api/camera");

  const rows = (data as any)?.cameras ?? [];

  return Array.isArray(rows)
    ? rows.map((row: any) => ({
        latitude: row.latitude,
        longitude: row.longitude,
        location: row.location, // ✅ 여기에 추가!
      }))
    : [];
}

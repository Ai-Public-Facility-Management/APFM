import { api } from "./http"; // 네가 준 axios 설정 파일 경로
import { AxiosProgressEvent } from "axios";

export async function uploadResult(
    file: File,
    onProgress: (percent: number) => void
) {
    const formData = new FormData();
    formData.append("file", file);

    await api.post("/api/upload/result", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
        onUploadProgress: (e: AxiosProgressEvent) => {
            if (e.total) {
                const percent = Math.round((e.loaded * 100) / e.total);
                onProgress(percent);
            }
        }
    });
}
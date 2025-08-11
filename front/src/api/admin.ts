import { api } from "./http";

export interface PendingUser {
  email: string;
  username: string;
  department: string;
}

export const getPendingUsers = async (): Promise<PendingUser[]> => {
  const { data } = await api.get("/api/admin/pending");
  return data;
};

export const approveUser = async (email: string) => {
  await api.post(`/api/admin/approve/${email}`);
};

export const rejectUser = async (email: string) => {
  await api.post(`/api/admin/reject/${email}`);
};
import { api } from "./http";

export interface PendingUser {
  email: string;
  username: string;
  department: string;
}

export const getPendingUsers = async (): Promise<PendingUser[]> => {
  const { data } = await api.get("/admin/pending");
  return data;
};

export const approveUser = async (email: string) => {
  await api.post(`/admin/approve/${email}`);
};

export const rejectUser = async (email: string) => {
  await api.post(`/admin/reject/${email}`);
};
import axios from "axios";

export interface PendingUser {
  email: string;
  username: string;
  department: string;
}

export const getPendingUsers = async (): Promise<PendingUser[]> => {
  const response = await axios.get("http://localhost:8082/api/admin/pending");
  return response.data;
};

export const approveUser = async (email: string) => {
  await axios.post(`http://localhost:8082/api/admin/approve/${email}`);
};

export const rejectUser = async (email: string) => {
  await axios.post(`http://localhost:8082/api/admin/reject/${email}`);
};

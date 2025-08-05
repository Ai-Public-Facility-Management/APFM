INSERT INTO Users (
  email, password, username, department, type, approval_status, level
) VALUES (
  'test@example.com',
  '$2a$10$48DqaJuF.1En3hvUD7sKGue2zfGjDR7/DOLkjjoqWlScvwIv56WCS',
  '테스트유저',
  'DEVELOPMENT',
  'INSPECTOR',
  'APPROVED',
  1
);
INSERT INTO Inspection_Setting (
    Id,start_Date,start_Time,inspection_Cycle,address
) VALUES (
    1,
    '2028-08-05',
    '09:00',
    7,
    '주소값'
);



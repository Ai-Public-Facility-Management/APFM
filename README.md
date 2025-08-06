# Ai Public Facilities Management
data.spl에 아래 코드 추가
```
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
insert into INSPECTION values (TRUE,'2025-08-01',1);
insert into INSPECTION values (TRUE,'2025-08-02',2);
insert into CAMERA values (1,'1324','1324');
insert into ISSUE  values ('2025-08-01',1000,1,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,2,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,3,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,4,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,5,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,6,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,7,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,8,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,9,1,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,10,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,11,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,12,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,13,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,14,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,15,2,NULL,'1234','1234','1234','DAMAGE');
insert into ISSUE  values ('2025-08-01',1000,16,2,NULL,'1234','1234','1234','DAMAGE');
insert into PUBLIC_FA values (1,1,1,1,1,1,'2025-08-01',1,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,2,'2025-08-01',2,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,3,'2025-08-01',3,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,4,'2025-08-01',4,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,5,'2025-08-01',5,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,6,'2025-08-01',6,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,7,'2025-08-01',7,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,8,'2025-08-01',8,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,9,'2025-08-01',9,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,10,'2025-08-01',10,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,11,'2025-08-01',11,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,12,'2025-08-01',12,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,13,'2025-08-01',13,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,14,'2025-08-01',14,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,15,'2025-08-01',15,'2025-08-01' ,100,'ABNORMAL','BENCH');
insert into PUBLIC_FA values (1,1,1,1,1,16,'2025-08-01',16,'2025-08-01' ,100,'ABNORMAL','BENCH');


```

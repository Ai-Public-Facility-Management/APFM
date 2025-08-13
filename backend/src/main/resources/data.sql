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
    Id,start_Date,start_Time,inspection_Cycle
) VALUES (
    1,
    '2028-08-05',
    '09:00',
    7
);

-- insert into INSPECTION(id,CREATE_DATE ) values (1,'2025-08-01');
--
-- insert into CAMERA(id,ip,location,latitude,longitude) values (1,'testIP_1','부산역 5번출구',35.115012160083616,129.03920684658144);
--
--
-- INSERT INTO PUBLIC_FA(ID,INSTALL_DATE,LAST_REPAIR,OBSTRUCTION,SECTION_HEIGHT,  SECTION_WIDTH ,SECTION_X_CENTER ,SECTION_Y_CENTER, STATUS ,TYPE , CAMERA_ID ) VALUES (3,'2025-08-01','2025-08-01',100,1,1,1,1,'ABNORMAL','BENCH',1);

-- INSERT INTO issue (
--     id, content, creation_date, description, estimate, estimate_basis,
--     image_description, image_url, location, obstruction_level, result_id,
--     status, type, inspection_id, public_fa_id
-- ) VALUES
-- (2, 'Content 2', '2025-08-01', 'Description for TYPE_B', 20000, 'Basis B', 'Image desc B', 'http://example.com/image2.jpg', 'Busan', 3, 102, 'REPAIR', 'DAMAGE', 1, 3);




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

insert into INSPECTION(id,CREATE_DATE ) values (1,'2025-08-01');

insert into CAMERA(id,ip,location,latitude,longitude,IMAGE_DESCRIPTION,image_url) values (1,'testIP_1','부산역 5번출구',35.115012160083616,129.03920684658144,'image','test_img_url');

INSERT INTO PUBLIC_FA(ID,INSTALL_DATE,LAST_REPAIR,OBSTRUCTION,SECTION_HEIGHT,SECTION_WIDTH ,SECTION_X_CENTER ,SECTION_Y_CENTER, STATUS ,TYPE ,CAMERA_ID, IMAGE_DESCRIPTION,image_url) VALUES (1,'2025-08-01','2025-08-01',100,1,1,1,1,'ABNORMAL','BENCH',1,'image','test_img_url');

INSERT INTO issue (
    id,
    creation_date,
    estimate,
    estimate_basis,
    estimate_references,
    obstruction,
    obstruction_basis,
    result_id,
    status,
    type,
    vision_analysis,
    inspection_id,
    public_fa_id
) VALUES (
             1,
             '2025-08-13 14:30:00.000000',
             120000,
             '현장 조사 결과 기반',
             '설계 문서 #23',
             3,
             '도로 폐쇄 수준',
             101,
             'REPAIR',
             'DAMAGE',
             'AI 분석 결과 이상 없음',
             1,
             1
         );



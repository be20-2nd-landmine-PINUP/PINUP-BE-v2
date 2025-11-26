DELETE FROM territory_visit_log;
DELETE FROM territory;
DELETE FROM conquer_session;
DELETE FROM region;
DELETE FROM users;

INSERT INTO users (user_id, login_type, user_name, email, nickname, gender, profile_image, status,
                   created_at, updated_at, birth_date, preferred_category, preferred_season)
VALUES (1, 'GOOGLE', '정복러', 'conquer@example.com', 'conqueror', 'M', NULL, 'ACTIVE',
        NOW(), NOW(), '1990-01-01', '문화', '봄'),
       (2, 'GOOGLE', '다른유저', 'other@example.com', 'other', 'F', NULL, 'ACTIVE',
        NOW(), NOW(), '1992-02-02', '자연', '여름');

INSERT INTO region (region_id, region_name, region_depth1, region_depth2, region_depth3, geom)
VALUES (10, '서울특별시 성북구', '서울특별시', '성북구', '성북1동',
        ST_GeomFromText('POLYGON((0 0, 10 0, 10 10, 0 10, 0 0))', 4326));

INSERT INTO territory (territory_id, user_id, region_id, capture_start_at, capture_end_at, visit_count, photo_url)
VALUES
    (100, 1, 10,
     DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY),
     DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY),
     1, NULL),
    (101, 1, 10,
     DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY),
     DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY),
     2, NULL),
    (102, 1, 10,
     DATE_SUB(CURRENT_DATE(), INTERVAL 40 DAY),
     DATE_SUB(CURRENT_DATE(), INTERVAL 39 DAY),
     1, NULL),
    (103, 2, 10,
     DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),
     DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),
     1, NULL);

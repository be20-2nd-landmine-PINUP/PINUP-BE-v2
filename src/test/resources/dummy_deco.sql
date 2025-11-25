DELETE FROM territory_visit_log;
DELETE FROM territory;
DELETE FROM inventory;
DELETE FROM store;
DELETE FROM region;
DELETE FROM admin;
DELETE FROM users;

INSERT INTO users (user_id, login_type, user_name, email, nickname, gender, profile_image, status,
                   created_at, updated_at, birth_date, preferred_category, preferred_season)
VALUES (101, 'GOOGLE', '데코유저', 'deco@example.com', 'deco-user', 'M', NULL, 'ACTIVE',
        NOW(), NOW(), '1995-01-01', '문화', '봄');

INSERT INTO admin (admin_id, admin_name, admin_pw, status, created_at, updated_at)
VALUES (201, '관리자', 'password', 'ACTIVE', NOW(), NOW());

INSERT INTO region (region_id, region_name, region_depth1, region_depth2, region_depth3, geom)
VALUES (301, '서울특별시 성북구', '서울특별시', '성북구', '성북1동',
        ST_GeomFromText('POLYGON((0 0, 50 0, 50 50, 0 50, 0 0))', 4326));

INSERT INTO store (item_id, region_id, admin_id, name, description, price, category, limit_type,
                   image_url, is_active, created_at)
VALUES (401, 301, 201, '서울 마커', '서울 지역 마커', 1000, 'MARKER', 'NORMAL',
        'https://example.com/marker.png', TRUE, '2024-01-01 00:00:00'),
       (402, 301, 201, '서울 건물', '서울 지역 건물', 2000, 'BUILDING', 'EVENT',
        'https://example.com/building.png', TRUE, '2024-02-01 00:00:00');

INSERT INTO inventory (user_id, item_id, earned_at, is_equipped, equipped_coordinates)
VALUES (101, 401, '2024-03-01 10:00:00', FALSE, NULL),
       (101, 402, '2024-03-05 12:00:00', TRUE, ST_GeomFromText('POINT(126.98 37.58)', 4326));

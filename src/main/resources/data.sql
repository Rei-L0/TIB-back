-- Sido (Province) Data - Only Busan
INSERT INTO sido (sido_code, sido_name) VALUES (6, '부산');

-- Gugun (District) Data - Busan (sido_code = 6)
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (101, '강서구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (102, '금정구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (103, '기장군', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (104, '남구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (105, '동구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (106, '동래구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (107, '부산진구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (108, '북구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (109, '사상구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (110, '사하구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (111, '서구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (112, '수영구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (113, '연제구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (114, '영도구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (115, '중구', 6);
INSERT INTO gugun (gugun_code, gugun_name, sido_code) VALUES (116, '해운대구', 6);

-- Content Type Data
INSERT INTO content_type (content_type_id, content_type_name) VALUES (12, '관광지');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (14, '문화시설');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (15, '축제공연행사');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (25, '여행코스');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (28, '레포츠');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (32, '숙박');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (38, '쇼핑');
INSERT INTO content_type (content_type_id, content_type_name) VALUES (39, '음식점');

-- Attraction Info Data (5 records)
-- 1. Haeundae Beach
INSERT INTO attraction_info (content_id, content_type_id, gugun_code, sido_code, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, latitude, longitude, mlevel) 
VALUES (1001, 12, 116, 6, '해운대 해수욕장', '부산광역시 해운대구 해운대해변로 264', '', '48099', '051-749-7611', 'http://tong.visitkorea.or.kr/cms/resource/67/2612467_image2_1.jpg', 'http://tong.visitkorea.or.kr/cms/resource/67/2612467_image2_1.jpg', 15000, 35.158698, 129.160384, '6');

-- 2. Gwangalli Beach
INSERT INTO attraction_info (content_id, content_type_id, gugun_code, sido_code, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, latitude, longitude, mlevel) 
VALUES (1002, 12, 112, 6, '광안리 해수욕장', '부산광역시 수영구 광안해변로 219', '', '48303', '051-610-4000', 'http://tong.visitkorea.or.kr/cms/resource/75/2612475_image2_1.jpg', 'http://tong.visitkorea.or.kr/cms/resource/75/2612475_image2_1.jpg', 12000, 35.153169, 129.118666, '6');

-- 3. Taejongdae
INSERT INTO attraction_info (content_id, content_type_id, gugun_code, sido_code, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, latitude, longitude, mlevel) 
VALUES (1003, 12, 114, 6, '태종대', '부산광역시 영도구 전망로 24', '', '49129', '051-405-2004', 'http://tong.visitkorea.or.kr/cms/resource/78/2612478_image2_1.jpg', 'http://tong.visitkorea.or.kr/cms/resource/78/2612478_image2_1.jpg', 8000, 35.053049, 129.088190, '6');

-- 4. Jagalchi Market
INSERT INTO attraction_info (content_id, content_type_id, gugun_code, sido_code, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, latitude, longitude, mlevel) 
VALUES (1004, 38, 115, 6, '자갈치 시장', '부산광역시 중구 자갈치해안로 52', '', '48984', '051-245-2594', 'http://tong.visitkorea.or.kr/cms/resource/88/2612488_image2_1.jpg', 'http://tong.visitkorea.or.kr/cms/resource/88/2612488_image2_1.jpg', 9500, 35.096752, 129.030588, '6');

-- 5. Gamcheon Culture Village
INSERT INTO attraction_info (content_id, content_type_id, gugun_code, sido_code, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, latitude, longitude, mlevel) 
VALUES (1005, 12, 110, 6, '감천문화마을', '부산광역시 사하구 감내2로 203', '', '49368', '051-204-1444', 'http://tong.visitkorea.or.kr/cms/resource/90/2612490_image2_1.jpg', 'http://tong.visitkorea.or.kr/cms/resource/90/2612490_image2_1.jpg', 18000, 35.097457, 129.010609, '6');


-- Attraction Detail Data
INSERT INTO attraction_detail (content_id, cat1, cat2, cat3, created_time, modified_time, booktour)
VALUES (1001, 'A01', 'A0101', 'A01010100', '20230101120000', '20231201120000', '');

INSERT INTO attraction_detail (content_id, cat1, cat2, cat3, created_time, modified_time, booktour)
VALUES (1002, 'A01', 'A0101', 'A01010100', '20230101120000', '20231201120000', '');

INSERT INTO attraction_detail (content_id, cat1, cat2, cat3, created_time, modified_time, booktour)
VALUES (1003, 'A01', 'A0101', 'A01010200', '20230101120000', '20231201120000', '');

INSERT INTO attraction_detail (content_id, cat1, cat2, cat3, created_time, modified_time, booktour)
VALUES (1004, 'A04', 'A0401', 'A04010600', '20230101120000', '20231201120000', '');

INSERT INTO attraction_detail (content_id, cat1, cat2, cat3, created_time, modified_time, booktour)
VALUES (1005, 'A02', 'A0206', 'A02060100', '20230101120000', '20231201120000', '');


-- Attraction Description Data
INSERT INTO attraction_description (content_id, homepage, overview, telname)
VALUES (1001, 'http://sun-n-fun.haeundae.go.kr', '해운대 해수욕장은 부산을 대표하는 명소로...', '해운대구청');

INSERT INTO attraction_description (content_id, homepage, overview, telname)
VALUES (1002, 'http://www.suyeong.go.kr', '광안리 해수욕장은 광안대교의 야경이 아름다운...', '수영구청');

INSERT INTO attraction_description (content_id, homepage, overview, telname)
VALUES (1003, 'http://taejongdae.bisco.or.kr', '태종대는 기암괴석과 바다가 어우러진...', '태종대유원지');

INSERT INTO attraction_description (content_id, homepage, overview, telname)
VALUES (1004, 'http://jagalchimarket.bisco.or.kr', '부산의 대표적인 어시장으로...', '자갈치시장');

INSERT INTO attraction_description (content_id, homepage, overview, telname)
VALUES (1005, 'http://www.gamcheon.or.kr', '감천문화마을은 알록달록한 집들이...', '사하구청');


-- Hashtag Data
INSERT INTO hashtag (id, name) VALUES (1, '여행');
INSERT INTO hashtag (id, name) VALUES (2, '부산');
INSERT INTO hashtag (id, name) VALUES (3, '바다');
INSERT INTO hashtag (id, name) VALUES (4, '야경');
INSERT INTO hashtag (id, name) VALUES (5, '맛집');


-- Shorts Data (Explicit IDs for easy linking)
INSERT INTO shorts (id, name, title, video, created_at, longitude, latitude, good, readcount, content_id, hls_master_url, thumbnail_url, status)
VALUES (1, '해운대 브이로그', '해운대 시원한 바다', 'https://example.com/videos/haeundae.mp4', NOW(), 129.160384, 35.158698, 120, 1500, 1001, 'https://example.com/hls/haeundae.m3u8', 'https://example.com/thumbnails/haeundae.jpg', 'PROCESSED');

INSERT INTO shorts (id, name, title, video, created_at, longitude, latitude, good, readcount, content_id, hls_master_url, thumbnail_url, status)
VALUES (2, '광안리 야경', '광안대교 불꽃놀이', 'https://example.com/videos/gwangalli.mp4', NOW(), 129.118666, 35.153169, 350, 4200, 1002, 'https://example.com/hls/gwangalli.m3u8', 'https://example.com/thumbnails/gwangalli.jpg', 'PROCESSED');

INSERT INTO shorts (id, name, title, video, created_at, longitude, latitude, good, readcount, content_id, hls_master_url, thumbnail_url, status)
VALUES (3, '시장투어', '자갈치 시장 먹방', 'https://example.com/videos/jagalchi.mp4', NOW(), 129.030588, 35.096752, 50, 300, 1004, 'https://example.com/hls/jagalchi.m3u8', 'https://example.com/thumbnails/jagalchi.jpg', 'PROCESSED');

INSERT INTO shorts (id, name, title, video, created_at, longitude, latitude, good, readcount, content_id, hls_master_url, thumbnail_url, status)
VALUES (4, '감천마을 산책', '어린왕자와 함께', 'https://example.com/videos/gamcheon.mp4', NOW(), 129.010609, 35.097457, 210, 2000, 1005, 'https://example.com/hls/gamcheon.m3u8', 'https://example.com/thumbnails/gamcheon.jpg', 'PROCESSED');

INSERT INTO shorts (id, name, title, video, created_at, longitude, latitude, good, readcount, content_id, hls_master_url, thumbnail_url, status)
VALUES (5, '부산 여행 하이라이트', '부산 명소 모음zip', 'https://example.com/videos/busan_highlight.mp4', NOW(), 129.0556277, 35.1379222, 500, 10000, NULL, 'https://example.com/hls/busan.m3u8', 'https://example.com/thumbnails/busan.jpg', 'PROCESSED');


-- Shorts Metadata Data
INSERT INTO shorts_metadata (shorts_id, weather, theme, season, date_time_original)
VALUES (1, 'Sunny', 'Beach', 'Summer', '2023-07-20 14:00:00');

INSERT INTO shorts_metadata (shorts_id, weather, theme, season, date_time_original)
VALUES (2, 'Clear', 'NightView', 'Autumn', '2023-10-05 20:00:00');

INSERT INTO shorts_metadata (shorts_id, weather, theme, season, date_time_original)
VALUES (3, 'Cloudy', 'Food', 'Winter', '2023-12-15 11:00:00');

INSERT INTO shorts_metadata (shorts_id, weather, theme, season, date_time_original)
VALUES (4, 'Sunny', 'Walking', 'Spring', '2024-04-10 15:00:00');

INSERT INTO shorts_metadata (shorts_id, weather, theme, season, date_time_original)
VALUES (5, 'Mixed', 'Travel', 'All', '2024-01-01 10:00:00');


-- Shorts Hashtag Data
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (1, 2); -- Haeundae - Busan
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (1, 3); -- Haeundae - Sea
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (2, 2); -- Gwangalli - Busan
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (2, 4); -- Gwangalli - NightView
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (3, 2); -- Jagalchi - Busan
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (3, 5); -- Jagalchi - Food
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (4, 2); -- Gamcheon - Busan
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (4, 1); -- Gamcheon - Travel
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (5, 1); -- Highlight - Travel
INSERT INTO shorts_hashtag (shorts_id, hashtag_id) VALUES (5, 2); -- Highlight - Busan


-- Shorts Like Data
INSERT INTO shorts_like (shorts_id, user_identifier, created_at) VALUES (1, 'user_001', NOW());
INSERT INTO shorts_like (shorts_id, user_identifier, created_at) VALUES (2, 'user_002', NOW());
INSERT INTO shorts_like (shorts_id, user_identifier, created_at) VALUES (2, 'user_001', NOW());
INSERT INTO shorts_like (shorts_id, user_identifier, created_at) VALUES (3, 'user_003', NOW());
INSERT INTO shorts_like (shorts_id, user_identifier, created_at) VALUES (5, 'user_004', NOW());


-- Shorts Play Event Data
INSERT INTO shorts_play_event (shorts_id, user_identifier, watch_time_sec, created_at) VALUES (1, 'user_001', 30, NOW());
INSERT INTO shorts_play_event (shorts_id, user_identifier, watch_time_sec, created_at) VALUES (1, 'user_002', 60, NOW());
INSERT INTO shorts_play_event (shorts_id, user_identifier, watch_time_sec, created_at) VALUES (2, 'user_002', 120, NOW());
INSERT INTO shorts_play_event (shorts_id, user_identifier, watch_time_sec, created_at) VALUES (4, 'user_003', 45, NOW());
INSERT INTO shorts_play_event (shorts_id, user_identifier, watch_time_sec, created_at) VALUES (5, 'user_004', 300, NOW());

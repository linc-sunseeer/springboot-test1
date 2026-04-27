-- 玉子屋本週菜單資料（2026-04-20 ~ 04-24）
-- 來源：https://www.tamagoya.co.jp/menu_list/#weeks-menu

INSERT INTO menus (name, description, price, image_url, available_date, calorie, allergens, is_deleted, created_at, updated_at)
SELECT '大人気！ハンバーグトマトソース',
       'オニオンリング＆ハッシュポテト添え。野菜と玉子の炒め物、ポテトサラダ、スパゲティ、鮭フレーク、お漬物付き。',
       650, 'https://www.tamagoya.co.jp/wp-content/uploads/2026/04/0420.jpg', '2026-04-20', 750, '小麦,卵,乳', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menus WHERE available_date = '2026-04-20' AND name LIKE '%ハンバーグトマト%');

INSERT INTO menus (name, description, price, image_url, available_date, calorie, allergens, is_deleted, created_at, updated_at)
SELECT 'ブラックカレーコロッケ',
       'ウインナーと大根のポトフ風。さば塩焼、ぜんまい田舎煮、かぼちゃサラダ、お漬物、千切キャベツ付き。',
       680, 'https://www.tamagoya.co.jp/wp-content/uploads/2026/04/0421.jpg', '2026-04-21', 720, '小麦,卵', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menus WHERE available_date = '2026-04-21' AND name LIKE '%ブラックカレー%');

INSERT INTO menus (name, description, price, image_url, available_date, calorie, allergens, is_deleted, created_at, updated_at)
SELECT '豚ロースの特製チャーシューだれ',
       'ごはんがすすむ麻婆豆腐。アジ大葉フライ、切干大根サラダ、おさつの胡麻マヨ、お漬物、千切キャベツ付き。',
       720, 'https://www.tamagoya.co.jp/wp-content/uploads/2026/04/0422.jpg', '2026-04-22', 780, '小麦,大豆,ごま', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menus WHERE available_date = '2026-04-22' AND name LIKE '%チャーシュー%');

INSERT INTO menus (name, description, price, image_url, available_date, calorie, allergens, is_deleted, created_at, updated_at)
SELECT 'さっくさく！チキンカツ',
       '胡麻とんかつソース。ほっけ塩焼、厚揚げのカレー煮、野菜サラダ、ひよこ豆煮、浅漬白菜、千切キャベツ付き。',
       750, 'https://www.tamagoya.co.jp/wp-content/uploads/2026/04/0423.jpg', '2026-04-23', 800, '小麦,卵', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menus WHERE available_date = '2026-04-23' AND name LIKE '%チキンカツ%');

INSERT INTO menus (name, description, price, image_url, available_date, calorie, allergens, is_deleted, created_at, updated_at)
SELECT '豆腐ハンバーグ和風おろしソース',
       '鶏肉とナスのトマト煮。野菜コロッケ、小松菜煮びたし、胡麻キクラゲ佃煮、お漬物、千切キャベツ付き。',
       690, 'https://www.tamagoya.co.jp/wp-content/uploads/2026/04/0424.jpg', '2026-04-24', 700, '小麦,卵,乳,大豆', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM menus WHERE available_date = '2026-04-24' AND name LIKE '%豆腐ハンバーグ%');

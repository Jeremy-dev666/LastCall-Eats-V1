-- LastCall Eats dev seed data, executed by DataSeeder (dev profile only).
-- All account passwords: 111111 (BCrypt strength=10)

-- =====================
-- Clear all data (respect FK order)
-- =====================
TRUNCATE TABLE review CASCADE;
TRUNCATE TABLE post CASCADE;
TRUNCATE TABLE pickup_code CASCADE;
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE product_listing CASCADE;
TRUNCATE TABLE product_template CASCADE;
TRUNCATE TABLE user_favorite CASCADE;
TRUNCATE TABLE merchant CASCADE;
TRUNCATE TABLE "user" CASCADE;

-- =====================
-- Users (password: 111111)
-- =====================
INSERT INTO "user" (id, email, password_hash, nickname) OVERRIDING SYSTEM VALUE VALUES
(1, 'alice@example.com',   '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Alice'),
(2, 'bob@example.com',     '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Bob'),
(3, 'charlie@example.com', '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Charlie');
SELECT setval(pg_get_serial_sequence('"user"', 'id'), 3);

-- =====================
-- Merchants (password: 111111)
-- =====================
INSERT INTO merchant (id, email, password_hash, name, address, business_hours) OVERRIDING SYSTEM VALUE VALUES
(1, 'bakery@example.com', '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Golden Bakery',    '123 Main St, Boston, MA',    '08:00-20:00'),
(2, 'sushi@example.com',  '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Sakura Sushi',     '456 Elm St, Boston, MA',     '11:00-22:00'),
(3, 'cafe@example.com',   '$2a$10$lwTs9A3cEkFqSItPrHiPmeN3zcEg2zzs7VNxXUgrdKklpJs2gVc4.', 'Brew & Bite Cafe', '789 Oak Ave, Cambridge, MA', '07:00-18:00');
SELECT setval(pg_get_serial_sequence('merchant', 'id'), 3);

-- =====================
-- Product templates (merchant_id 1=Bakery, 2=Sushi, 3=Cafe)
-- =====================
INSERT INTO product_template (id, merchant_id, name, description, original_price) OVERRIDING SYSTEM VALUE VALUES
(1, 1, 'Sourdough Bread',   'Freshly baked sourdough loaf',       8.00),
(2, 1, 'Croissant Box',     'Assorted croissants x6',             15.00),
(3, 1, 'Muffin Pack',       'Blueberry and chocolate muffins x4', 10.00),
(4, 2, 'Salmon Roll Set',   '8-piece salmon roll',                18.00),
(5, 2, 'Bento Box',         'Mixed sushi bento with miso soup',   22.00),
(6, 2, 'Sashimi Platter',   'Chef selection sashimi x12',         28.00),
(7, 3, 'Coffee & Sandwich', 'Latte + club sandwich combo',        14.00),
(8, 3, 'Pastry Bundle',     'Danish pastry x3 + drip coffee',     12.00),
(9, 3, 'Salad Bowl',        'Caesar salad with grilled chicken',  13.00);
SELECT setval(pg_get_serial_sequence('product_template', 'id'), 9);

-- =====================
-- Product listings (today and tomorrow, evening pickup)
-- =====================
INSERT INTO product_listing (id, merchant_id, template_id, discount_price, quantity, remaining_quantity, pickup_start, pickup_end, date) OVERRIDING SYSTEM VALUE VALUES
(1, 1, 1, 4.00,  5, 5, '17:00', '19:00', CURRENT_DATE),
(2, 1, 2, 8.00,  3, 3, '17:00', '19:00', CURRENT_DATE),
(3, 1, 3, 5.00,  4, 4, '17:00', '19:00', CURRENT_DATE + INTERVAL '1 day'),
(4, 2, 4, 10.00, 4, 4, '20:00', '21:30', CURRENT_DATE),
(5, 2, 5, 12.00, 3, 3, '20:00', '21:30', CURRENT_DATE),
(6, 2, 6, 15.00, 2, 2, '20:00', '21:30', CURRENT_DATE + INTERVAL '1 day'),
(7, 3, 7, 7.00,  6, 6, '16:00', '18:00', CURRENT_DATE),
(8, 3, 8, 6.00,  5, 5, '16:00', '18:00', CURRENT_DATE),
(9, 3, 9, 7.00,  4, 4, '16:00', '18:00', CURRENT_DATE + INTERVAL '1 day');
SELECT setval(pg_get_serial_sequence('product_listing', 'id'), 9);

-- =====================
-- Completed orders (for review seed)
-- =====================
INSERT INTO orders (id, user_id, listing_id, merchant_id, price, status, created_at, updated_at) OVERRIDING SYSTEM VALUE VALUES
(1, 1, 1, 1, 4.00,  'COMPLETED', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(2, 2, 1, 1, 4.00,  'COMPLETED', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(3, 3, 4, 2, 10.00, 'COMPLETED', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(4, 1, 4, 2, 10.00, 'COMPLETED', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(5, 2, 7, 3, 7.00,  'COMPLETED', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
(6, 3, 7, 3, 7.00,  'COMPLETED', NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day');
SELECT setval(pg_get_serial_sequence('orders', 'id'), 6);

-- =====================
-- Pickup codes (one per order)
-- =====================
INSERT INTO pickup_code (id, order_id, numeric_code, qr_code, used) OVERRIDING SYSTEM VALUE VALUES
(1, 1, '123401', 'ORDER:1', TRUE),
(2, 2, '123402', 'ORDER:2', TRUE),
(3, 3, '123403', 'ORDER:3', TRUE),
(4, 4, '123404', 'ORDER:4', TRUE),
(5, 5, '123405', 'ORDER:5', TRUE),
(6, 6, '123406', 'ORDER:6', TRUE);
SELECT setval(pg_get_serial_sequence('pickup_code', 'id'), 6);

-- =====================
-- Reviews
-- =====================
INSERT INTO review (id, order_id, user_id, merchant_id, template_id, rating, content, is_visible, created_at, updated_at) OVERRIDING SYSTEM VALUE VALUES
(1, 1, 1, 1, 1, 5, 'Amazing sourdough, perfectly crusty outside and soft inside. Great value!',    TRUE, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(2, 2, 2, 1, 1, 4, 'Really good bread for the price. Will definitely pick up again.',              TRUE, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(3, 3, 3, 2, 4, 5, 'Super fresh salmon rolls. Could not believe this was a discount deal!',        TRUE, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(4, 4, 1, 2, 4, 4, 'Solid sushi, fresh fish and good portion size.',                               TRUE, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(5, 5, 2, 3, 7, 5, 'Best coffee and sandwich combo in Cambridge. The latte was perfect.',          TRUE, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
(6, 6, 3, 3, 7, 3, 'Sandwich was okay, a bit dry. Coffee was good though.',                        TRUE, NOW() - INTERVAL '1 day',  NOW() - INTERVAL '1 day');
SELECT setval(pg_get_serial_sequence('review', 'id'), 6);

-- =====================
-- Community posts
-- =====================
INSERT INTO post (id, user_id, merchant_id, content, image_urls, like_count, comment_count, is_visible, created_at, updated_at) OVERRIDING SYSTEM VALUE VALUES
(1, 1, 1,    'Just picked up the sourdough bread from Golden Bakery — absolutely worth it at half price!', NULL, 3, 1, TRUE, NOW(), NOW()),
(2, 2, 2,    'Sakura Sushi never disappoints. Got the salmon roll set for $10, fresh and delicious.',      NULL, 5, 2, TRUE, NOW(), NOW()),
(3, 3, 3,    'Brew & Bite Cafe has the best morning deal. Coffee + sandwich for $7, can''t beat that.',    NULL, 2, 0, TRUE, NOW(), NOW()),
(4, 1, NULL, 'Anyone else been saving so much money using LastCall? Picked up 3 meals this week under $20 total!', NULL, 8, 3, TRUE, NOW(), NOW()),
(5, 2, 1,    'Golden Bakery croissant box is a steal. Got 6 croissants for $8, froze half of them.',      NULL, 4, 1, TRUE, NOW(), NOW()),
(6, 3, 2,    'The bento box from Sakura Sushi was huge. Definitely coming back tomorrow if they list again.', NULL, 6, 2, TRUE, NOW(), NOW());
SELECT setval(pg_get_serial_sequence('post', 'id'), 6);

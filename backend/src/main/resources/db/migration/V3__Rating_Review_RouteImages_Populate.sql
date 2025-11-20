

INSERT INTO route_images (id, image_url, route_id)
VALUES
    (1, 'Biking - Molly at Britton Peak - VCC UL - by Al & Lyndsey Johnson  (42).jpg', 1),
    (2, 'images (1).jpeg', 7),
    (3, 'Biking - Molly at Britton Peak - VCC UL - by Al & Lyndsey Johnson  (42).jpg', 7),
    (4, 'images (1).jpeg', 2),
    (5, 'images (1).jpeg', 3),
    (6, 'images (1).jpeg', 4),
    (7, 'Biking - Molly at Britton Peak - VCC UL - by Al & Lyndsey Johnson  (42).jpg', 5),
    (8, 'Biking - Molly at Britton Peak - VCC UL - by Al & Lyndsey Johnson  (42).jpg', 6),
    (9, 'Biking - Molly at Britton Peak - VCC UL - by Al & Lyndsey Johnson  (42).jpg', 7)
ON CONFLICT (id) DO NOTHING;


INSERT INTO review (review, created_by_user_id, created_at, updated_at, route_id)
VALUES
    ('Amazing mountain views and a rewarding climb!', 1, NOW(), NOW(), 1),
    ('Great trail for biking, well maintained paths.', 1, NOW(), NOW(), 2),
    ('Beautiful river scenery but can get muddy after rain.', 1, NOW(), NOW(), 3),
    ('Perfect for families — easy and shaded most of the way.', 1, NOW(), NOW(), 4),
    ('Urban ride with good coffee stops along the route.', 1, NOW(), NOW(), 5),
    ('Loved the desert landscapes and sunset views.', 1, NOW(), NOW(), 6),
    ('Nice lakeside path, quiet and relaxing.', 1, NOW(), NOW(), 7);


INSERT INTO rating (rating, created_by_user_id, created_at, updated_at, route_id)
VALUES
    (5.0, 1, NOW(), NOW(), 1),
    (4.5, 1, NOW(), NOW(), 2),
    (4.0, 1, NOW(), NOW(), 3),
    (5.0, 1, NOW(), NOW(), 4),
    (2.5, 1, NOW(), NOW(), 5),
    (1.0, 1, NOW(), NOW(), 6),
    (0.5, 1, NOW(), NOW(), 7);



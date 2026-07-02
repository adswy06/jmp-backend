INSERT INTO m_activity (id, category_name, name) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Loading', 'Loading Activity'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Unloading', 'Unloading Activity'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Transit', 'Transit Activity'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Customs', 'Customs Activity')
ON CONFLICT (id) DO NOTHING;

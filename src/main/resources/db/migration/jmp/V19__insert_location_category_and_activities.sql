-- Migration: V19__insert_location_category_and_activities.sql
-- Description: Seed JMP database with Location Categories, Activities, mapping, and their default costs/leadtimes.

-- 1. Insert Location Categories
INSERT INTO m_location_category (id, category_name, description, lastsync) VALUES
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c7', 'Pabrik / Shipper', 'Tempat asal pengiriman barang dari pabrik/shipper', CURRENT_TIMESTAMP),
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c8', 'Pelabuhan / Port', 'Pelabuhan laut untuk loading/unloading container', CURRENT_TIMESTAMP),
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c9', 'Gudang / Warehouse', 'Gudang penyimpanan atau pusat distribusi', CURRENT_TIMESTAMP)
ON CONFLICT (category_name) DO UPDATE 
SET description = EXCLUDED.description, lastsync = EXCLUDED.lastsync;

-- 2. Insert Activities
INSERT INTO m_activity (id, category_name, name) VALUES
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1', 'Loading', 'Pemuatan Barang'),
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c2', 'Unloading', 'Pembongkaran Barang'),
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c3', 'Customs', 'Kepabeanan'),
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c4', 'Weighing', 'Timbang Masuk'),
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c5', 'Weighing', 'Timbang Keluar'),
('a0f865f8-8bb8-4ecf-9177-d64e9e03d3c6', 'Administration', 'Administrasi Dokumen')
ON CONFLICT (id) DO UPDATE 
SET category_name = EXCLUDED.category_name, name = EXCLUDED.name;

-- 3. Map Location Categories to Activities
INSERT INTO zrel_location_category_activity (location_category_id, activity_id) VALUES
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c7', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1'), -- Pabrik -> Pemuatan Barang
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c7', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c4'), -- Pabrik -> Timbang Masuk
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c7', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c5'), -- Pabrik -> Timbang Keluar
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c7', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c6'), -- Pabrik -> Administrasi Dokumen

('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c9', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c2'), -- Gudang -> Pembongkaran Barang
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c9', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1'), -- Gudang -> Pemuatan Barang
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c9', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c6'), -- Gudang -> Administrasi Dokumen

('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c8', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1'), -- Pelabuhan -> Pemuatan Barang
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c8', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c2'), -- Pelabuhan -> Pembongkaran Barang
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c8', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c3'), -- Pelabuhan -> Kepabeanan
('c0f865f8-8bb8-4ecf-9177-d64e9e03d3c8', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c6')  -- Pelabuhan -> Administrasi Dokumen
ON CONFLICT (location_category_id, activity_id) DO NOTHING;

-- 4. Create Mock Route
INSERT INTO m_route (id, name, alias, isactive, isdeleted) VALUES
('r0f865f8-8bb8-4ecf-9177-d64e9e03d301', 'Rute Jakarta - Surabaya', 'JKT-SUB', TRUE, FALSE)
ON CONFLICT (id) DO NOTHING;

-- 5. Create Mock POIs
INSERT INTO m_poi (id, name, location_category, isactive) VALUES
('p0f865f8-8bb8-4ecf-9177-d64e9e03d311', 'Pabrik Jakarta Barat', 'Pabrik / Shipper', TRUE),
('p0f865f8-8bb8-4ecf-9177-d64e9e03d312', 'Pelabuhan Tanjung Priok JKT', 'Pelabuhan / Port', TRUE),
('p0f865f8-8bb8-4ecf-9177-d64e9e03d313', 'Gudang Surabaya Margomulyo', 'Gudang / Warehouse', TRUE)
ON CONFLICT (id) DO NOTHING;

-- 6. Create Mock Route Points
INSERT INTO m_route_point (id, ref_id_route, poi_id, seqno, alias, iszone) VALUES
('rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'r0f865f8-8bb8-4ecf-9177-d64e9e03d301', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d311', 1, 'Pabrik Jkt', FALSE),
('rp0865f8-8bb8-4ecf-9177-d64e9e03d322', 'r0f865f8-8bb8-4ecf-9177-d64e9e03d301', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d312', 2, 'Tanjung Priok', FALSE),
('rp0865f8-8bb8-4ecf-9177-d64e9e03d323', 'r0f865f8-8bb8-4ecf-9177-d64e9e03d301', 'p0f865f8-8bb8-4ecf-9177-d64e9e03d313', 3, 'Gudang Sub', FALSE)
ON CONFLICT (id) DO NOTHING;

-- 7. Insert Activity Cost
INSERT INTO m_activity_cost (id, route_point_id, activity_id, amount) VALUES
('c0f865f8-8bb8-4ecf-9177-d64e9e03d331', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1', 150000.00), -- Loading at Pabrik Jkt
('c0f865f8-8bb8-4ecf-9177-d64e9e03d332', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c4', 50000.00),  -- Weigh-in at Pabrik Jkt
('c0f865f8-8bb8-4ecf-9177-d64e9e03d333', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c5', 50000.00),  -- Weigh-out at Pabrik Jkt
('c0f865f8-8bb8-4ecf-9177-d64e9e03d334', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d322', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c3', 500000.00), -- Customs at Tanjung Priok
('c0f865f8-8bb8-4ecf-9177-d64e9e03d335', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d323', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c2', 120000.00)  -- Unloading at Gudang Sub
ON CONFLICT (id) DO UPDATE 
SET amount = EXCLUDED.amount;

-- 8. Insert Activity Lead Time (in minutes)
INSERT INTO m_activity_lead_time (id, route_point_id, activity_id, leadtime) VALUES
('t0f865f8-8bb8-4ecf-9177-d64e9e03d341', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c1', 120), -- Loading at Pabrik Jkt (120 mins)
('t0f865f8-8bb8-4ecf-9177-d64e9e03d342', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c4', 15),  -- Weigh-in at Pabrik Jkt (15 mins)
('t0f865f8-8bb8-4ecf-9177-d64e9e03d343', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d321', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c5', 15),  -- Weigh-out at Pabrik Jkt (15 mins)
('t0f865f8-8bb8-4ecf-9177-d64e9e03d344', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d322', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c3', 180), -- Customs at Tanjung Priok (180 mins)
('t0f865f8-8bb8-4ecf-9177-d64e9e03d345', 'rp0865f8-8bb8-4ecf-9177-d64e9e03d323', 'a0f865f8-8bb8-4ecf-9177-d64e9e03d3c2', 90)   -- Unloading at Gudang Sub (90 mins)
ON CONFLICT (id) DO UPDATE 
SET leadtime = EXCLUDED.leadtime;

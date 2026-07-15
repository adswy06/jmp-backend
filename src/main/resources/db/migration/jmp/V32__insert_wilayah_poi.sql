-- Migration: V32__insert_wilayah_poi.sql
-- Description: Seed m_poi with official coordinates populated from wilayah.provinsi (DKI Jakarta) and wilayah.kabupaten (Jakarta Utara).

-- 1. Insert DKI Jakarta Province POI (kode_prov: 31)
INSERT INTO m_poi (id, name, address, lng, lat, region_code, location_category, isactive, createdby, createdat)
SELECT 
    'poi-wilayah-prov-dki', 
    'DKI Jakarta Province POI', 
    'Provinsi DKI Jakarta, Indonesia',
    ST_X(ST_Centroid(geom)), 
    ST_Y(ST_Centroid(geom)), 
    '31', 
    'Pelabuhan / Port',
    TRUE, 
    'SYSTEM', 
    NOW()
FROM wilayah.provinsi 
WHERE kode_prov = '31'
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Jakarta Utara Regency POI (kode_kab: 3172)
INSERT INTO m_poi (id, name, address, lng, lat, region_code, location_category, isactive, createdby, createdat)
SELECT 
    'poi-wilayah-kab-jakut', 
    'Jakarta Utara Regency POI', 
    'Kota Jakarta Utara, DKI Jakarta, Indonesia',
    ST_X(ST_Centroid(geom)), 
    ST_Y(ST_Centroid(geom)), 
    '3172', 
    'Pelabuhan / Port', 
    TRUE, 
    'SYSTEM', 
    NOW()
FROM wilayah.kabupaten 
WHERE kode_kab = '3172'
ON CONFLICT (id) DO NOTHING;

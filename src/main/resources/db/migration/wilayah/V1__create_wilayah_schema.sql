CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SCHEMA IF NOT EXISTS wilayah;

-- 1. Provinsi
CREATE TABLE IF NOT EXISTS wilayah.provinsi (
    id SERIAL PRIMARY KEY,
    kode_prov character varying(2) NOT NULL UNIQUE,
    nama_provinsi character varying(100) NOT NULL,
    geom public.geometry(MultiPolygon,4326) NOT NULL,
    area_km2 numeric(12,4),
    created_at timestamp without time zone DEFAULT now(),
    jumlah_penduduk integer,
    jumlah_kk integer,
    jumlah_kab integer,
    jumlah_kota integer,
    jumlah_kec integer,
    jumlah_desa integer,
    jumlah_kel integer,
    kepadatan numeric,
    luas_wilayah numeric,
    updated_at timestamp without time zone DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_provinsi_geom ON wilayah.provinsi USING gist (geom);

-- 2. Kabupaten
CREATE TABLE IF NOT EXISTS wilayah.kabupaten (
    id SERIAL PRIMARY KEY,
    kode_kab character varying(4) NOT NULL UNIQUE,
    kode_prov character varying(2) NOT NULL REFERENCES wilayah.provinsi(kode_prov),
    nama_kabupaten character varying(100) NOT NULL,
    tipe character varying(15) NOT NULL CHECK (tipe IN ('KABUPATEN', 'KOTA')),
    geom public.geometry(MultiPolygon,4326) NOT NULL,
    area_km2 numeric(12,4),
    created_at timestamp without time zone DEFAULT now(),
    jumlah_penduduk integer,
    jumlah_kk integer,
    jumlah_kec integer,
    jumlah_desa integer,
    jumlah_kel integer,
    kepadatan numeric,
    luas_wilayah numeric,
    updated_at timestamp without time zone DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_kabupaten_geom ON wilayah.kabupaten USING gist (geom);

-- 3. Kecamatan
CREATE TABLE IF NOT EXISTS wilayah.kecamatan (
    id SERIAL PRIMARY KEY,
    kode_kec character varying(6) NOT NULL UNIQUE,
    kode_kab character varying(4) NOT NULL REFERENCES wilayah.kabupaten(kode_kab),
    nama_kecamatan character varying(100) NOT NULL,
    geom public.geometry(MultiPolygon,4326) NOT NULL,
    area_km2 numeric(12,4),
    created_at timestamp without time zone DEFAULT now(),
    jumlah_penduduk integer,
    jumlah_kk integer,
    jumlah_desa integer,
    jumlah_kel integer,
    kepadatan numeric,
    luas_wilayah numeric,
    updated_at timestamp without time zone DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_kecamatan_geom ON wilayah.kecamatan USING gist (geom);

-- 4. Desa
CREATE TABLE IF NOT EXISTS wilayah.desa (
    id SERIAL PRIMARY KEY,
    kode_desa character varying(10) NOT NULL UNIQUE,
    kode_kec character varying(6) NOT NULL REFERENCES wilayah.kecamatan(kode_kec),
    nama_desa character varying(100) NOT NULL,
    tipe character varying(15) NOT NULL,
    geom public.geometry(MultiPolygon,4326) NOT NULL,
    area_km2 numeric(12,4),
    created_at timestamp without time zone DEFAULT now(),
    jumlah_penduduk integer,
    pulau character varying(100),
    jangkauan character varying(100),
    updated_at timestamp without time zone DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_desa_geom ON wilayah.desa USING gist (geom);

-- 5. Postal Code
CREATE TABLE IF NOT EXISTS wilayah.postal_code (
    id SERIAL PRIMARY KEY,
    kode_desa character varying(10) NOT NULL REFERENCES wilayah.desa(kode_desa),
    kode_pos character varying(5),
    status character varying(20) DEFAULT 'AUGMENTED'::character varying,
    confidence numeric,
    sumber text,
    created_at timestamp without time zone DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_postal_kode_desa ON wilayah.postal_code USING btree (kode_desa);

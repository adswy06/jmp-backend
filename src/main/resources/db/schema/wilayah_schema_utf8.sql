WARNING:  database "jmp" has a collation version mismatch
DETAIL:  The database was created using collation version 2.41, but the operating system provides version 2.31.
HINT:  Rebuild all objects in this database that use the default collation and run ALTER DATABASE jmp REFRESH COLLATION VERSION, or build PostgreSQL with the right library version.
--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4 (Debian 16.4-1.pgdg110+2)
-- Dumped by pg_dump version 16.4 (Debian 16.4-1.pgdg110+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: wilayah; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA wilayah;


ALTER SCHEMA wilayah OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: desa; Type: TABLE; Schema: wilayah; Owner: admin
--

CREATE TABLE wilayah.desa (
    id integer NOT NULL,
    kode_desa character varying(10) NOT NULL,
    kode_kec character varying(6) NOT NULL,
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


ALTER TABLE wilayah.desa OWNER TO admin;

--
-- Name: desa_id_seq; Type: SEQUENCE; Schema: wilayah; Owner: admin
--

CREATE SEQUENCE wilayah.desa_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE wilayah.desa_id_seq OWNER TO admin;

--
-- Name: desa_id_seq; Type: SEQUENCE OWNED BY; Schema: wilayah; Owner: admin
--

ALTER SEQUENCE wilayah.desa_id_seq OWNED BY wilayah.desa.id;


--
-- Name: kabupaten; Type: TABLE; Schema: wilayah; Owner: admin
--

CREATE TABLE wilayah.kabupaten (
    id integer NOT NULL,
    kode_kab character varying(4) NOT NULL,
    kode_prov character varying(2) NOT NULL,
    nama_kabupaten character varying(100) NOT NULL,
    tipe character varying(15) NOT NULL,
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
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT kabupaten_tipe_check CHECK (((tipe)::text = ANY (ARRAY[('KABUPATEN'::character varying)::text, ('KOTA'::character varying)::text])))
);


ALTER TABLE wilayah.kabupaten OWNER TO admin;

--
-- Name: kabupaten_id_seq; Type: SEQUENCE; Schema: wilayah; Owner: admin
--

CREATE SEQUENCE wilayah.kabupaten_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE wilayah.kabupaten_id_seq OWNER TO admin;

--
-- Name: kabupaten_id_seq; Type: SEQUENCE OWNED BY; Schema: wilayah; Owner: admin
--

ALTER SEQUENCE wilayah.kabupaten_id_seq OWNED BY wilayah.kabupaten.id;


--
-- Name: kecamatan; Type: TABLE; Schema: wilayah; Owner: admin
--

CREATE TABLE wilayah.kecamatan (
    id integer NOT NULL,
    kode_kec character varying(6) NOT NULL,
    kode_kab character varying(4) NOT NULL,
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


ALTER TABLE wilayah.kecamatan OWNER TO admin;

--
-- Name: kecamatan_id_seq; Type: SEQUENCE; Schema: wilayah; Owner: admin
--

CREATE SEQUENCE wilayah.kecamatan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE wilayah.kecamatan_id_seq OWNER TO admin;

--
-- Name: kecamatan_id_seq; Type: SEQUENCE OWNED BY; Schema: wilayah; Owner: admin
--

ALTER SEQUENCE wilayah.kecamatan_id_seq OWNED BY wilayah.kecamatan.id;


--
-- Name: postal_code; Type: TABLE; Schema: wilayah; Owner: admin
--

CREATE TABLE wilayah.postal_code (
    id integer NOT NULL,
    kode_desa character varying(10) NOT NULL,
    kode_pos character varying(5),
    status character varying(20) DEFAULT 'AUGMENTED'::character varying,
    confidence numeric,
    sumber text,
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE wilayah.postal_code OWNER TO admin;

--
-- Name: postal_code_id_seq; Type: SEQUENCE; Schema: wilayah; Owner: admin
--

CREATE SEQUENCE wilayah.postal_code_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE wilayah.postal_code_id_seq OWNER TO admin;

--
-- Name: postal_code_id_seq; Type: SEQUENCE OWNED BY; Schema: wilayah; Owner: admin
--

ALTER SEQUENCE wilayah.postal_code_id_seq OWNED BY wilayah.postal_code.id;


--
-- Name: provinsi; Type: TABLE; Schema: wilayah; Owner: admin
--

CREATE TABLE wilayah.provinsi (
    id integer NOT NULL,
    kode_prov character varying(2) NOT NULL,
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


ALTER TABLE wilayah.provinsi OWNER TO admin;

--
-- Name: provinsi_id_seq; Type: SEQUENCE; Schema: wilayah; Owner: admin
--

CREATE SEQUENCE wilayah.provinsi_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE wilayah.provinsi_id_seq OWNER TO admin;

--
-- Name: provinsi_id_seq; Type: SEQUENCE OWNED BY; Schema: wilayah; Owner: admin
--

ALTER SEQUENCE wilayah.provinsi_id_seq OWNED BY wilayah.provinsi.id;


--
-- Name: desa id; Type: DEFAULT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.desa ALTER COLUMN id SET DEFAULT nextval('wilayah.desa_id_seq'::regclass);


--
-- Name: kabupaten id; Type: DEFAULT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kabupaten ALTER COLUMN id SET DEFAULT nextval('wilayah.kabupaten_id_seq'::regclass);


--
-- Name: kecamatan id; Type: DEFAULT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kecamatan ALTER COLUMN id SET DEFAULT nextval('wilayah.kecamatan_id_seq'::regclass);


--
-- Name: postal_code id; Type: DEFAULT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.postal_code ALTER COLUMN id SET DEFAULT nextval('wilayah.postal_code_id_seq'::regclass);


--
-- Name: provinsi id; Type: DEFAULT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.provinsi ALTER COLUMN id SET DEFAULT nextval('wilayah.provinsi_id_seq'::regclass);


--
-- Name: desa desa_kode_desa_key; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.desa
    ADD CONSTRAINT desa_kode_desa_key UNIQUE (kode_desa);


--
-- Name: desa desa_pkey; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.desa
    ADD CONSTRAINT desa_pkey PRIMARY KEY (id);


--
-- Name: kabupaten kabupaten_kode_kab_key; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kabupaten
    ADD CONSTRAINT kabupaten_kode_kab_key UNIQUE (kode_kab);


--
-- Name: kabupaten kabupaten_pkey; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kabupaten
    ADD CONSTRAINT kabupaten_pkey PRIMARY KEY (id);


--
-- Name: kecamatan kecamatan_kode_kec_key; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kecamatan
    ADD CONSTRAINT kecamatan_kode_kec_key UNIQUE (kode_kec);


--
-- Name: kecamatan kecamatan_pkey; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kecamatan
    ADD CONSTRAINT kecamatan_pkey PRIMARY KEY (id);


--
-- Name: postal_code postal_code_pkey; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.postal_code
    ADD CONSTRAINT postal_code_pkey PRIMARY KEY (id);


--
-- Name: provinsi provinsi_kode_prov_key; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.provinsi
    ADD CONSTRAINT provinsi_kode_prov_key UNIQUE (kode_prov);


--
-- Name: provinsi provinsi_pkey; Type: CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.provinsi
    ADD CONSTRAINT provinsi_pkey PRIMARY KEY (id);


--
-- Name: idx_desa_geom; Type: INDEX; Schema: wilayah; Owner: admin
--

CREATE INDEX idx_desa_geom ON wilayah.desa USING gist (geom);


--
-- Name: idx_kabupaten_geom; Type: INDEX; Schema: wilayah; Owner: admin
--

CREATE INDEX idx_kabupaten_geom ON wilayah.kabupaten USING gist (geom);


--
-- Name: idx_kecamatan_geom; Type: INDEX; Schema: wilayah; Owner: admin
--

CREATE INDEX idx_kecamatan_geom ON wilayah.kecamatan USING gist (geom);


--
-- Name: idx_postal_kode_desa; Type: INDEX; Schema: wilayah; Owner: admin
--

CREATE INDEX idx_postal_kode_desa ON wilayah.postal_code USING btree (kode_desa);


--
-- Name: idx_provinsi_geom; Type: INDEX; Schema: wilayah; Owner: admin
--

CREATE INDEX idx_provinsi_geom ON wilayah.provinsi USING gist (geom);


--
-- Name: desa desa_kode_kec_fkey; Type: FK CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.desa
    ADD CONSTRAINT desa_kode_kec_fkey FOREIGN KEY (kode_kec) REFERENCES wilayah.kecamatan(kode_kec);


--
-- Name: kabupaten kabupaten_kode_prov_fkey; Type: FK CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kabupaten
    ADD CONSTRAINT kabupaten_kode_prov_fkey FOREIGN KEY (kode_prov) REFERENCES wilayah.provinsi(kode_prov);


--
-- Name: kecamatan kecamatan_kode_kab_fkey; Type: FK CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.kecamatan
    ADD CONSTRAINT kecamatan_kode_kab_fkey FOREIGN KEY (kode_kab) REFERENCES wilayah.kabupaten(kode_kab);


--
-- Name: postal_code postal_code_kode_desa_fkey; Type: FK CONSTRAINT; Schema: wilayah; Owner: admin
--

ALTER TABLE ONLY wilayah.postal_code
    ADD CONSTRAINT postal_code_kode_desa_fkey FOREIGN KEY (kode_desa) REFERENCES wilayah.desa(kode_desa);


--
-- PostgreSQL database dump complete
--


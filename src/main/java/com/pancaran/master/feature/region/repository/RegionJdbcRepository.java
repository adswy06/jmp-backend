package com.pancaran.master.feature.region.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RegionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public RegionJdbcRepository(
            @Qualifier("wilayah-dbJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String findProvinceGeom(String code) {
        try {
            return jdbcTemplate.queryForObject(
                "select ST_AsGeoJSON(geom) from wilayah.provinsi where kode_prov = ?",
                String.class, code);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> findProvinceGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_prov, ST_AsGeoJSON(geom) as geom_json from wilayah.provinsi where kode_prov in (" + placeholders + ")";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codes.toArray());
        return rows.stream().collect(Collectors.toMap(
            row -> (String) row.get("kode_prov"),
            row -> (String) row.get("geom_json")
        ));
    }

    public String findRegencyGeom(String code) {
        try {
            return jdbcTemplate.queryForObject(
                "select ST_AsGeoJSON(geom) from wilayah.kabupaten where kode_kab = ?",
                String.class, code);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> findRegencyGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_kab, ST_AsGeoJSON(geom) as geom_json from wilayah.kabupaten where kode_kab in (" + placeholders + ")";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codes.toArray());
        return rows.stream().collect(Collectors.toMap(
            row -> (String) row.get("kode_kab"),
            row -> (String) row.get("geom_json")
        ));
    }

    public String findDistrictGeom(String code) {
        try {
            return jdbcTemplate.queryForObject(
                "select ST_AsGeoJSON(geom) from wilayah.kecamatan where kode_kec = ?",
                String.class, code);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> findDistrictGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_kec, ST_AsGeoJSON(geom) as geom_json from wilayah.kecamatan where kode_kec in (" + placeholders + ")";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codes.toArray());
        return rows.stream().collect(Collectors.toMap(
            row -> (String) row.get("kode_kec"),
            row -> (String) row.get("geom_json")
        ));
    }

    public String findVillageGeom(String code) {
        try {
            return jdbcTemplate.queryForObject(
                "select ST_AsGeoJSON(geom) from wilayah.desa where kode_desa = ?",
                String.class, code);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, String> findVillageGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyMap();
        String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_desa, ST_AsGeoJSON(geom) as geom_json from wilayah.desa where kode_desa in (" + placeholders + ")";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codes.toArray());
        return rows.stream().collect(Collectors.toMap(
            row -> (String) row.get("kode_desa"),
            row -> (String) row.get("geom_json")
        ));
    }

    public Object[] findRegionDetailsByCode(String code) {
        if (code == null) return null;
        String sql = null;
        if (code.length() == 10) {
            sql = "select nama_desa, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.desa where kode_desa = ?";
        } else if (code.length() == 6) {
            sql = "select nama_kecamatan, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.kecamatan where kode_kec = ?";
        } else if (code.length() == 4) {
            sql = "select nama_kabupaten, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.kabupaten where kode_kab = ?";
        } else if (code.length() == 2) {
            sql = "select nama_provinsi, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.provinsi where kode_prov = ?";
        }

        if (sql == null) return null;

        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, code);
            String nameCol = row.keySet().stream()
                .filter(k -> k.startsWith("nama_"))
                .findFirst().orElse("");
            Number xVal = (Number) row.get("x");
            Number yVal = (Number) row.get("y");
            return new Object[]{
                row.get(nameCol),
                xVal != null ? xVal.doubleValue() : null,
                yVal != null ? yVal.doubleValue() : null
            };
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> findRegionDetailsByCodes(List<String> codes, int length) {
        if (codes == null || codes.isEmpty()) return new ArrayList<>();
        String sql = null;
        String codeCol = null;
        String nameCol = null;
        String placeholders = codes.stream().map(c -> "?").collect(Collectors.joining(","));
        if (length == 10) {
            sql = "select kode_desa, nama_desa, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.desa where kode_desa in (" + placeholders + ")";
            codeCol = "kode_desa";
            nameCol = "nama_desa";
        } else if (length == 6) {
            sql = "select kode_kec, nama_kecamatan, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.kecamatan where kode_kec in (" + placeholders + ")";
            codeCol = "kode_kec";
            nameCol = "nama_kecamatan";
        } else if (length == 4) {
            sql = "select kode_kab, nama_kabupaten, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.kabupaten where kode_kab in (" + placeholders + ")";
            codeCol = "kode_kab";
            nameCol = "nama_kabupaten";
        } else if (length == 2) {
            sql = "select kode_prov, nama_provinsi, ST_X(ST_Centroid(geom)) as x, ST_Y(ST_Centroid(geom)) as y from wilayah.provinsi where kode_prov in (" + placeholders + ")";
            codeCol = "kode_prov";
            nameCol = "nama_provinsi";
        }

        if (sql == null) return new ArrayList<>();

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, codes.toArray());
            
            final String finalCodeCol = codeCol;
            final String finalNameCol = nameCol;
            return rows.stream().map(row -> {
                Number xVal = (Number) row.get("x");
                Number yVal = (Number) row.get("y");
                return new Object[]{
                    row.get(finalCodeCol),
                    row.get(finalNameCol),
                    xVal != null ? xVal.doubleValue() : null,
                    yVal != null ? yVal.doubleValue() : null
                };
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Object[]> findProvinces(String name) {
        String sql = "select kode_prov, nama_provinsi, area_km2 from wilayah.provinsi";
        List<Object> params = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) {
            sql += " where upper(nama_provinsi) like ?";
            params.add("%" + name.trim().toUpperCase() + "%");
        }
        sql += " order by kode_prov";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_prov"),
            rs.getString("nama_provinsi"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, params.toArray());
    }

    public List<Object[]> findRegencies(String provinceCode, String name) {
        String sql = "select kode_kab, nama_kabupaten, kode_prov, area_km2 from wilayah.kabupaten where 1=1";
        List<Object> params = new ArrayList<>();
        if (provinceCode != null && !provinceCode.trim().isEmpty()) {
            sql += " and kode_prov = ?";
            params.add(provinceCode);
        }
        if (name != null && !name.trim().isEmpty()) {
            sql += " and upper(nama_kabupaten) like ?";
            params.add("%" + name.trim().toUpperCase() + "%");
        }
        sql += " order by kode_kab";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kab"),
            rs.getString("nama_kabupaten"),
            rs.getString("kode_prov"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, params.toArray());
    }

    public List<Object[]> findDistrictsByRegencyCodes(List<String> regencyCodes) {
        if (regencyCodes == null || regencyCodes.isEmpty()) return Collections.emptyList();
        String placeholders = regencyCodes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_kec, nama_kecamatan, kode_kab, area_km2 from wilayah.kecamatan where kode_kab in (" + placeholders + ") order by kode_kec";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kec"),
            rs.getString("nama_kecamatan"),
            rs.getString("kode_kab"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, regencyCodes.toArray());
    }

    public List<Object[]> findVillagesByDistrictCodes(List<String> districtCodes) {
        if (districtCodes == null || districtCodes.isEmpty()) return Collections.emptyList();
        String placeholders = districtCodes.stream().map(c -> "?").collect(Collectors.joining(","));
        String sql = "select kode_desa, nama_desa, kode_kec, area_km2 from wilayah.desa where kode_kec in (" + placeholders + ") order by kode_desa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_desa"),
            rs.getString("nama_desa"),
            rs.getString("kode_kec"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, districtCodes.toArray());
    }

    public List<Object[]> findProvinceByCode(String code) {
        String sql = "select kode_prov, nama_provinsi, area_km2 from wilayah.provinsi where kode_prov = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_prov"),
            rs.getString("nama_provinsi"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, code);
    }

    public long countProvinces(String search) {
        String sql = "select count(*) from wilayah.provinsi";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_provinsi) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        Long count = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    public List<Object[]> findProvincesPaginated(String search, int offset, int limit) {
        String sql = "select kode_prov, nama_provinsi, area_km2 from wilayah.provinsi";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_provinsi) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        sql += " order by kode_prov limit ? offset ?";
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_prov"),
            rs.getString("nama_provinsi"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, params.toArray());
    }

    public long countRegencies(String search) {
        String sql = "select count(*) from wilayah.kabupaten";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_kabupaten) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        Long count = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    public List<Object[]> findRegenciesPaginated(String search, int offset, int limit) {
        String sql = "select kode_kab, nama_kabupaten, kode_prov from wilayah.kabupaten";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_kabupaten) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        sql += " order by kode_kab limit ? offset ?";
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kab"),
            rs.getString("nama_kabupaten"),
            rs.getString("kode_prov")
        }, params.toArray());
    }

    public long countDistricts(String search) {
        String sql = "select count(*) from wilayah.kecamatan";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_kecamatan) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        Long count = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    public List<Object[]> findDistrictsPaginated(String search, int offset, int limit) {
        String sql = "select kode_kec, nama_kecamatan, kode_kab from wilayah.kecamatan";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_kecamatan) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        sql += " order by kode_kec limit ? offset ?";
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kec"),
            rs.getString("nama_kecamatan"),
            rs.getString("kode_kab")
        }, params.toArray());
    }

    public long countVillages(String search) {
        String sql = "select count(*) from wilayah.desa";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_desa) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        Long count = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    public List<Object[]> findVillagesPaginated(String search, int offset, int limit) {
        String sql = "select kode_desa, nama_desa, kode_kec from wilayah.desa";
        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql += " where upper(nama_desa) like ?";
            params.add("%" + search.trim().toUpperCase() + "%");
        }
        sql += " order by kode_desa limit ? offset ?";
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_desa"),
            rs.getString("nama_desa"),
            rs.getString("kode_kec")
        }, params.toArray());
    }

    public List<Object[]> searchProvinces(String search) {
        String sql = "select kode_prov, nama_provinsi, area_km2 from wilayah.provinsi where upper(nama_provinsi) like ? order by kode_prov";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_prov"),
            rs.getString("nama_provinsi"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, "%" + search.trim().toUpperCase() + "%");
    }

    public List<Object[]> searchRegencies(String search) {
        String sql = "select kode_kab, nama_kabupaten, kode_prov, area_km2 from wilayah.kabupaten where upper(nama_kabupaten) like ? order by kode_kab limit 50";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kab"),
            rs.getString("nama_kabupaten"),
            rs.getString("kode_prov"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, "%" + search.trim().toUpperCase() + "%");
    }

    public List<Object[]> searchDistricts(String search) {
        String sql = "select kode_kec, nama_kecamatan, kode_kab, area_km2 from wilayah.kecamatan where upper(nama_kecamatan) like ? order by kode_kec limit 100";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_kec"),
            rs.getString("nama_kecamatan"),
            rs.getString("kode_kab"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, "%" + search.trim().toUpperCase() + "%");
    }

    public List<Object[]> searchVillages(String search) {
        String sql = "select kode_desa, nama_desa, kode_kec, area_km2 from wilayah.desa where upper(nama_desa) like ? order by kode_desa limit 200";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
            rs.getString("kode_desa"),
            rs.getString("nama_desa"),
            rs.getString("kode_kec"),
            rs.getObject("area_km2") != null ? rs.getDouble("area_km2") : null
        }, "%" + search.trim().toUpperCase() + "%");
    }
}

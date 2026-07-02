package com.pancaran.master.feature.region.repository;

import com.pancaran.master.feature.region.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RegionRepository {

    @PersistenceContext(unitName = "wilayah-dbEntityManagerFactory")
    private EntityManager entityManager;

    public List<ProvinceEntity> findProvinces(String name) {
        if (name != null && !name.trim().isEmpty()) {
            return entityManager.createQuery(
                "select p from ProvinceEntity p where upper(p.namaProvinsi) like :name order by p.kodeProv", ProvinceEntity.class)
                .setParameter("name", "%" + name.trim().toUpperCase() + "%")
                .getResultList();
        }
        return entityManager.createQuery("select p from ProvinceEntity p order by p.kodeProv", ProvinceEntity.class)
                .getResultList();
    }

    public Optional<ProvinceEntity> findProvinceByCode(String code) {
        List<ProvinceEntity> list = entityManager.createQuery(
            "select p from ProvinceEntity p where p.kodeProv = :code", ProvinceEntity.class)
            .setParameter("code", code)
            .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<RegencyEntity> findRegencies(String provinceCode, String name) {
        StringBuilder jpql = new StringBuilder("select r from RegencyEntity r where 1=1");
        if (provinceCode != null && !provinceCode.trim().isEmpty()) {
            jpql.append(" and r.kodeProv = :provinceCode");
        }
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(r.namaKabupaten) like :name");
        }
        jpql.append(" order by r.kodeKab");

        var query = entityManager.createQuery(jpql.toString(), RegencyEntity.class);
        if (provinceCode != null && !provinceCode.trim().isEmpty()) {
            query.setParameter("provinceCode", provinceCode);
        }
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public Optional<RegencyEntity> findRegencyByCode(String code) {
        List<RegencyEntity> list = entityManager.createQuery(
            "select r from RegencyEntity r where r.kodeKab = :code", RegencyEntity.class)
            .setParameter("code", code)
            .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<DistrictEntity> findDistricts(String regencyCode, String name) {
        StringBuilder jpql = new StringBuilder("select d from DistrictEntity d where 1=1");
        if (regencyCode != null && !regencyCode.trim().isEmpty()) {
            jpql.append(" and d.kodeKab = :regencyCode");
        }
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(d.namaKecamatan) like :name");
        }
        jpql.append(" order by d.kodeKec");

        var query = entityManager.createQuery(jpql.toString(), DistrictEntity.class);
        if (regencyCode != null && !regencyCode.trim().isEmpty()) {
            query.setParameter("regencyCode", regencyCode);
        }
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public Optional<DistrictEntity> findDistrictByCode(String code) {
        List<DistrictEntity> list = entityManager.createQuery(
            "select d from DistrictEntity d where d.kodeKec = :code", DistrictEntity.class)
            .setParameter("code", code)
            .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<VillageEntity> findVillages(String districtCode, String name) {
        StringBuilder jpql = new StringBuilder("select v from VillageEntity v where 1=1");
        if (districtCode != null && !districtCode.trim().isEmpty()) {
            jpql.append(" and v.kodeKec = :districtCode");
        }
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(v.namaDesa) like :name");
        }
        jpql.append(" order by v.kodeDesa");

        var query = entityManager.createQuery(jpql.toString(), VillageEntity.class);
        if (districtCode != null && !districtCode.trim().isEmpty()) {
            query.setParameter("districtCode", districtCode);
        }
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }

    public Optional<VillageEntity> findVillageByCode(String code) {
        List<VillageEntity> list = entityManager.createQuery(
            "select v from VillageEntity v where v.kodeDesa = :code", VillageEntity.class)
            .setParameter("code", code)
            .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<PostalCodeEntity> findPostalCodes(String villageCode, String code) {
        StringBuilder jpql = new StringBuilder("select p from PostalCodeEntity p where 1=1");
        if (villageCode != null && !villageCode.trim().isEmpty()) {
            jpql.append(" and p.kodeDesa = :villageCode");
        }
        if (code != null && !code.trim().isEmpty()) {
            jpql.append(" and p.kodePos = :code");
        }
        jpql.append(" order by p.kodePos");

        var query = entityManager.createQuery(jpql.toString(), PostalCodeEntity.class);
        if (villageCode != null && !villageCode.trim().isEmpty()) {
            query.setParameter("villageCode", villageCode);
        }
        if (code != null && !code.trim().isEmpty()) {
            query.setParameter("code", code);
        }
        return query.getResultList();
    }

    public String findProvinceGeom(String code) {
        try {
            return (String) entityManager.createNativeQuery(
                "select ST_AsGeoJSON(geom) from wilayah.provinsi where kode_prov = :code")
                .setParameter("code", code)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, String> findProvinceGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return java.util.Collections.emptyMap();
        List<Object[]> list = entityManager.createNativeQuery(
            "select kode_prov, ST_AsGeoJSON(geom) from wilayah.provinsi where kode_prov in :codes")
            .setParameter("codes", codes)
            .getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> (String) row[1]
        ));
    }

    public String findRegencyGeom(String code) {
        try {
            return (String) entityManager.createNativeQuery(
                "select ST_AsGeoJSON(geom) from wilayah.kabupaten where kode_kab = :code")
                .setParameter("code", code)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, String> findRegencyGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return java.util.Collections.emptyMap();
        List<Object[]> list = entityManager.createNativeQuery(
            "select kode_kab, ST_AsGeoJSON(geom) from wilayah.kabupaten where kode_kab in :codes")
            .setParameter("codes", codes)
            .getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> (String) row[1]
        ));
    }

    public String findDistrictGeom(String code) {
        try {
            return (String) entityManager.createNativeQuery(
                "select ST_AsGeoJSON(geom) from wilayah.kecamatan where kode_kec = :code")
                .setParameter("code", code)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, String> findDistrictGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return java.util.Collections.emptyMap();
        List<Object[]> list = entityManager.createNativeQuery(
            "select kode_kec, ST_AsGeoJSON(geom) from wilayah.kecamatan where kode_kec in :codes")
            .setParameter("codes", codes)
            .getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> (String) row[1]
        ));
    }

    public String findVillageGeom(String code) {
        try {
            return (String) entityManager.createNativeQuery(
                "select ST_AsGeoJSON(geom) from wilayah.desa where kode_desa = :code")
                .setParameter("code", code)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.Map<String, String> findVillageGeoms(List<String> codes) {
        if (codes == null || codes.isEmpty()) return java.util.Collections.emptyMap();
        List<Object[]> list = entityManager.createNativeQuery(
            "select kode_desa, ST_AsGeoJSON(geom) from wilayah.desa where kode_desa in :codes")
            .setParameter("codes", codes)
            .getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> (String) row[1]
        ));
    }

    public Object[] findRegionDetailsByCode(String code) {
        if (code == null) return null;
        String sql = null;
        if (code.length() == 10) {
            sql = "select nama_desa, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.desa where kode_desa = :code";
        } else if (code.length() == 6) {
            sql = "select nama_kecamatan, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.kecamatan where kode_kec = :code";
        } else if (code.length() == 4) {
            sql = "select nama_kabupaten, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.kabupaten where kode_kab = :code";
        } else if (code.length() == 2) {
            sql = "select nama_provinsi, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.provinsi where kode_prov = :code";
        }

        if (sql == null) return null;

        try {
            return (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findRegionDetailsByCodes(List<String> codes, int length) {
        if (codes == null || codes.isEmpty()) return new java.util.ArrayList<>();
        String sql = null;
        if (length == 10) {
            sql = "select kode_desa, nama_desa, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.desa where kode_desa in :codes";
        } else if (length == 6) {
            sql = "select kode_kec, nama_kecamatan, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.kecamatan where kode_kec in :codes";
        } else if (length == 4) {
            sql = "select kode_kab, nama_kabupaten, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.kabupaten where kode_kab in :codes";
        } else if (length == 2) {
            sql = "select kode_prov, nama_provinsi, ST_X(ST_Centroid(geom)), ST_Y(ST_Centroid(geom)) from wilayah.provinsi where kode_prov in :codes";
        }

        if (sql == null) return new java.util.ArrayList<>();

        try {
            return entityManager.createNativeQuery(sql)
                    .setParameter("codes", codes)
                    .getResultList();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}

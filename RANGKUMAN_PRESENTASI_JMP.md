# Rangkuman Teknis: Arsitektur & Logic Alur JMP & Trip Planning

Dokumen ini berisi rangkuman teknis yang siap digunakan untuk presentasi. Menjelaskan secara detail arsitektur service, alur logika, validasi, dan alur integrasi database antara **Master Trip Planning (Template Rute)** dengan **JMP Transaction (Detail Eksekusi)**.

---

## 1. Pembagian Arsitektur & Service

Sistem dipisah secara tegas menjadi dua bagian utama untuk memisahkan **Template** dengan **Transaksi Eksekusi**:

```
                       +-------------------+
                       |  Client REST API  |
                       +---------+---------+
                                 |
           +---------------------+---------------------+
           |                                           |
           v                                           v
(POST /api/planing)                         (POST /api/jmp)
+------------------+                       +---------------+
|  PlaningService  |                       |  JmpService   |
+--------+---------+                       +-------+-------+
         |                                         |
         v                                         v
 [Simpan Rute Murni]                       [Simpan Detail Transaksi]
 (RouteRepository)                         (JmpRepository)
         |                                         |
         v                                         v
 [Tabel: m_route]                          [Tabel: t_jmp]
```

### A. Route Planning Service (`PlaningService.java`)
*   **Tujuan**: Menyimpan blueprint rute fisik yang bersifat statis (Master Route).
*   **Logic**:
    1. **Mapping**: Menerima request rute murni (POI, koordinat, urutan sequence).
    2. **Enrichment**: Melakukan kalkulasi otomatis jarak (`distanceKm`), waktu tempuh rata-rata (`journeyLeadTime`), koordinat pusat, dan pemetaan batas wilayah PostGIS geofence.
    3. **Persist**: Menyimpan ke tabel master `m_route`, `m_route_point`, `m_geofence`.

### B. Journey Management Plan Service (`JmpService.java`)
*   **Tujuan**: Membuat perencanaan perjalanan nyata untuk satu order (transaksi eksekusi).
*   **Logic**:
    1. Menghubungkan transaksi ke template master rute (`routeId`).
    2. Menentukan unit kendaraan, driver, notifikasi, segmentasi jalur, dan rincian biaya.
    3. Menyimpan ke tabel transaksi `t_jmp`, `t_jmp_trip_plan`, `t_jmp_route_point`, `t_jmp_activity`, dan `t_jmp_route_segment`.

---

## 2. Alur Logika (Core Logic) di JmpService

Proses penyimpanan transaksi JMP melalui tahapan logika terstruktur sebagai berikut:

### Alur A: Penyimpanan JMP Header & Notifikasi Global
1. Menyimpan data order utama seperti customer, consignee, nomor PO/referensi ke tabel `t_jmp`.
2. Menyimpan pengaturan notifikasi global (`isNotificationGlobal`) yang menentukan apakah sistem akan mengirimkan update status otomatis secara keseluruhan.

### Alur B: Logika Pewarisan & Custom Unit (Inherit/Override Unit)
Logika ini menentukan jenis armada apa yang dipakai di setiap segmen rute perjalanan:
```
           [ JMP Header Units Ada? ]
                  /        \
               (Ya)        (Tidak)
               /              \
[Propagasikan unit header]    [Gunakan unit custom]
[ke semua segmen perjalanan]  [di masing-masing segmen]
```
1. **Kasus 1 (Default Global)**: Jika armada diisi di JMP Header (misal: Wingbox), sistem mengasumsikan seluruh segmen perjalanan memakai Wingbox. Unit di segmen (`t_jmp_route_segment_unit`) otomatis di-copy dari header.
2. **Kasus 2 (Custom Segment)**: Jika JMP Header kosong, sistem membaca array `units` di setiap segmen `routeDetails` (misal: Segmen 1 Wingbox, Segmen 2 Kapal Penyeberangan, Segmen 3 Colt Diesel). Sistem menyimpannya sebagai unit kustom per segmen.

### Alur C: Mapping Urutan Rute ke Database ID (`routePointSeqToIdMap`)
Karena tabel `t_jmp_route_point` menggunakan UUID auto-generated dari Postgres, sedangkan data payload REST API mendefinisikan rute berdasarkan urutan numerik (`seqno`), maka:
1. Sistem membuat cache lokal `Map<Integer, String> routePointSeqToIdMap`.
2. Saat menyimpan setiap titik rute, sistem merekam: `Key: seqno -> Value: Generated UUID`.
3. Saat memproses segmen jalan (`routeDetails`), sistem menentukan id titik start (`start_route_point_id`) dan end (`end_route_point_id`) dengan mengambil UUID dari map berdasarkan `startSeqNo` dan `endSeqNo`. Ini mencegah error inkonsistensi relasi rute.

### Alur D: Notifikasi & Catatan Khusus Aktivitas
Pada setiap aktivitas titik pemberhentian (misal: Loading, Timbang, Bongkar):
1. Menyimpan flag notifikasi khusus (`isNotification`) yang menentukan apakah aktivitas ini butuh warning alert saat driver tiba di lokasi.
2. Menyimpan catatan instruksi kerja (`notes`) khusus untuk dibaca oleh driver/tim lapangan.

### Alur E: Propagasi Balik ke Master (Jika `saveToMaster = true`)
Jika flag `saveToMaster` bernilai `true`, sistem akan memperbarui template master rute secara otomatis:
1. Mengubah titik rute custom menjadi titik rute master (`m_route_point`).
2. Menghitung/mengupdate lead time standar (`m_activity_lead_time`) dan harga dasar (`m_activity_cost`) di master rute template berdasarkan inputan real-time transaksi JMP.
3. **Optimasi Performance**: Cache lokal (`masterActivityMap`, `costMap`, `leadTimeMap`) di-load di awal proses untuk mencegah query database berulang-ulang (*N+1 Query Issue*).

---

## 3. Validasi & Penanganan Transaksi

### A. Validasi Model Request
*   **Format Validasi**: Menggunakan anotasi Spring `@Valid`, `@NotBlank`, dan `@NotEmpty` di Controller untuk memastikan request payload tidak bernilai null sebelum diproses di Service.
*   **Integritas Relasi**: Database Postgres di-setting ketat menggunakan FK (Foreign Key) constraint. Jika `routeId` yang dimasukkan di JMP tidak terdaftar di tabel `m_route`, database otomatis melempar `DataIntegrityViolationException` (Constraint Violation) untuk mencegah data sampah.

### B. Validasi Multi-Database (Transactional Boundary)
Aplikasi ini berjalan dengan multi-datasource yang dikonfigurasi dinamis. 
*   Setiap method Service yang melakukan query ke DB modul JMP wajib didekorasi dengan annotation:
    ```java
    @Transactional(value = "jmp-dbTransactionManager", readOnly = true/false)
    ```
*   Hal ini memastikan Spring Boot menggunakan manager transaksi database JMP (`jmp-db`) yang tepat dan mencegah kegagalan pencarian bean default (`NoSuchBeanDefinitionException`).

---

## 4. Strategi Pengambilan Data (Retrieval Strategy: Lazy, Eager & Join Fetch)

Ketika mengambil data terelasi di JPA (seperti relasi `JmpEntity` ke `CustomerEntity` / `ConsigneeEntity`), pemilihan cara memuat data sangat menentukan performa aplikasi. Berikut adalah strategi yang diimplementasikan:

### A. Lazy Loading by Default (`FetchType.LAZY`)
*   **Implementasi**: Semua hubungan `@ManyToOne` dan `@OneToMany` di-set menggunakan `FetchType.LAZY` di level Entity.
*   **Alasan**: Mencegah overhead memori. Saat aplikasi hanya butuh list data JMP dasar (misal untuk tabel ringkas), Hibernate tidak akan meload data objek `CustomerEntity` dan `ConsigneeEntity` secara otomatis. Relasi hanya dimuat sebagai proxy.

### B. Masalah N+1 Query & Jackson Serialization
*   **Masalah**: Jika relasi didefinisikan sebagai Lazy, namun serializer JSON (Jackson) mencoba mengubah list data JMP menjadi JSON payload, Jackson secara tidak sengaja akan memanggil method getter (misal `customer.getName()`). Hal ini memaksa Hibernate mengeksekusi query SQL tambahan untuk setiap baris data (1 query utama + N query detail), yang mengakibatkan sistem menjadi sangat lambat (*N+1 Query Issue*).
*   **Solusi pada API Listing**: Kita mengoptimasi method `findJmpPage` dengan memutus relasi proxy Jackson secara dinamis dengan mengeset `j.setCustomer(null)` dan `j.setConsignee(null)` sebelum data dikirim ke serializer. Dengan begitu, query berjalan murni 1x tanpa overhead join relasi.

### C. Join Fetch untuk Detail Query (Dynamic Eager Loading)
*   **Implementasi**: Ketika user masuk ke halaman detail JMP dan aplikasi **wajib** menampilkan info lengkap Customer dan Consignee, kita menggunakan query JPQL dengan clause `JOIN FETCH`:
    ```sql
    select j from JmpEntity j 
    left join fetch j.customer 
    left join fetch j.consignee 
    where j.id = :id
    ```
*   **Alasan**: `JOIN FETCH` bertindak sebagai *Dynamic Eager Loading*. Relasi tetap disetting `LAZY` pada level entity agar query lain tidak terbebani, namun pada query detail spesifik ini, Hibernate diperintahkan untuk menyatukan data JMP, Customer, dan Consignee dalam **1 kali query database (Single Roundtrip)** dengan SQL `LEFT JOIN`. Objek relasi terisi penuh tanpa memicu *N+1 Query*.

### D. Ringkasan Perbandingan Strategi
| Pendekatan | Cara Kerja | Kapan Digunakan | Kelebihan / Kekurangan |
| :--- | :--- | :--- | :--- |
| **Lazy Loading (`LAZY`)** | Data relasi hanya dimuat saat diakses pertama kali di Java code. | Digunakan secara default pada semua relasi Entity. | **(+)** Hemat memori & query cepat.<br>**(-)** Resiko N+1 query jika salah penanganan saat serialisasi JSON. |
| **Eager Loading (`EAGER`)** | Data relasi *selalu* ikut diload setiap kali entity di-query. | Sangat jarang digunakan (dihindari). | **(+)** Data relasi selalu tersedia.<br>**(-)** Berat, memicu query raksasa walaupun data relasi tidak dibutuhkan. |
| **Join Fetch (`JOIN FETCH`)** | Data relasi diload secara instan lewat perintah join eksplisit di JPQL. | Query Detail atau Laporan Lengkap yang membutuhkan relasi anak. | **(+)** Menggabungkan data secara instan dalam 1 query SQL.<br>**(+)** Menghilangkan N+1 query secara tuntas. |

---

## 5. Contoh Integrasi Data Riil (Real Case Request Payloads Flow)

Berikut adalah contoh skenario pengiriman barang nyata dari **Tanjung Priok (Jakarta) ke Tanjung Perak (Surabaya)** menggunakan rute Toll + Pantura.

### Langkah 1: POST /api/planing (Membuat Template Rute Murni)
API ini dipanggil untuk merencanakan template rute statis yang hanya berisikan koordinat titik rute (`routePoints`), geofence, serta default estimasi leadtime.

*   **URL**: `http://localhost:8081/api/planing`
*   **Method**: `POST`
*   **Payload**:
```json
{
    "name": "Jakarta - Surabaya Main Route (Toll & Pantura)",
    "alias": "JKT-SUB-MIX-ROUTE",
    "routePoints": [
        {
            "poiId": "11111111-1111-1111-1111-111111111111",
            "seqNo": 1,
            "alias": "Tanjung Priok Port Area",
            "address": "Tanjung Priok, DKI Jakarta",
            "geofence": [
                {
                    "shapeType": "ZONE",
                    "radius": 0,
                    "paths": {
                        "zoneId": "JKT-ZONE",
                        "encodePolyline": "encoded_polygon_bounds_jakarta_zone"
                    }
                }
            ],
            "activities": [
                {
                    "activityId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                    "leadTime": 60,
                    "amount": 120000,
                    "extraCost": [
                        {
                            "name": "Administrasi Pelabuhan",
                            "amount": 25000
                        }
                    ]
                }
            ],
            "paths": {
                "encodePolyline": "encoded_polyline_jakarta_to_cikampek_via_toll",
                "steps": [
                    {
                        "seqNo": 1,
                        "instruction": "Masuk Gerbang Tol Sunter / Tanjung Priok",
                        "maneuver": "merge",
                        "distanceMeter": 1500,
                        "durationSecond": 120,
                        "encodePolyline": "sunter_toll_in_polyline"
                    },
                    {
                        "seqNo": 2,
                        "instruction": "Lurus di Tol Jakarta-Cikampek",
                        "maneuver": "straight",
                        "distanceMeter": 72000,
                        "durationSecond": 2700,
                        "encodePolyline": "jkt_cikampek_toll_polyline"
                    },
                    {
                        "seqNo": 3,
                        "instruction": "Keluar Tol Cikampek Utama",
                        "maneuver": "ramp-left",
                        "distanceMeter": 500,
                        "durationSecond": 60,
                        "encodePolyline": "cikampek_toll_out_polyline"
                    }
                ]
            }
        },
        {
            "poiId": "22222222-2222-2222-2222-222222222222",
            "seqNo": 2,
            "alias": "Cikampek Pantura Point",
            "address": "Cikampek, Jawa Barat",
            "geofence": [
                {
                    "shapeType": "RADIUS",
                    "radius": 150,
                    "paths": {
                        "center": {
                            "lat": -6.3900,
                            "lng": 107.4580
                        }
                    }
                }
            ],
            "activities": [
                {
                    "activityId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
                    "leadTime": 30,
                    "amount": 40000,
                    "extraCost": []
                }
            ],
            "paths": {
                "encodePolyline": "encoded_polyline_cikampek_to_semarang_via_pantura_non_toll",
                "steps": [
                    {
                        "seqNo": 1,
                        "instruction": "Lanjut ke Jalan Raya Pantura (Cirebon - Pekalongan)",
                        "maneuver": "straight",
                        "distanceMeter": 180000,
                        "durationSecond": 14400,
                        "encodePolyline": "pantura_main_road_polyline"
                    },
                    {
                        "seqNo": 2,
                        "instruction": "Masuk Batas Kota Semarang",
                        "maneuver": "straight",
                        "distanceMeter": 25000,
                        "durationSecond": 1800,
                        "encodePolyline": "semarang_entrance_polyline"
                    }
                ]
            }
        },
        {
            "poiId": "33333333-3333-3333-3333-333333333333",
            "seqNo": 3,
            "alias": "Semarang Waypoint",
            "address": "Semarang, Jawa Tengah",
            "geofence": [
                {
                    "shapeType": "RADIUS",
                    "radius": 100,
                    "paths": {
                        "center": {
                            "lat": -6.9932,
                            "lng": 110.4203
                        }
                    }
                }
            ],
            "activities": [
                {
                    "activityId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
                    "leadTime": 45,
                    "amount": 75000,
                    "extraCost": [
                        {
                            "name": "Retribusi Wilayah",
                            "amount": 10000
                        }
                    ]
                }
            ],
            "paths": {
                "encodePolyline": "encoded_polyline_semarang_to_surabaya_via_toll",
                "steps": [
                    {
                        "seqNo": 1,
                        "instruction": "Masuk Gerbang Tol Muktiharjo Semarang",
                        "maneuver": "merge",
                        "distanceMeter": 2000,
                        "durationSecond": 150,
                        "encodePolyline": "muktiharjo_toll_in"
                    },
                    {
                        "seqNo": 2,
                        "instruction": "Melaju di Tol Trans-Jawa Semarang-Surabaya",
                        "maneuver": "straight",
                        "distanceMeter": 310000,
                        "durationSecond": 10800,
                        "encodePolyline": "semarang_sub_toll_main"
                    },
                    {
                        "seqNo": 3,
                        "instruction": "Keluar Gerbang Tol Waru Surabaya",
                        "maneuver": "ramp-right",
                        "distanceMeter": 1200,
                        "durationSecond": 180,
                        "encodePolyline": "waru_toll_exit"
                    }
                ]
            }
        },
        {
            "poiId": "44444444-4444-4444-4444-444444444444",
            "seqNo": 4,
            "alias": "Surabaya Tanjung Perak DC",
            "address": "Tanjung Perak, Surabaya, Jawa Timur",
            "geofence": [
                {
                    "shapeType": "ZONE",
                    "radius": 0,
                    "paths": {
                        "zoneId": "SUB-ZONE",
                        "encodePolyline": "encoded_polygon_bounds_surabaya_zone"
                    }
                }
            ],
            "activities": [
                {
                    "activityId": "dddddddd-dddd-dddd-dddd-dddddddddddd",
                    "leadTime": 180,
                    "amount": 250000,
                    "extraCost": [
                        {
                            "name": "Bongkar Muat",
                            "amount": 150000
                        }
                    ]
                }
            ],
            "paths": null
        }
    ]
}
```

*Response sukses dari API di atas akan mengembalikan UUID master route (misal: `9b1deb4d-3b7d-4bad-9bdd-2b0d7b3d4b6c`).*

---

### Langkah 2: POST /api/jmp (Membuat JMP Transaksi dengan Detail Rute)
Setelah mendapatkan UUID route dari Langkah 1, gunakan UUID tersebut sebagai `routeId` untuk membuat perencanaan order transaksi lengkap, mencakup pemilihan JMP-level multi-unit, segmentasi jalan (`routeDetails`), driver, serta instruksi notifikasi/notes aktivitas.

*   **URL**: `http://localhost:8081/api/jmp`
*   **Method**: `POST`
*   **Payload**:
```json
{
  "id": "",
  "customerId": "cust-pancaran-101",
  "consigneeId": "cons-surabaya-202",
  "commercialRoute": "JKT-SUB-MIX-TOLL-PANTURA",
  "referenceNo": "REF-PO-99881-A",
  "title": "Mixed Route Delivery Jakarta to Surabaya",
  "description": "Electronics distribution shipment from Tanjung Priok to Tanjung Perak via Toll + Pantura",
  "status": "DRAFT",
  "saveToMaster": true,
  "isNotificationGlobal": true,
  "units": [
    {
      "unitTypeId": "UNIT-WINGBOX"
    },
    {
      "unitTypeId": "UNIT-COLT-DIESEL"
    }
  ],
  "tripPlans": [
    {
      "routeId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3d4b6c",
      "seqno": 1,
      "transportMode": "LAND",
      "remarks": "Mixed Toll & Pantura Trip Plan",
      "routePoints": [
        {
          "poiId": "11111111-1111-1111-1111-111111111111",
          "seqno": 1,
          "alias": "Tanjung Priok Port Area",
          "address": "Tanjung Priok, DKI Jakarta",
          "isCustom": false,
          "activities": [
            {
              "activityId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
              "activityName": "Loading Cargo",
              "leadtime": 60,
              "cost": 120000,
              "seqno": 1,
              "remarks": "Load electronics container",
              "isNotification": true,
              "notes": "Ensure cargo is properly strapped before departure"
            }
          ]
        },
        {
          "poiId": "22222222-2222-2222-2222-222222222222",
          "seqno": 2,
          "alias": "Cikampek Pantura Point",
          "address": "Cikampek, Jawa Barat",
          "isCustom": false,
          "activities": [
            {
              "activityId": "cccccccc-cccc-cccc-cccc-cccccccccccc",
              "activityName": "Checkpoint Transit",
              "leadtime": 30,
              "cost": 40000,
              "seqno": 1,
              "remarks": "Check cargo tie-downs",
              "isNotification": false,
              "notes": "Quick security check"
            }
          ]
        },
        {
          "poiId": "33333333-3333-3333-3333-333333333333",
          "seqno": 3,
          "alias": "Semarang Waypoint",
          "address": "Semarang, Jawa Tengah",
          "isCustom": false,
          "activities": [
            {
              "activityId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
              "activityName": "Refuel & Rest",
              "leadtime": 45,
              "cost": 75000,
              "seqno": 1,
              "remarks": "Driver rest stop",
              "isNotification": true,
              "notes": "Driver must log 45 minutes rest time"
            }
          ]
        },
        {
          "poiId": "44444444-4444-4444-4444-444444444444",
          "seqno": 4,
          "alias": "Surabaya Tanjung Perak DC",
          "address": "Tanjung Perak, Surabaya, Jawa Timur",
          "isCustom": false,
          "activities": [
            {
              "activityId": "dddddddd-dddd-dddd-dddd-dddddddddddd",
              "activityName": "Cargo Unloading",
              "leadtime": 180,
              "cost": 250000,
              "seqno": 1,
              "remarks": "Unload at Surabaya DC",
              "isNotification": true,
              "notes": "Verify seals before unloading"
            }
          ]
        }
      ],
      "extraCosts": [
        {
          "name": "E-Toll Fees JKT-SUB",
          "amount": 750000,
          "remarks": "Toll roads Cikampek and Semarang-Surabaya segments"
        },
        {
          "name": "Jembatan Timbang",
          "amount": 50000,
          "remarks": "Pantura weighing stations"
        }
      ],
      "drivers": [
        {
          "driverId": "driver-pancaran-881",
          "seqno": 1
        }
      ],
      "routeDetails": [
        {
          "seqNo": 1,
          "startSeqNo": 1,
          "endSeqNo": 2,
          "remarks": "Tanjung Priok to Cikampek Interchange (Toll Segment)",
          "units": [
            {
              "unitTypeId": "UNIT-WINGBOX"
            }
          ]
        },
        {
          "seqNo": 2,
          "startSeqNo": 2,
          "endSeqNo": 3,
          "remarks": "Cikampek to Semarang (Pantura Road Segment)",
          "units": [
            {
              "unitTypeId": "UNIT-WINGBOX"
            },
            {
              "unitTypeId": "UNIT-COLT-DIESEL"
            }
          ]
        },
        {
          "seqNo": 3,
          "startSeqNo": 3,
          "endSeqNo": 4,
          "remarks": "Semarang to Surabaya Tanjung Perak (Toll Segment)",
          "units": [
            {
              "unitTypeId": "UNIT-WINGBOX"
            }
          ]
        }
      ]
    }
  ]
}
```


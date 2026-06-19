package com.pancaran.master.constant;

public final class ResponseMessageConstant {

    private ResponseMessageConstant() {
        // prevent instantiation
    }

    /* ================= SUCCESS ================= */
    public static final String SUCCESS_CREATE = "Data berhasil dibuat";
    public static final String SUCCESS_UPDATE = "Data berhasil diupdate";
    public static final String SUCCESS_DELETE = "Data berhasil dihapus";
    public static final String SUCCESS_GET = "Data berhasil diambil";

    /* ================= ERROR ================= */
    public static final String ERROR_NOT_FOUND = "Data tidak ditemukan";
    public static final String ERROR_SAVE_FAILED = "Gagal menyimpan data";
    public static final String ERROR_INVALID_REQUEST = "Request tidak valid";
    public static final String ERROR_INTERNAL_SERVER = "Terjadi kesalahan pada server";

}

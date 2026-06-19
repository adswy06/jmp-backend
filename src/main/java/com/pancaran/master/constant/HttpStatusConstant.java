package com.pancaran.master.constant;

import org.springframework.http.HttpStatus;

public final class HttpStatusConstant {
    private HttpStatusConstant() {}

    /* ================= 2xx SUCCESS ================= */

    public static final HttpStatus OK = HttpStatus.OK; // 200
    public static final HttpStatus CREATED = HttpStatus.CREATED; // 201
    public static final HttpStatus NO_CONTENT = HttpStatus.NO_CONTENT; // 204

    /* ================= 4xx CLIENT ERROR ================= */

    public static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST; // 400
    public static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED; // 401
    public static final HttpStatus FORBIDDEN = HttpStatus.FORBIDDEN; // 403
    public static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND; // 404
    public static final HttpStatus CONFLICT = HttpStatus.CONFLICT; // 409
    public static final HttpStatus UNPROCESSABLE_ENTITY = HttpStatus.UNPROCESSABLE_ENTITY; // 422

    /* ================= 5xx SERVER ERROR ================= */

    public static final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR; // 500
    public static final HttpStatus SERVICE_UNAVAILABLE = HttpStatus.SERVICE_UNAVAILABLE; // 503
}

package com.po_chunchen.gv_eye_cloud_servlet.common;

/**
 * Created by po-chunchen on 2017/12/13.
 */
public enum GvErrorCode {
    HTTP_OK(200, "HTTP OK"),
    HTTP_BAD_REQUEST(400, "Bad Request"),
    HTTP_NOT_FOUND(404, "Not Found"),
    PARAMETER_ERROR(3000, "Parameter Error"),
    DATASTORE_TIMEOUT_EXCEPTION(3001, "Datastore Timeout Exception"),
    DATASTORE_EXCEPTION(3002, "Datastore Exception"),
    UPDATE_WILL_SUCCESS(1, "Success"),
    UPDATE_WILL_FAIL(0, "Fail."),
    DOWNLOAD_CAMERA_LISTS_WILL_SUCCESS(1, "Success"),
    DOWNLOAD_CAMERA_LISTS_WILL_FAIL(0, "Fail");

    private final int code;
    private final String description;

    private GvErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCode() {
        return this.code;
    }

    public String toString() {
        return this.code + ": " + this.description;
    }

}

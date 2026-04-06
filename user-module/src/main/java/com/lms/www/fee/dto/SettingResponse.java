package com.lms.www.fee.dto;

public class SettingResponse<T> {
    private boolean success;
    private T data;

    public SettingResponse() {}
    public SettingResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> SettingResponse<T> success(T data) {
        return new SettingResponse<T>(true, data);
    }
}

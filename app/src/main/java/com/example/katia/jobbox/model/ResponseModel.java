package com.example.katia.jobbox.model;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {

    private boolean success;
    @SerializedName("user_status")
    private int status;
    @SerializedName("last_id")
    private int id;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

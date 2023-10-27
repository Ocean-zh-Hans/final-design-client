package com.example.vo;


import androidx.annotation.NonNull;

/**
 * 封装前端返回的统一实体类
 * @param <T>
 */
public class ServerResponse<T> {
    private int status;
    private T data;
    private String msg;

    public ServerResponse() {
    }

    public ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
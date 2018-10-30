package com.example.zhanghao.skylu.httpTool.RuoKuai;

public class RuoKuaiSuccess {
    private String result;
    private String id;
    private String error;
    private String error_code;
    private String request;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public RuoKuaiSuccess(String result, String id, String error, String error_code, String request) {
        this.result = result;
        this.id = id;
        this.error = error;
        this.error_code = error_code;
        this.request = request;
    }

    @Override
    public String toString() {
        return "RuoKuaiSuccess{" +
                "result='" + result + '\'' +
                ", id='" + id + '\'' +
                ", error='" + error + '\'' +
                ", error_code='" + error_code + '\'' +
                ", request='" + request + '\'' +
                '}';
    }
}

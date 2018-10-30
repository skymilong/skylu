package com.example.zhanghao.skylu.httpTool.RuoKuai;

public class RuoKuaiInfo {
    private String score;
    private String historyScore;
    private String totalScore;
    private String totalTopic;
    private String error;
    private String error_code;
    private String request;

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getHistoryScore() {
        return historyScore;
    }

    public void setHistoryScore(String historyScore) {
        this.historyScore = historyScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getTotalTopic() {
        return totalTopic;
    }

    public void setTotalTopic(String totalTopic) {
        this.totalTopic = totalTopic;
    }


    public RuoKuaiInfo(String score, String historyScore, String totalScore, String totalTopic, String error, String error_code, String request) {
        this.score = score;
        this.historyScore = historyScore;
        this.totalScore = totalScore;
        this.totalTopic = totalTopic;
        this.error = error;
        this.error_code = error_code;
        this.request = request;
    }

    @Override
    public String toString() {
        if(this.error!=""){
            return "success:"+this.score;
        }else{
            return "error:"+this.error;
        }

    }
}

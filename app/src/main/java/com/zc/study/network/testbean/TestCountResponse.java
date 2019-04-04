package com.zc.study.network.testbean;

/**
 * @作者 zhouchao
 * @日期 2019/3/22
 * @描述
 */
public class TestCountResponse {

    private int errorCode;
    private String errorMessage;
    private String result;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TestCountResponse{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}

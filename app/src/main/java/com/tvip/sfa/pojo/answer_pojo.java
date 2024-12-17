package com.tvip.sfa.pojo;

public class answer_pojo {
    private final String szAnswerText;
    private final String szAnswerValue;

    public answer_pojo(String szAnswerText, String szAnswerValue) {
        this.szAnswerText = szAnswerText;
        this.szAnswerValue = szAnswerValue;

    }

    public String getSzAnswerText() {
        return szAnswerText;
    }

    public String getSzAnswerValue() {
        return szAnswerValue;
    }
}

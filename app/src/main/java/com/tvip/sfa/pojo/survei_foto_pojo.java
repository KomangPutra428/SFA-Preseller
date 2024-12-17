package com.tvip.sfa.pojo;

public class survei_foto_pojo {
    private final String intItemNumber;
    private final String questionItem;

    private final String szName;
    private final String bMandatory;
    private final String szAnswerType;
    private final String szQuestionId;
    private String foto;

    public survei_foto_pojo(String intItemNumber, String questionItem, String szName, String bMandatory, String szAnswerType, String szQuestionId) {
        this.intItemNumber = intItemNumber;
        this.questionItem = questionItem;
        this.szName = szName;
        this.bMandatory = bMandatory;
        this.szAnswerType = szAnswerType;
        this.szQuestionId = szQuestionId;
    }

    public String getIntItemNumber() {
        return intItemNumber;
    }

    public String getQuestionItem() {
        return questionItem;
    }

    public String getSzName() { return szName; }
    public String getbMandatory() {return bMandatory;}
    public String getSzAnswerType() {return szAnswerType;}

    public String getSzQuestionId() {
        return szQuestionId;
    }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}

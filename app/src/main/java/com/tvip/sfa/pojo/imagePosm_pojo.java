package com.tvip.sfa.pojo;

public class imagePosm_pojo {
    private final String iId;
    private final String szImage;
    private final String szSurveyId;

    public imagePosm_pojo(String iId, String szImage, String szSurveyId) {
        this.iId = iId;
        this.szImage = szImage;
        this.szSurveyId = szSurveyId;
    }

    public String getiId() {
        return iId;
    }

    public String getSzImage() {
        return szImage;
    }

    public String getSzSurveyId() {
        return szSurveyId;
    }
}

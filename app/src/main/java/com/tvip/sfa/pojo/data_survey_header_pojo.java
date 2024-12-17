package com.tvip.sfa.pojo;

public class data_survey_header_pojo {

    private String szId;
    private String szName;
    private String szDescription;

    public data_survey_header_pojo(String szId, String szName, String szDescription) {
        this.szId = szId;
        this.szName = szName;
        this.szDescription = szDescription;
    }

    public String getSzId() {
        return szId;
    }

    public String getSzName() {
        return szName;
    }

    public String getSzDescription() {
        return szDescription;
    }
}

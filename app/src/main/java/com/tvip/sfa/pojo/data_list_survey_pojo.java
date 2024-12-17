package com.tvip.sfa.pojo;

public class data_list_survey_pojo {
    private final String szDocId;
    private final String dtmDoc;
    private final String szName;
    private final String szAddress;


    public data_list_survey_pojo(String szDocId, String dtmDoc, String szName, String szAddress) {
        this.szDocId = szDocId;
        this.dtmDoc = dtmDoc;
        this.szName =  szName;
        this.szAddress = szAddress;
    }

    public String getSzDocId() {
        return szDocId;
    }

    public String getDtmDoc() {
        return dtmDoc;
    }

    public String getSzName() {
        return szName;
    }

    public String getSzAddress() {
        return szAddress;
    }
}

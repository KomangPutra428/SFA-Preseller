package com.tvip.sfa.pojo;

public class so_penjualan_pojo {
    private final String szDocId;
    private final String dtmDoc;
    private final String decAmount;

    public so_penjualan_pojo(String szDocId, String dtmDoc, String decAmount) {
        this.szDocId = szDocId;
        this.dtmDoc = dtmDoc;
        this.decAmount = decAmount;
    }

    public String getSzDocId() {return szDocId;}
    public String getDtmDoc() {return dtmDoc;}
    public String getDecAmount() {return decAmount;}
}

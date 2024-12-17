package com.tvip.sfa.pojo;

public class laporan_penjualan_pojo {
    private final String szDocId;
    private final String szCustomerId;
    private final String szName;

    public laporan_penjualan_pojo(String szDocId, String szCustomerId, String szName) {
        this.szDocId = szDocId;
        this.szCustomerId = szCustomerId;
        this.szName = szName;
    }

    public String getSzDocId() {return szDocId;}
    public String getSzCustomerId() {return szCustomerId;}
    public String getSzName() { return szName; }

}

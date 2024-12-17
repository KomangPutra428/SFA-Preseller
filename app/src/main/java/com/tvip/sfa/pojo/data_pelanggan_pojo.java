package com.tvip.sfa.pojo;

public class data_pelanggan_pojo {
    private final String szId;
    private final String szName;
    private final String szAddress;
    private final String szLangitude;
    private final String szLongitude;

    public data_pelanggan_pojo(String szId, String szName, String szAddress, String szLangitude, String szLongitude) {
        this.szId = szId;
        this.szName = szName;
        this.szAddress = szAddress;
        this.szLangitude = szLangitude;
        this.szLongitude = szLongitude;
    }

    public String getSzId() { return szId; }

    public String getSzName() { return szName; }
    public String getSzAddress() { return szAddress; }

    public String getSzLangitude() { return szLangitude; }
    public String getSzLongitude() { return szLongitude; }

}


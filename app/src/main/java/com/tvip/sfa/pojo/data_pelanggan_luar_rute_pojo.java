package com.tvip.sfa.pojo;

public class data_pelanggan_luar_rute_pojo {
    private final String szCustomerId;
    private final String szName;
    private final String szAddress;
    private final String szLatitude;
    private final String szLongitude;

    private final String bStarted;
    private final String bsuccess;
    private final String bScanBarcode;
    private final String bPostPone;

    public data_pelanggan_luar_rute_pojo(String szCustomerId, String szName, String szAddress, String szLatitude, String szLongitude, String bStarted, String bsuccess, String bScanBarcode, String bPostPone) {
        this.szCustomerId = szCustomerId;
        this.szName = szName;
        this.szAddress = szAddress;
        this.szLatitude = szLatitude;
        this.szLongitude = szLongitude;
        this.bStarted = bStarted;
        this.bsuccess = bsuccess;
        this.bScanBarcode = bScanBarcode;
        this.bPostPone = bPostPone;
    }

    public String getSzCustomerId() {
        return szCustomerId;
    }

    public String getSzName() {
        return szName;
    }

    public String getSzAddress() { return szAddress; }

    public String getSzLatitude() { return szLatitude; }

    public String getSzLongitude() { return szLongitude; }

    public String getbStarted() { return bStarted; }

    public String getBsuccess() {
        return bsuccess;
    }

    public String getbScanBarcode() {
        return bScanBarcode;
    }

    public String getbPostPone() {
        return bPostPone;
    }

    @Override
    public String toString() {
        return szName;
    }

}

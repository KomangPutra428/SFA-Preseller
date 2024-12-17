package com.tvip.sfa.pojo;

public class data_pelanggan_belum_finish_pojo {
    private final String szCustomerId;
    private final String szName;
    private final String szDocSO;
    private final String szAddress;
    private final String szLatitude;
    private final String szLongitude;
    private final String bStarted;
    private final String bsuccess;
    private final String bScanBarcode;
    private final String bPostPone;
    private final String szFailReason;
    private String reason;

    public data_pelanggan_belum_finish_pojo(String szCustomerId, String szName, String szDocSO, String szAddress, String szLatitude, String szLongitude, String bStarted, String bsuccess, String bScanBarcode, String bPostPone, String szFailReason) {
        this.szCustomerId = szCustomerId;
        this.szName = szName;
        this.szDocSO = szDocSO;
        this.szAddress = szAddress;
        this.szLatitude = szLatitude;
        this.szLongitude = szLongitude;
        this.bStarted = bStarted;
        this.bsuccess = bsuccess;
        this.bScanBarcode = bScanBarcode;
        this.bPostPone = bPostPone;
        this.szFailReason = szFailReason;
    }

    public String getSzCustomerId() {
        return szCustomerId;
    }

    public String getSzName() {
        return szName;
    }

    public String getSzDocSO() {return szDocSO;}

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

    public String getSzFailReason() { return szFailReason; }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

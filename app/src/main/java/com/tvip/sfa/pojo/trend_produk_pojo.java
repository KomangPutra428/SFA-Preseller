package com.tvip.sfa.pojo;

public class trend_produk_pojo {
    private final String szProductId;
    private final String szName;

    private final String minggu_1;
    private final String minggu_2;
    private final String minggu_3;
    private final String minggu_4;
    private final String minggu_5;
    private final String minggu_6;
    private final String minggu_7;
    private final String minggu_8;

    public trend_produk_pojo(String szProductId, String szName, String minggu_1, String minggu_2, String minggu_3,
            String minggu_4, String minggu_5, String minggu_6, String minggu_7, String minggu_8) {
        this.szProductId = szProductId;
        this.szName = szName;

        this.minggu_1 = minggu_1;
        this.minggu_2 = minggu_2;
        this.minggu_3 = minggu_3;
        this.minggu_4 = minggu_4;
        this.minggu_5 = minggu_5;
        this.minggu_6 = minggu_6;
        this.minggu_7 = minggu_7;
        this.minggu_8 = minggu_8;
    }

    public String getSzProductId() {
        return szProductId;
    }

    public String getSzName() {
        return szName;
    }

    public String getMinggu_1() {
        return minggu_1;
    }

    public String getMinggu_2() {
        return minggu_2;
    }

    public String getMinggu_3() {
        return minggu_3;
    }

    public String getMinggu_4() {
        return minggu_4;
    }

    public String getMinggu_5() {
        return minggu_5;
    }

    public String getMinggu_6() {
        return minggu_6;
    }

    public String getMinggu_7() {
        return minggu_7;
    }

    public String getMinggu_8() {
        return minggu_8;
    }
}

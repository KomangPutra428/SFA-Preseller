package com.tvip.sfa.pojo;

public class summary_SKU_pojo {
    private final String szName;
    private final String decQty;

    public summary_SKU_pojo(String szName, String decQty) {
        this.szName = szName;
        this.decQty = decQty;
    }

    public String getSzName() {return szName;}
    public String getDecQty() {return decQty;}
}

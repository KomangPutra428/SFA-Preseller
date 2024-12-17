package com.tvip.sfa.pojo;

public class data_product_pojo {
    private final String szName;
    private final String decQty;

    public data_product_pojo(String szName, String decQty) {
        this.szName = szName;
        this.decQty = decQty;
    }

    public String getSzName() { return szName; }
    public String getDecQty() {return decQty;}
}

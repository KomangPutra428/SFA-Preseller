package com.tvip.sfa.pojo;

public class data_product_potensial_pojo {
    private final String szId;
    private final String szName;
    private String qty;

    public data_product_potensial_pojo(String szId, String szName, String qty) {
        this.szId = szId;
        this.szName = szName;
        this.qty = qty;
    }

    public String getSzId() {return szId;}

    public String getSzName() { return szName; }

    public String getQty() {return qty;}
    public void setQty(String qty) {this.qty = qty;}
}

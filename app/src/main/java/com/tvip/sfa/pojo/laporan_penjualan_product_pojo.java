package com.tvip.sfa.pojo;

public class laporan_penjualan_product_pojo {
    private final String szName;
    private final String decQty;
    private final String decPrice;
    private final String decAmount;

    public laporan_penjualan_product_pojo(String szName, String decQty, String decPrice, String decAmount) {
        this.szName = szName;
        this.decQty = decQty;
        this.decPrice = decPrice;
        this.decAmount = decAmount;
    }
    public String getSzName() { return szName; }
    public String getDecQty() {return decQty;}
    public String getDecPrice() {return decPrice;}
    public String getDecAmount() {return decAmount;}
}

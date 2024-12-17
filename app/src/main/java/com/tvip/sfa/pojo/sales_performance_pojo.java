package com.tvip.sfa.pojo;

public class sales_performance_pojo {
    private final String decQty;
    private final String szName;
    private final String decAmount;

    public sales_performance_pojo(String decQty, String szName, String decAmount) {
        this.decQty = decQty;
        this.szName = szName;
        this.decAmount = decAmount;
    }

    public String getDecQty() {
        return decQty;
    }

    public String getSzName() {
        return szName;
    }

    public String getDecAmount() {
        return decAmount;
    }
}

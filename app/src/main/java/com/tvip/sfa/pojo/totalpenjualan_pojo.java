package com.tvip.sfa.pojo;

public class totalpenjualan_pojo {
    private final String decAmount;
    private final String decTax;
    private final String decDpp;
    private final String decDiscount;

    public totalpenjualan_pojo(String decAmount, String decTax, String decDpp, String decDiscount) {
        this.decAmount = decAmount;
        this.decTax = decTax;
        this.decDpp = decDpp;
        this.decDiscount = decDiscount;
    }

    public String getDecAmount() {
        return decAmount;
    }

    public String getDecTax() {
        return decTax;
    }

    public String getDecDpp() {
        return decDpp;
    }

    public String getDecDiscount() {
        return decDiscount;
    }
}

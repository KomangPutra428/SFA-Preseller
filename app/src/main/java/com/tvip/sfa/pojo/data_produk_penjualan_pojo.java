package com.tvip.sfa.pojo;

public class data_produk_penjualan_pojo {
    private final String szId;
    private final String szName;
    private final String szUomId;
    private final String decPrice;
    private final String szPriceSegmentId;
    private final String qty;

    private String stock;
    private String display;
    private String expired;
    private String expired_date;

    private String stock_qty;
    private String disc_principle;
    private String disc_distributor;
    private String disc_internal;
    private String disc_total;

    public data_produk_penjualan_pojo(String szId, String szName, String szUomId, String decPrice, String szPriceSegmentId, String qty) {
        this.szId = szId;
        this.szName = szName;
        this.szUomId = szUomId;
        this.decPrice = decPrice;
        this.szPriceSegmentId = szPriceSegmentId;
        this.qty = qty;
    }

    public String getSzId() { return szId; }
    public String getSzName() { return szName; }

    public String getSzUomId() {return szUomId;}
    public String getDecPrice() { return decPrice; }

    public String getSzPriceSegmentId() {return szPriceSegmentId;}

    public String getQty() {
        return qty;
    }

    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }

    public String getDisplay() { return display; }
    public void setDisplay(String display) { this.display = display; }

    public String getExpired() { return expired; }
    public void setExpired(String expired) { this.expired = expired; }

    public String getExpired_date() { return expired_date; }
    public void setExpired_date(String expired_date) { this.expired_date = expired_date; }

    public String getStock_qty() { return stock_qty; }
    public void setStock_qty(String stock_qty) { this.stock_qty = stock_qty; }

    public String getDisc_principle() { return disc_principle; }
    public void setDisc_principle(String disc_principle) { this.disc_principle = disc_principle; }

    public String getDisc_distributor() { return disc_distributor; }
    public void setDisc_distributor(String disc_distributor) { this.disc_distributor = disc_distributor; }

    public String getDisc_internal() { return disc_internal; }
    public void setDisc_internal(String disc_internal) { this.disc_internal = disc_internal; }

    public String getDisc_total() { return disc_total; }
    public void setDisc_total(String disc_total) { this.disc_total = disc_total; }

}

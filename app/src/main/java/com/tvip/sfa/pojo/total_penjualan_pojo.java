package com.tvip.sfa.pojo;

public class total_penjualan_pojo {
    private final String szId;
    private final String szName;
    private final String decPrice;
    private final String stock;
    private final String display;
    private final String jumlah_harga;
    private final String jumlah_diskon;
    private final String stockawal;
    private final String expired;
    private final String expired_date;
    private final String szUomId;
    private final String disc_distributor;
    private final String disc_internal;
    private final String disc_principle;

    private final String szPriceSegmentId;


    public total_penjualan_pojo(String szId, String szName,
                                String decPrice, String stock,
                                String display, String jumlah_harga,
                                String jumlah_diskon,
                                String stockawal, String expired, String expired_date,
                                String szUomId,
                                String disc_distributor, String disc_internal,
                                String disc_principle,
                                String szPriceSegmentId) {
        this.szId = szId;
        this.szName = szName;
        this.decPrice = decPrice;
        this.stock = stock;
        this.display = display;
        this.jumlah_harga = jumlah_harga;
        this.jumlah_diskon = jumlah_diskon;
        this.stockawal = stockawal;
        this.expired = expired;
        this.expired_date = expired_date;
        this.szUomId = szUomId;
        this.disc_distributor = disc_distributor;
        this.disc_internal = disc_internal;
        this.disc_principle = disc_principle;
        this.szPriceSegmentId = szPriceSegmentId;

    }

    public String getSzId() { return szId; }
    public String getSzName() { return szName; }
    public String getDecPrice() { return decPrice; }
    public String getStock() { return stock; }
    public String getDisplay() { return display; }
    public String getJumlah_harga() { return jumlah_harga; }
    public String getJumlah_diskon() { return jumlah_diskon; }

    public String getStockawal() {
        return stockawal;
    }

    public String getExpired() {
        return expired;
    }

    public String getExpired_date() {
        return expired_date;
    }

    public String getSzUomId() {return szUomId;}

    public String getDisc_distributor() {return disc_distributor;}
    public String getDisc_internal() {return disc_internal;}
    public String getDisc_principle() {return disc_principle;}

    public String getSzPriceSegmentId() {return szPriceSegmentId;}
}

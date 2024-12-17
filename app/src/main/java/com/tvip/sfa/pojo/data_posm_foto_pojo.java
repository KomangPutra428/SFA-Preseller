package com.tvip.sfa.pojo;

public class data_posm_foto_pojo {
    private final String szId;
    private final String szName;
    private final String qty;
    private String foto;
    private final String szUomId;

    public data_posm_foto_pojo(String szId, String szName, String qty, String foto, String szUomId) {
        this.szId = szId;
        this.szName = szName;
        this.qty = qty;
        this.foto = foto;
        this.szUomId = szUomId;
    }

    public String getSzId() { return szId; }
    public String getSzName() { return szName; }

    public String getQty() { return qty; }

    public String getSzUomId() {
        return szUomId;
    }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}

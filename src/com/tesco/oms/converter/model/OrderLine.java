package com.tesco.oms.converter.model;


public class OrderLine {
    private String PrimeLineNo;
    private String ItemID;
    private String Quantity;
    private String ProductClass;
    private String UOM;

    public String getPrimeLineNo() {
        return PrimeLineNo;
    }

    public void setPrimeLineNo(String primeLineNo) {
        PrimeLineNo = primeLineNo;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getProductClass() {
        return ProductClass;
    }

    public void setProductClass(String productClass) {
        ProductClass = productClass;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
}

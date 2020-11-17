package com.tesco.oms.converter.model;

public class Order {
    private String OrderNo;
    private String EnterpriseCode;
    private String ReleaseNo;
    private String OrderDate;
    private String ReqDeliveryDate;
    private String ShipNode;
    private OrderLine OrderLInes;

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getEnterpriseCode() {
        return EnterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        EnterpriseCode = enterpriseCode;
    }

    public String getReleaseNo() {
        return ReleaseNo;
    }

    public void setReleaseNo(String releaseNo) {
        ReleaseNo = releaseNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getReqDeliveryDate() {
        return ReqDeliveryDate;
    }

    public void setReqDeliveryDate(String reqDeliveryDate) {
        ReqDeliveryDate = reqDeliveryDate;
    }

    public String getShipNode() {
        return ShipNode;
    }

    public void setShipNode(String shipNode) {
        ShipNode = shipNode;
    }

    public OrderLine getOrderLInes() {
        return OrderLInes;
    }

    public void setOrderLInes(OrderLine orderLInes) {
        OrderLInes = orderLInes;
    }
}

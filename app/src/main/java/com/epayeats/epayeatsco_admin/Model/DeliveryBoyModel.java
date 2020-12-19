package com.epayeats.epayeatsco_admin.Model;

public class DeliveryBoyModel
{
    private String deliveryBoyID;
    private String deliveryBoyName;
    private String deliveryBoyEmail;
    private String deliveryBoyPassword;

    private String deliveyBoyLicence;
    private String deliveyBoyVechileNo;
    private String deliveyBoyMobileNo;
    private String photo;

    private String OnlineorOffline;
    private String deliveryBoyLocalAdminID;
    private String deliveryBoyLocalAdminName;
    private String deliveryBoyDeliveryCharge;

    private String isBlocked;
    private String temp1;
    private String temp2;

    public DeliveryBoyModel(){}

    public DeliveryBoyModel(String deliveryBoyID, String deliveryBoyName, String deliveryBoyEmail, String deliveryBoyPassword, String deliveyBoyLicence, String deliveyBoyVechileNo, String deliveyBoyMobileNo, String photo, String onlineorOffline, String deliveryBoyLocalAdminID, String deliveryBoyLocalAdminName, String deliveryBoyDeliveryCharge, String isBlocked, String temp1, String temp2) {
        this.deliveryBoyID = deliveryBoyID;
        this.deliveryBoyName = deliveryBoyName;
        this.deliveryBoyEmail = deliveryBoyEmail;
        this.deliveryBoyPassword = deliveryBoyPassword;
        this.deliveyBoyLicence = deliveyBoyLicence;
        this.deliveyBoyVechileNo = deliveyBoyVechileNo;
        this.deliveyBoyMobileNo = deliveyBoyMobileNo;
        this.photo = photo;
        OnlineorOffline = onlineorOffline;
        this.deliveryBoyLocalAdminID = deliveryBoyLocalAdminID;
        this.deliveryBoyLocalAdminName = deliveryBoyLocalAdminName;
        this.deliveryBoyDeliveryCharge = deliveryBoyDeliveryCharge;
        this.isBlocked = isBlocked;
        this.temp1 = temp1;
        this.temp2 = temp2;
    }

    public String getDeliveryBoyID() {
        return deliveryBoyID;
    }

    public void setDeliveryBoyID(String deliveryBoyID) {
        this.deliveryBoyID = deliveryBoyID;
    }

    public String getDeliveryBoyName() {
        return deliveryBoyName;
    }

    public void setDeliveryBoyName(String deliveryBoyName) {
        this.deliveryBoyName = deliveryBoyName;
    }

    public String getDeliveryBoyEmail() {
        return deliveryBoyEmail;
    }

    public void setDeliveryBoyEmail(String deliveryBoyEmail) {
        this.deliveryBoyEmail = deliveryBoyEmail;
    }

    public String getDeliveryBoyPassword() {
        return deliveryBoyPassword;
    }

    public void setDeliveryBoyPassword(String deliveryBoyPassword) {
        this.deliveryBoyPassword = deliveryBoyPassword;
    }

    public String getDeliveyBoyLicence() {
        return deliveyBoyLicence;
    }

    public void setDeliveyBoyLicence(String deliveyBoyLicence) {
        this.deliveyBoyLicence = deliveyBoyLicence;
    }

    public String getDeliveyBoyVechileNo() {
        return deliveyBoyVechileNo;
    }

    public void setDeliveyBoyVechileNo(String deliveyBoyVechileNo) {
        this.deliveyBoyVechileNo = deliveyBoyVechileNo;
    }

    public String getDeliveyBoyMobileNo() {
        return deliveyBoyMobileNo;
    }

    public void setDeliveyBoyMobileNo(String deliveyBoyMobileNo) {
        this.deliveyBoyMobileNo = deliveyBoyMobileNo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOnlineorOffline() {
        return OnlineorOffline;
    }

    public void setOnlineorOffline(String onlineorOffline) {
        OnlineorOffline = onlineorOffline;
    }

    public String getDeliveryBoyLocalAdminID() {
        return deliveryBoyLocalAdminID;
    }

    public void setDeliveryBoyLocalAdminID(String deliveryBoyLocalAdminID) {
        this.deliveryBoyLocalAdminID = deliveryBoyLocalAdminID;
    }

    public String getDeliveryBoyLocalAdminName() {
        return deliveryBoyLocalAdminName;
    }

    public void setDeliveryBoyLocalAdminName(String deliveryBoyLocalAdminName) {
        this.deliveryBoyLocalAdminName = deliveryBoyLocalAdminName;
    }

    public String getDeliveryBoyDeliveryCharge() {
        return deliveryBoyDeliveryCharge;
    }

    public void setDeliveryBoyDeliveryCharge(String deliveryBoyDeliveryCharge) {
        this.deliveryBoyDeliveryCharge = deliveryBoyDeliveryCharge;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }
}



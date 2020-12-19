package com.epayeats.epayeatsco_admin.Model;

public class RestaurantModel
{
    private String resID;
    private String resUserName;
    private String resEmail;
    private String resPassword;

    private String resLocalAdminID;
    private String resLocalAdminName;

    private String resLocation;
    private String isShopClosed;

    private String lat;
    private String lon;

    private String resOpenTime;
    private String resCloseTime;

    private String resLicenceNo;
    private String resGstNo;

    private String resName;
    private String resPhone;
    private String resPhoto;

    private String isBlocked;

    public RestaurantModel(){}

    public RestaurantModel(String resID, String resUserName, String resEmail, String resPassword, String resLocalAdminID, String resLocalAdminName, String resLocation, String isShopClosed, String lat, String lon, String resOpenTime, String resCloseTime, String resLicenceNo, String resGstNo, String resName, String resPhone, String resPhoto, String isBlocked) {
        this.resID = resID;
        this.resUserName = resUserName;
        this.resEmail = resEmail;
        this.resPassword = resPassword;
        this.resLocalAdminID = resLocalAdminID;
        this.resLocalAdminName = resLocalAdminName;
        this.resLocation = resLocation;
        this.isShopClosed = isShopClosed;
        this.lat = lat;
        this.lon = lon;
        this.resOpenTime = resOpenTime;
        this.resCloseTime = resCloseTime;
        this.resLicenceNo = resLicenceNo;
        this.resGstNo = resGstNo;
        this.resName = resName;
        this.resPhone = resPhone;
        this.resPhoto = resPhoto;
        this.isBlocked = isBlocked;
    }

    public String getResID() {
        return resID;
    }

    public void setResID(String resID) {
        this.resID = resID;
    }

    public String getResUserName() {
        return resUserName;
    }

    public void setResUserName(String resUserName) {
        this.resUserName = resUserName;
    }

    public String getResEmail() {
        return resEmail;
    }

    public void setResEmail(String resEmail) {
        this.resEmail = resEmail;
    }

    public String getResPassword() {
        return resPassword;
    }

    public void setResPassword(String resPassword) {
        this.resPassword = resPassword;
    }

    public String getResLocalAdminID() {
        return resLocalAdminID;
    }

    public void setResLocalAdminID(String resLocalAdminID) {
        this.resLocalAdminID = resLocalAdminID;
    }

    public String getResLocalAdminName() {
        return resLocalAdminName;
    }

    public void setResLocalAdminName(String resLocalAdminName) {
        this.resLocalAdminName = resLocalAdminName;
    }

    public String getResLocation() {
        return resLocation;
    }

    public void setResLocation(String resLocation) {
        this.resLocation = resLocation;
    }

    public String getIsShopClosed() {
        return isShopClosed;
    }

    public void setIsShopClosed(String isShopClosed) {
        this.isShopClosed = isShopClosed;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getResOpenTime() {
        return resOpenTime;
    }

    public void setResOpenTime(String resOpenTime) {
        this.resOpenTime = resOpenTime;
    }

    public String getResCloseTime() {
        return resCloseTime;
    }

    public void setResCloseTime(String resCloseTime) {
        this.resCloseTime = resCloseTime;
    }

    public String getResLicenceNo() {
        return resLicenceNo;
    }

    public void setResLicenceNo(String resLicenceNo) {
        this.resLicenceNo = resLicenceNo;
    }

    public String getResGstNo() {
        return resGstNo;
    }

    public void setResGstNo(String resGstNo) {
        this.resGstNo = resGstNo;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResPhone() {
        return resPhone;
    }

    public void setResPhone(String resPhone) {
        this.resPhone = resPhone;
    }

    public String getResPhoto() {
        return resPhoto;
    }

    public void setResPhoto(String resPhoto) {
        this.resPhoto = resPhoto;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }
}

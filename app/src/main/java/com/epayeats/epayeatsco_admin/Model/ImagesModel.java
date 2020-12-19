package com.epayeats.epayeatsco_admin.Model;

public class ImagesModel
{
    private String id;
    private String imageUrl;

    private String localAdminID;
    private String localAdminName;
    private String lat;
    private String lon;

    private String businessArea;
    private String businessKM;

    public ImagesModel(){}

    public ImagesModel(String id, String imageUrl, String localAdminID, String localAdminName, String lat, String lon, String businessArea, String businessKM) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.localAdminID = localAdminID;
        this.localAdminName = localAdminName;
        this.lat = lat;
        this.lon = lon;
        this.businessArea = businessArea;
        this.businessKM = businessKM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocalAdminID() {
        return localAdminID;
    }

    public void setLocalAdminID(String localAdminID) {
        this.localAdminID = localAdminID;
    }

    public String getLocalAdminName() {
        return localAdminName;
    }

    public void setLocalAdminName(String localAdminName) {
        this.localAdminName = localAdminName;
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

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getBusinessKM() {
        return businessKM;
    }

    public void setBusinessKM(String businessKM) {
        this.businessKM = businessKM;
    }
}

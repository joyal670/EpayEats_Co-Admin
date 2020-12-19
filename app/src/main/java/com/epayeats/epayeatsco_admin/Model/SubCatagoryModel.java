package com.epayeats.epayeatsco_admin.Model;

public class SubCatagoryModel
{
    private String subCatagoryID;
    private String subCatagoryName;

    private String mainCategoryName;
    private String mainCategoryID;

    public SubCatagoryModel()
    {
    }

    public SubCatagoryModel(String subCatagoryID, String subCatagoryName, String mainCategoryName, String mainCategoryID) {
        this.subCatagoryID = subCatagoryID;
        this.subCatagoryName = subCatagoryName;
        this.mainCategoryName = mainCategoryName;
        this.mainCategoryID = mainCategoryID;
    }

    public String getSubCatagoryID() {
        return subCatagoryID;
    }

    public void setSubCatagoryID(String subCatagoryID) {
        this.subCatagoryID = subCatagoryID;
    }

    public String getSubCatagoryName() {
        return subCatagoryName;
    }

    public void setSubCatagoryName(String subCatagoryName) {
        this.subCatagoryName = subCatagoryName;
    }

    public String getMainCategoryName() {
        return mainCategoryName;
    }

    public void setMainCategoryName(String mainCategoryName) {
        this.mainCategoryName = mainCategoryName;
    }

    public String getMainCategoryID() {
        return mainCategoryID;
    }

    public void setMainCategoryID(String mainCategoryID) {
        this.mainCategoryID = mainCategoryID;
    }
}

package com.epayeats.epayeatsco_admin.Model;

public class FoodCatagoryModel
{
    private String foodCatagoreyID;
    private String foodCatagoreyType;


    public FoodCatagoryModel(){}

    public FoodCatagoryModel(String foodCatagoreyID, String foodCatagoreyType) {
        this.foodCatagoreyID = foodCatagoreyID;
        this.foodCatagoreyType = foodCatagoreyType;
    }

    public String getFoodCatagoreyID() {
        return foodCatagoreyID;
    }

    public void setFoodCatagoreyID(String foodCatagoreyID) {
        this.foodCatagoreyID = foodCatagoreyID;
    }

    public String getFoodCatagoreyType() {
        return foodCatagoreyType;
    }

    public void setFoodCatagoreyType(String foodCatagoreyType) {
        this.foodCatagoreyType = foodCatagoreyType;
    }
}

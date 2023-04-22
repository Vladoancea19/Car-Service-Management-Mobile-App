package com.example.checkit.Models;

public class CarDamageInfoModel {
    private String description;
    private int cost;
    private String state;

    public CarDamageInfoModel(String description, int cost, String state) {
        this.description = description;
        this.cost = cost;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}

package com.example.checkit.Models;

public class MechanicRepairDynamicRvModel {
    String description, cost;

    public MechanicRepairDynamicRvModel(String description, String cost) {
        this.description = description;
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}

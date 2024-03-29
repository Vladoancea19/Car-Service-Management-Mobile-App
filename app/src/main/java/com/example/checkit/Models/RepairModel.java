package com.example.checkit.Models;

import java.util.ArrayList;

public class RepairModel {
    private String clientPhoneNumber, mechanicPhoneNumber, estimatedTime, state, mechanicOk, serviceNote, uniqueID;
    private CarInfoModel carInfo;
    private ArrayList<CarDamageInfoModel> carDamageInfoList;

    public RepairModel(String clientPhoneNumber, String mechanicPhoneNumber, CarInfoModel carInfo, ArrayList<CarDamageInfoModel> carDamageInfoList, String estimatedTime, String state, String mechanicOk, String serviceNote, String uniqueID) {
        this.clientPhoneNumber = clientPhoneNumber;
        this.mechanicPhoneNumber = mechanicPhoneNumber;
        this.carInfo = carInfo;
        this.carDamageInfoList = carDamageInfoList;
        this.estimatedTime = estimatedTime;
        this.state = state;
        this.mechanicOk = mechanicOk;
        this.serviceNote = serviceNote;
        this.uniqueID = uniqueID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getMechanicPhoneNumber() {
        return mechanicPhoneNumber;
    }

    public void setMechanicPhoneNumber(String mechanicPhoneNumber) {
        this.mechanicPhoneNumber = mechanicPhoneNumber;
    }

    public CarInfoModel getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfoModel carInfo) {
        this.carInfo = carInfo;
    }

    public ArrayList<CarDamageInfoModel> getCarDamageInfoList() {
        return carDamageInfoList;
    }

    public void setCarDamageInfoList(ArrayList<CarDamageInfoModel> carDamageInfoList) {
        this.carDamageInfoList = carDamageInfoList;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}

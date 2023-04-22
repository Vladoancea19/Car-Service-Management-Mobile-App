package com.example.checkit.Models;

public class CarInfoModel {
    private String plateNumber, carModel, manufactureYear, transmissionType, fuelType;

    public CarInfoModel(String plateNumber, String carModel, String manufactureYear, String transmissionType, String fuelType) {
        this.plateNumber = plateNumber;
        this.carModel = carModel;
        this.manufactureYear = manufactureYear;
        this.transmissionType = transmissionType;
        this.fuelType = fuelType;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(String manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}

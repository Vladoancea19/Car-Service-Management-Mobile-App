package com.example.checkit.Models;

public class MechanicDynamicRvModel {

    private int progress;
    private String plateNumber, carModel, progressText, uniqueID;

    public MechanicDynamicRvModel(int progress, String progressText, String plateNumber, String carModel, String uniqueID) {
        this.progress = progress;
        this.progressText = progressText;
        this.plateNumber = plateNumber;
        this.carModel = carModel;
        this.uniqueID = uniqueID;
    }

    public MechanicDynamicRvModel(String plateNumber, String carModel, String uniqueID) {
        this.plateNumber = plateNumber;
        this.carModel = carModel;
        this.uniqueID = uniqueID;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}

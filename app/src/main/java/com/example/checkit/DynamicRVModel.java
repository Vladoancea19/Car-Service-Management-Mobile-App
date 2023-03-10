package com.example.checkit;

public class DynamicRVModel {

    private int progress;
    private String plateNumber, carModel, progressText;

    public DynamicRVModel(int progress, String progressText, String plateNumber, String carModel) {
        this.progress = progress;
        this.progressText = progressText;
        this.plateNumber = plateNumber;
        this.carModel = carModel;
    }

    public int getProgress() {
        return progress;
    }

    public String getProgressText() {
        return progressText;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getCarModel() {
        return carModel;
    }
}

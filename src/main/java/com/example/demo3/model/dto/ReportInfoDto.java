package com.example.demo3.model.dto;

public class ReportInfoDto {
    private long vehicleId;
    private String type;
    private String stringDateFrom;
    private String stringDateTo;
    private String period;

    public ReportInfoDto(long vehicleId, String type, String stringDateFrom, String stringDateTo, String period) {
        this.vehicleId = vehicleId;
        this.type = type;
        this.stringDateFrom = stringDateFrom;
        this.stringDateTo = stringDateTo;
        this.period = period;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStringDateFrom() {
        return stringDateFrom;
    }

    public void setStringDateFrom(String stringDateFrom) {
        this.stringDateFrom = stringDateFrom;
    }

    public String getStringDateTo() {
        return stringDateTo;
    }

    public void setStringDateTo(String stringDateTo) {
        this.stringDateTo = stringDateTo;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}

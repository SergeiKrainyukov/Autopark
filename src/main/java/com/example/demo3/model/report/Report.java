package com.example.demo3.model.report;

import com.example.demo3.model.entity.TripEntity;

import java.util.List;

public class Report {
    private ReportType reportType;
    private ReportPeriod period;
    private long dateFrom;
    private long dateTo;
    private List<ReportResult> result;

    public Report() {
    }

    public Report(ReportPeriod period, long dateFrom, long dateTo) {
        this.period = period;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ReportPeriod period) {
        this.period = period;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    public List<ReportResult> getResult(List<TripEntity> allTripsByVehicleIdAndDates) {
        return result;
    }

    public void setResult(List<ReportResult> result) {
        this.result = result;
    }
}

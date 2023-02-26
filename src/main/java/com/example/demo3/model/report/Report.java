package com.example.demo3.model.report;

import java.util.List;

public class Report {
    private final ReportType reportType;
    private final ReportPeriod period;
    private final long dateFrom;
    private final long dateTo;
    private List<ReportResult> result;

    public Report(ReportType reportType, ReportPeriod period, long dateFrom, long dateTo) {
        this.reportType = reportType;
        this.period = period;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public ReportPeriod getPeriod() {
        return period;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public List<ReportResult> getResult() {
        return result;
    }

    public void setResult(List<ReportResult> result) {
        this.result = result;
    }
}

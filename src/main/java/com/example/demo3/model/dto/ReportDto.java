package com.example.demo3.model.dto;

import com.example.demo3.model.report.ReportResult;

import java.util.List;

public class ReportDto {
    private List<ReportResult> values;

    public ReportDto(List<ReportResult> values) {
        this.values = values;
    }

    public List<ReportResult> getValues() {
        return values;
    }

    public void setValues(List<ReportResult> values) {
        this.values = values;
    }
}

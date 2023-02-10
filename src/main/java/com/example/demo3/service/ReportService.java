package com.example.demo3.service;

import com.example.demo3.model.dto.ReportDto;
import com.example.demo3.model.dto.ReportInfoDto;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.model.report.MileageByPeriodReport;
import com.example.demo3.model.report.ReportPeriod;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringComponent
public class ReportService {

    public ReportDto getReport(ReportInfoDto reportInfoDto, List<TripEntity> tripsByVehicleIdAndDates) {
        switch (reportInfoDto.getType()) {
            default: {
                return new ReportDto(new MileageByPeriodReport(parseReportPeriod(reportInfoDto.getPeriod()), getLongDate(reportInfoDto.getStringDateFrom()), getLongDate(reportInfoDto.getStringDateTo())).getResult(tripsByVehicleIdAndDates));
            }
        }
    }

    private ReportPeriod parseReportPeriod(String period) {
        switch (period) {
            case "month":
                return ReportPeriod.MONTH;
            default:
                return ReportPeriod.DAY;
        }
    }

    private Long getLongDate(String date) {
        try {
            Date parsedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(date);
            return parsedDate.getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0L;
        }
    }
}

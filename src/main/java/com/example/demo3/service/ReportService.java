package com.example.demo3.service;

import com.example.demo3.model.dto.ReportDto;
import com.example.demo3.model.dto.ReportInfoDto;
import com.example.demo3.model.report.MileageByPeriodReport;
import com.example.demo3.model.report.ReportPeriod;
import com.example.demo3.repository.TripRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringComponent
public class ReportService {

    private final TripRepository tripRepository;

    @Autowired
    public ReportService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public ReportDto getReport(ReportInfoDto reportInfoDto) {
        switch (reportInfoDto.getType()) {
            default:
                return new ReportDto(new MileageByPeriodReport(parseReportPeriod(reportInfoDto.getPeriod()), getLongDate(reportInfoDto.getStringDateFrom()), getLongDate(reportInfoDto.getStringDateTo()), reportInfoDto.getVehicleId(), tripRepository).getResult());
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

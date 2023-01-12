package com.example.demo3.model.report;

import com.example.demo3.repository.VehiclesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class MileageByPeriodReport extends Report {

    @Autowired
    private VehiclesRepository vehiclesRepository;

    private Long vehicleId;

    public MileageByPeriodReport() {
    }

    public MileageByPeriodReport(ReportPeriod period, long dateFrom, long dateTo, Long vehicleId) {
        super(period, dateFrom, dateTo);
        this.vehicleId = vehicleId;
        setReportType(ReportType.MILEAGE_BY_PERIOD);
    }

    //TODO: get result for month and year
    @Override
    public List<ReportResult> getResult() {
        List<ReportResult> reportResults = new ArrayList<>();
        switch (getPeriod()) {
            case DAY: {
                Date dateFrom = new Date(getDateFrom());
                Date dateTo = new Date(getDateTo());

                Calendar calendarFrom = new GregorianCalendar();
                calendarFrom.setTime(dateFrom);

                Calendar calendarTo = new GregorianCalendar();
                calendarTo.setTime(dateTo);

                LocalDate startDate = LocalDate.of(calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH) + 1, calendarFrom.get(Calendar.DAY_OF_MONTH));
                LocalDate endDate = LocalDate.of(calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH) + 1, calendarTo.get(Calendar.DAY_OF_MONTH));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

                long difference = getDateTo() - getDateFrom();
                long daysCount = difference / (24 * 60 * 60 * 1000);

                for (int i = 0; i < daysCount; i++) {
                    reportResults.add(new ReportResult(startDate.plusDays(i).format(formatter), "111"));
                }
                break;
            }
            case MONTH: {
                break;
            }
            case YEAR: {
                break;
            }
        }

        return reportResults;
    }
}

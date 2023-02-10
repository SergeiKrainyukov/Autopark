package com.example.demo3.model.report;

import com.example.demo3.model.entity.TripEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.demo3.common.Constants.DATE_FORMAT_PATTERN_BASE;

public class MileageByPeriodReport extends Report {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN_BASE);
    private final Calendar calendarFrom = new GregorianCalendar();
    private final Calendar calendarTo = new GregorianCalendar();

    public MileageByPeriodReport(ReportPeriod period, long dateFrom, long dateTo) {
        super(period, dateFrom, dateTo);
        setReportType(ReportType.MILEAGE_BY_PERIOD);
    }

    @Override
    public List<ReportResult> getResult(List<TripEntity> tripsByVehicleIdAndDates) {
        List<ReportResult> reportResults = new ArrayList<>();
        int totalDistance = calculateDistance(tripsByVehicleIdAndDates);

        calendarFrom.setTime(new Date(getDateFrom()));
        calendarTo.setTime(new Date(getDateTo()));

        LocalDate startDate = LocalDate.of(calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH) + 1, calendarFrom.get(Calendar.DAY_OF_MONTH));

        long difference = getDateTo() - getDateFrom();

        switch (getPeriod()) {
            case DAY: {
                long daysCount = difference / (24 * 60 * 60 * 1000);
                for (int i = 0; i < daysCount; i++) {
                    reportResults.add(new ReportResult(startDate.plusDays(i).format(formatter), "" + (totalDistance + 0.0) / daysCount));
                }
                break;
            }
            case MONTH: {
                long monthsCount = difference / (30L * 24 * 60 * 60 * 1000);
                for (int i = 0; i < monthsCount; i++) {
                    reportResults.add(new ReportResult(startDate.plusMonths(i).format(formatter), "" + (totalDistance + 0.0) / monthsCount));
                }
                break;
            }
        }
        return reportResults;
    }

    private int calculateDistance(List<TripEntity> tripsByVehicleIdAndDates) {
        int distanceSum = 0;
        for (TripEntity tripEntity : tripsByVehicleIdAndDates) {
            distanceSum += tripEntity.getDistance();
        }
        return distanceSum;
    }
}

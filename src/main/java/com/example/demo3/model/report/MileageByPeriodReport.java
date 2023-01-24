package com.example.demo3.model.report;

import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.repository.TripRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringComponent
public class MileageByPeriodReport extends Report {

    private TripRepository tripRepository;

    private Long vehicleId;

    public MileageByPeriodReport() {
    }

    public MileageByPeriodReport(ReportPeriod period, long dateFrom, long dateTo, Long vehicleId, TripRepository tripRepository) {
        super(period, dateFrom, dateTo);
        this.vehicleId = vehicleId;
        setReportType(ReportType.MILEAGE_BY_PERIOD);
        this.tripRepository = tripRepository;
    }

    @Override
    public List<ReportResult> getResult() {
        List<ReportResult> reportResults = new ArrayList<>();
        int totalDistance = calculateDistance(getDateFrom(), getDateTo(), vehicleId);

        Date dateFrom = new Date(getDateFrom());
        Date dateTo = new Date(getDateTo());

        Calendar calendarFrom = new GregorianCalendar();
        calendarFrom.setTime(dateFrom);

        Calendar calendarTo = new GregorianCalendar();
        calendarTo.setTime(dateTo);

        LocalDate startDate = LocalDate.of(calendarFrom.get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH) + 1, calendarFrom.get(Calendar.DAY_OF_MONTH));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

    private int calculateDistance(long dateFrom, long dateTo, Long vehicleId) {
        int distanceSum = 0;
        List<TripEntity> tripEntities = tripRepository.getAllByVehicleIdAndDates(vehicleId, dateFrom, dateTo);
        for (TripEntity tripEntity : tripEntities) {
            distanceSum += tripEntity.getDistance();
        }
        return distanceSum;
    }
}

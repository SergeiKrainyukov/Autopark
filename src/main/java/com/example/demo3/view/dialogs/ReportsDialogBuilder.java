package com.example.demo3.view.dialogs;

import com.example.demo3.model.report.ReportPeriod;
import com.example.demo3.model.report.ReportType;
import com.example.demo3.view.dialogs.helpers.GetTripEntitiesHelper;
import com.example.demo3.view.dialogs.helpers.GetVehicleIdByStateNumber;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.time.ZoneId;

import static com.example.demo3.common.Strings.OK_BUTTON;
import static com.example.demo3.common.Strings.REPORTS_TITLE;

@SpringComponent
@UIScope
public class ReportsDialogBuilder {

    private static final String REPORT_TYPE_LABEL = "Report type: ";
    private static final String REPORT_PERIOD_LABEL = "Report period: ";
    private static final String VEHICLE_STATE_NUMBER_LABEL = "State Number: ";
    private static final String FROM_LABEL = "From";
    private static final String TO_LABEL = "To";
    private final TextField stateNumberField = new TextField(VEHICLE_STATE_NUMBER_LABEL);
    private final DatePicker datePickerFrom = new DatePicker(FROM_LABEL);
    private final DatePicker datePickerTo = new DatePicker(TO_LABEL);
    private final ComboBox<ReportPeriod> reportPeriodComboBox = new ComboBox<>(REPORT_PERIOD_LABEL);
    private GetVehicleIdByStateNumber getVehicleIdByStateNumber;
    private GetTripEntitiesHelper getTripEntitiesHelper;

    private final ComponentRenderer<Component, ReportType> driverEntityComponentRenderer = new ComponentRenderer<>(
            reportType -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.add(new Text(REPORT_TYPE_LABEL + reportType));
                cardLayout.add(infoLayout);
                cardLayout.addClickListener(event -> {
                    if (stateNumberField.getValue().isEmpty() || stateNumberField.getValue().isBlank()) return;
                    switch (reportType) {
                        case MILEAGE_BY_PERIOD: {
                            try {
                                if (datePickerFrom.getValue() == null || datePickerTo.getValue() == null) return;
                                long dateFrom = datePickerFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                long dateTo = datePickerTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                Long vehicleId = getVehicleIdByStateNumber.getVehicleId(Integer.parseInt(stateNumberField.getValue()));
                                if (vehicleId < 0) return;
                                new MileageByPeriodReportDialogBuilder().createDialogForShowingReports(dateFrom, dateTo, reportPeriodComboBox.getValue(), getTripEntitiesHelper.getTripEntities(vehicleId, dateFrom, dateTo));
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                return;
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                });
                return cardLayout;
            });

    public ReportsDialogBuilder(GetVehicleIdByStateNumber getVehicleIdByStateNumber, GetTripEntitiesHelper getTripEntitiesHelper) {
        this.getVehicleIdByStateNumber = getVehicleIdByStateNumber;
        this.getTripEntitiesHelper = getTripEntitiesHelper;
    }

    public void createDialogForShowingReports() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(REPORTS_TITLE);

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout() {
        VirtualList<ReportType> list = new VirtualList<>();
        list.setItems(ReportType.values());
        list.setRenderer(driverEntityComponentRenderer);

        reportPeriodComboBox.setItems(ReportPeriod.values());
        reportPeriodComboBox.setItemLabelGenerator(ReportPeriod::name);
        reportPeriodComboBox.setValue(ReportPeriod.DAY);

        VerticalLayout dialogLayout = new VerticalLayout(stateNumberField, datePickerFrom, datePickerTo, reportPeriodComboBox, list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}

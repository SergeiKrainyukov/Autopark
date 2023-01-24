package com.example.demo3.view.dialogs;

import com.example.demo3.model.report.ReportType;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;

import static com.example.demo3.common.Strings.OK_BUTTON;
import static com.example.demo3.common.Strings.REPORTS_TITLE;

@SpringComponent
@UIScope
public class ReportsDialogBuilder {

    private static final String REPORT_TYPE_LABEL = "Report type: ";
    private static final String VEHICLE_ID_LABEL = "VehicleId: ";
    private static final String FROM_LABEL = "From";
    private static final String TO_LABEL = "To";
    private TextField textField = new TextField(VEHICLE_ID_LABEL);
    private DatePicker datePickerFrom = new DatePicker(FROM_LABEL);
    private DatePicker datePickerTo = new DatePicker(TO_LABEL);

    private MileageByPeriodReportDialogBuilder mileageByPeriodReportDialogBuilder;

    private final ComponentRenderer<Component, ReportType> driverEntityComponentRenderer = new ComponentRenderer<>(
            reportType -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.add(new Text(REPORT_TYPE_LABEL + reportType));
                cardLayout.add(infoLayout);
                cardLayout.addClickListener((ComponentEventListener<ClickEvent<HorizontalLayout>>) horizontalLayoutClickEvent -> {
                    if (textField.getValue().isEmpty() || textField.getValue().isBlank()) return;
                    switch (reportType) {
                        case MILEAGE_BY_PERIOD: {
                            try {
                                long dateFrom = datePickerFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                long dateTo = datePickerTo.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                mileageByPeriodReportDialogBuilder.createDialogForShowingReports(Long.parseLong(textField.getValue()), dateFrom, dateTo);
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

    @Autowired
    public ReportsDialogBuilder(MileageByPeriodReportDialogBuilder mileageByPeriodReportDialogBuilder) {
        this.mileageByPeriodReportDialogBuilder = mileageByPeriodReportDialogBuilder;
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

        VerticalLayout dialogLayout = new VerticalLayout(textField, datePickerFrom, datePickerTo, list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}

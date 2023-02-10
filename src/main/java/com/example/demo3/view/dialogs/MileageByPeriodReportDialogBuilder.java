package com.example.demo3.view.dialogs;

import com.example.demo3.controller.DatabaseController;
import com.example.demo3.model.report.MileageByPeriodReport;
import com.example.demo3.model.report.ReportPeriod;
import com.example.demo3.model.report.ReportResult;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.demo3.common.Strings.OK_BUTTON;

@SpringComponent
@UIScope
public class MileageByPeriodReportDialogBuilder {

    private final DatabaseController databaseController;

    private final ComponentRenderer<Component, ReportResult> driverEntityComponentRenderer = new ComponentRenderer<>(
            result -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.add(new Text("Date: " + result.getTime()));
                infoLayout.add(new Div(new Text("Mileage: " + result.getValue() + " km")));
                cardLayout.add(infoLayout);
                return cardLayout;
            });

    @Autowired
    public MileageByPeriodReportDialogBuilder(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public void createDialogForShowingReports(Long vehicleId, long dateFrom, long dateTo, ReportPeriod reportPeriod) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Mileage By " + reportPeriod.name() + " Report");

        dialog.add(createDialogLayout(vehicleId, dateFrom, dateTo, reportPeriod));

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Long vehicleId, long dateFrom, long dateTo, ReportPeriod reportPeriod) {

        VirtualList<ReportResult> list = new VirtualList<>();
        list.setItems(getResults(vehicleId, dateFrom, dateTo, reportPeriod));
        list.setRenderer(driverEntityComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    //TODO: Нужно поправить работу с датами, чтобы было с 0:00, а не с текущего времени
    private List<ReportResult> getResults(Long vehicleId, long dateFrom, long dateTo, ReportPeriod reportPeriod) {
        return new MileageByPeriodReport(reportPeriod, dateFrom, dateTo).getResult(databaseController.getAllTripsByVehicleIdAndDates(vehicleId, dateFrom, dateTo));
    }


}

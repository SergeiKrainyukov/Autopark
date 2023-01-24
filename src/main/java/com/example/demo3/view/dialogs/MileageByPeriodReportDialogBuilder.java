package com.example.demo3.view.dialogs;

import com.example.demo3.model.report.MileageByPeriodReport;
import com.example.demo3.model.report.ReportPeriod;
import com.example.demo3.model.report.ReportResult;
import com.example.demo3.repository.TripRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo3.common.Strings.OK_BUTTON;

@SpringComponent
@UIScope
public class MileageByPeriodReportDialogBuilder {

    private final TripRepository tripRepository;

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
    public MileageByPeriodReportDialogBuilder(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public void createDialogForShowingReports(Long vehicleId, long dateFrom, long dateTo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Mileage By Period Report");

        dialog.add(optionsLayout(), createDialogLayout(vehicleId, dateFrom, dateTo));

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }


    //TODO: remove all fields
    private VerticalLayout optionsLayout() {

        VerticalLayout verticalLayout = new VerticalLayout();

        HorizontalLayout dateFields = new HorizontalLayout();
        DatePicker startDatePicker = new DatePicker("From");
        DatePicker endDatePicker = new DatePicker("To");
        dateFields.add(startDatePicker, endDatePicker);

        HorizontalLayout otherOptions = new HorizontalLayout();

        ComboBox<ReportPeriod> reportPeriodComboBox = new ComboBox<>("Report Period");
        reportPeriodComboBox.setItems(ReportPeriod.values());
        reportPeriodComboBox.setItemLabelGenerator(ReportPeriod::name);

        TextField vehicleNumberTextField = new TextField("Vehicle Number");

        Button applyButton = new Button("Apply");

        otherOptions.add(reportPeriodComboBox, vehicleNumberTextField, applyButton);

        verticalLayout.add(dateFields, otherOptions);
        return verticalLayout;
    }

    private VerticalLayout createDialogLayout(Long vehicleId, long dateFrom, long dateTo) {

        VirtualList<ReportResult> list = new VirtualList<>();
        list.setItems(getResults(vehicleId, dateFrom, dateTo));
        list.setRenderer(driverEntityComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private List<ReportResult> getResults(Long vehicleId, long dateFrom, long dateTo) {
        return new ArrayList<>(new MileageByPeriodReport(ReportPeriod.DAY, dateFrom, dateTo, vehicleId, tripRepository).getResult());
    }


}

package com.example.demo3.view.dialogs;

import com.example.demo3.model.dto.TripDto;
import com.example.demo3.view.dialogs.helpers.GetGeoPointsHelper;
import com.example.demo3.view.dialogs.helpers.GetTripsDtoHelper;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.time.ZoneOffset;

import static com.example.demo3.common.Strings.OK_BUTTON;
import static com.example.demo3.common.Strings.RELOAD_BUTTON;

@SpringComponent
@UIScope
public class ShowAllTripsDialogBuilder {

    private static final String ROUTES_TITLE = "Trips";
    private static final String START_PLACE = "Start place";
    private static final String END_PLACE = "End place";
    private static final String START_DATE = "Start Date";
    private static final String END_DATE = "End Date";
    private static final String TRIP_TITLE = "----------Trip----------";
    public static final long DEFAULT_TO_TIME = 99999999999999L;
    public static final long DEFAULT_DAY_MILLIS = 86400000;

    private final VirtualList<TripDto> list = new VirtualList<>();
    private GetGeoPointsHelper getGeoPointsHelper;
    private long vehicleId;

    private final ComponentRenderer<Component, TripDto> tripComponentRenderer = new ComponentRenderer<>(
            tripDto -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.getElement().appendChild(ElementFactory.createStrong(TRIP_TITLE));
                infoLayout.getElement().appendChild(ElementFactory.createStrong(START_DATE));
                infoLayout.add(new Div(new Text(tripDto.getStartDate())));
                infoLayout.getElement().appendChild(ElementFactory.createStrong(END_DATE));
                infoLayout.add(new Div(new Text(tripDto.getEndDate())));
                infoLayout.getElement().appendChild(ElementFactory.createStrong(START_PLACE));
                infoLayout.add(new Div(new Text(tripDto.getStartPlace().getPlaceName())));
                infoLayout.getElement().appendChild(ElementFactory.createStrong(END_PLACE));
                infoLayout.add(new Div(new Text(tripDto.getEndPlace().getPlaceName())));
                cardLayout.add(infoLayout);
                cardLayout.addClickListener((ComponentEventListener<ClickEvent<HorizontalLayout>>) horizontalLayoutClickEvent -> new ShowTripOnMapDialogBuilder().createDialog(getGeoPointsHelper.getGeoPointEntities(vehicleId, tripDto.getStartDateMillis(), tripDto.getEndDateMillis())));
                return cardLayout;
            });

    public ShowAllTripsDialogBuilder(GetGeoPointsHelper getGeoPointsHelper, long vehicleId) {
        this.getGeoPointsHelper = getGeoPointsHelper;
        this.vehicleId = vehicleId;
    }

    public void createTripsDialog(GetTripsDtoHelper getTripsDtoHelper) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(ROUTES_TITLE);

        VerticalLayout dialogLayout = createDialogLayout(vehicleId, getTripsDtoHelper);

        DatePicker startDatePicker = new DatePicker(START_DATE);
        DatePicker endDatePicker = new DatePicker(END_DATE);
        Button reloadButton = new Button(RELOAD_BUTTON);
        reloadButton.addClickListener(event -> {
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) return;
            long startDate = startDatePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
            long endDate = endDatePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
            if (startDate == endDate) {
                list.setItems(getTripsDtoHelper.getTrips(vehicleId, startDate, endDate + DEFAULT_DAY_MILLIS - 1));
            } else list.setItems(getTripsDtoHelper.getTrips(vehicleId, startDate, endDate));
        });
        dialog.add(startDatePicker, endDatePicker, reloadButton, dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());
        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Long vehicleId, GetTripsDtoHelper getTripsDtoHelper) {
        list.setItems(getTripsDtoHelper.getTrips(vehicleId, 0, DEFAULT_TO_TIME));
        list.setRenderer(tripComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}

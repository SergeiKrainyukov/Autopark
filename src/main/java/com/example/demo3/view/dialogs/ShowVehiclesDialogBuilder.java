package com.example.demo3.view.dialogs;

import com.example.demo3.model.entity.BrandEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.view.EnterpriseUi;
import com.example.demo3.view.dialogs.helpers.*;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.ElementFactory;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.example.demo3.common.Strings.OK_BUTTON;
import static com.example.demo3.common.Strings.VEHICLES_TITLE;

public class ShowVehiclesDialogBuilder {
    private static final String STATE_NUMBER = "State number: ";
    private static final String YEAR = "Year: ";
    private static final String PRICE = "Price: ";
    private static final String MILEAGE = "Mileage: ";
    private static final String PURCHASE_DATE = "Purchase date: ";
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final String FOR = " for ";

    private final List<BrandEntity> brandEntities;
    private final List<VehicleEntity> vehicleEntities;
    private final VehiclesDialogOperations vehiclesDialogOperations;

    public ShowVehiclesDialogBuilder(List<BrandEntity> brandEntities, List<VehicleEntity> vehicleEntities, VehiclesDialogOperations vehiclesDialogOperations) {
        this.brandEntities = brandEntities;
        this.vehiclesDialogOperations = vehiclesDialogOperations;
        this.vehicleEntities = vehicleEntities;
    }

    public void createDialogForShowingVehicles(EnterpriseUi enterpriseUi) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(VEHICLES_TITLE + FOR + enterpriseUi.getName());

        VerticalLayout dialogLayout = createDialogLayout(dialog);
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Dialog dialog) {
        VirtualList<VehicleEntity> list = new VirtualList<>();
        list.setItems(vehicleEntities);
        list.setRenderer(getVehicleComponentRenderer(dialog));

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.START);
        dialogLayout.getStyle().set("width", "30rem").set("max-width", "100%");

        return dialogLayout;
    }

    //TODO: ?????????????? ?????????????????? ???????????? ?????? ???????????????? ?? ???????????????????? ????????
    private Renderer<VehicleEntity> getVehicleComponentRenderer(Dialog dialog) {
        return new ComponentRenderer<>(
                vehicle -> {
                    HorizontalLayout cardLayout = new HorizontalLayout();

                    VerticalLayout infoLayout = new VerticalLayout();
                    infoLayout.setSpacing(false);
                    infoLayout.getElement().appendChild(
                            ElementFactory.createStrong(STATE_NUMBER + vehicle.getStateNumber()));
                    infoLayout.add(new Div(new Text(YEAR + vehicle.getYear().toString())));
                    infoLayout.add(new Div(new Text(PRICE + vehicle.getPrice().toString())));
                    infoLayout.add(new Div(new Text(MILEAGE + vehicle.getMileage().toString())));
                    infoLayout.add(new Div(new Text(PURCHASE_DATE + getPurchaseDate(vehicle))));

                    VerticalLayout actionsLayout = new VerticalLayout();
                    actionsLayout.setSpacing(false);
                    actionsLayout.setAlignItems(FlexComponent.Alignment.END);

                    Button routesButton = new Button(new Icon(VaadinIcon.ROAD));
                    routesButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                        new ShowAllTripsDialogBuilder(vehiclesDialogOperations.getGetGeoPointsHelper(), vehicle.getId()).createTripsDialog(vehiclesDialogOperations.getGetTripsDtoHelper());
                        dialog.close();
                    });
                    Button updateVehicleButton = new Button(new Icon(VaadinIcon.PENCIL));
                    updateVehicleButton.addClickListener(event -> {
                        new CRUDVehicleDialogBuilder(brandEntities).createDialogForUpdateVehicle(vehicle, vehiclesDialogOperations.getSaveVehicle());
                        dialog.close();
                    });
                    Button deleteVehicleButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        vehiclesDialogOperations.getDeleteVehicle().delete(vehicle);
                        dialog.close();
                    });
                    deleteVehicleButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    actionsLayout.add(routesButton, updateVehicleButton, deleteVehicleButton);

                    cardLayout.add(infoLayout, actionsLayout);
                    return cardLayout;
                });
    }

    private String getPurchaseDate(VehicleEntity vehicleEntity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(vehicleEntity.getPurchaseDate());

        Time time = new Time(calendar.getTimeInMillis());

        DateFormat timeFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        return timeFormat.format(time);
    }
}

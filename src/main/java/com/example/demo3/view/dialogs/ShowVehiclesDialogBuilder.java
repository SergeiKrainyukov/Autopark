package com.example.demo3.view.dialogs;

import com.example.demo3.model.entity.BrandEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.view.EnterpriseUi;
import com.example.demo3.view.dialogs.helpers.DeleteVehicle;
import com.example.demo3.view.dialogs.helpers.GetGeoPointsHelper;
import com.example.demo3.view.dialogs.helpers.GetTripsHelper;
import com.example.demo3.view.dialogs.helpers.SaveVehicle;
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

    public ShowVehiclesDialogBuilder(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }

    public void createDialogForShowingVehicles(EnterpriseUi enterpriseUi, List<VehicleEntity> vehicleEntities, SaveVehicle saveVehicle, DeleteVehicle deleteVehicle, GetTripsHelper getTripsHelper, GetGeoPointsHelper getGeoPointsHelper) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(VEHICLES_TITLE + FOR + enterpriseUi.getName());

        VerticalLayout dialogLayout = createDialogLayout(dialog, vehicleEntities, saveVehicle, deleteVehicle, getTripsHelper, getGeoPointsHelper);
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Dialog dialog, List<VehicleEntity> vehicleEntities, SaveVehicle saveVehicle, DeleteVehicle deleteVehicle, GetTripsHelper getTripsHelper, GetGeoPointsHelper getGeoPointsHelper) {
        VirtualList<VehicleEntity> list = new VirtualList<>();
        list.setItems(vehicleEntities);
        list.setRenderer(getVehicleComponentRenderer(dialog, saveVehicle, deleteVehicle, getTripsHelper, getGeoPointsHelper));

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.START);
        dialogLayout.getStyle().set("width", "30rem").set("max-width", "100%");

        return dialogLayout;
    }

    //TODO: сделать отдельный объект для операций в диалоговом окне
    private Renderer<VehicleEntity> getVehicleComponentRenderer(Dialog dialog, SaveVehicle saveVehicle, DeleteVehicle deleteVehicle, GetTripsHelper getTripsHelper, GetGeoPointsHelper getGeoPointsHelper) {
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
                        new ShowAllTripsDialogBuilder(getGeoPointsHelper, vehicle.getId()).createTripsDialog(getTripsHelper);
                        dialog.close();
                    });
                    Button updateVehicleButton = new Button(new Icon(VaadinIcon.PENCIL));
                    updateVehicleButton.addClickListener(event -> {
                        new CRUDVehicleDialogBuilder(brandEntities).createDialogForUpdateVehicle(vehicle, saveVehicle);
                        dialog.close();
                    });
                    Button deleteVehicleButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        deleteVehicle.delete(vehicle);
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

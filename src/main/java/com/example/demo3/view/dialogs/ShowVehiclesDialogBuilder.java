package com.example.demo3.view.dialogs;

import com.example.demo3.controller.DatabaseController;
import com.example.demo3.model.entity.BrandEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.BrandsRepository;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.service.TripService;
import com.example.demo3.view.EnterpriseUi;
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
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.demo3.common.Strings.OK_BUTTON;
import static com.example.demo3.common.Strings.VEHICLES_TITLE;

@SpringComponent
@UIScope
public class ShowVehiclesDialogBuilder {

    private static final String STATE_NUMBER = "State number: ";
    private static final String YEAR = "Year: ";
    private static final String PRICE = "Price: ";
    private static final String MILEAGE = "Mileage: ";
    private static final String PURCHASE_DATE = "Purchase date: ";
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final String FOR = " for ";

    private final VehiclesRepository vehiclesRepository;
    private final BrandsRepository brandsRepository;
    private final TripService tripService;
    private final GeoPointRepository geoPointRepository;
    private final DatabaseController databaseController;

    @Autowired
    public ShowVehiclesDialogBuilder(VehiclesRepository vehiclesRepository, BrandsRepository brandsRepository, TripService tripService, GeoPointRepository geoPointRepository, DatabaseController databaseController) {
        this.vehiclesRepository = vehiclesRepository;
        this.brandsRepository = brandsRepository;
        this.tripService = tripService;
        this.geoPointRepository = geoPointRepository;
        this.databaseController = databaseController;
    }

    public void createDialogForShowingVehicles(EnterpriseUi enterpriseUi) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(VEHICLES_TITLE + FOR + enterpriseUi.getName());

        VerticalLayout dialogLayout = createDialogLayout(enterpriseUi.getId(), dialog);
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Long enterpriseId, Dialog dialog) {

        VirtualList<VehicleEntity> list = new VirtualList<>();
        list.setItems(getVehicles(enterpriseId));
        list.setRenderer(getVehicleComponentRenderer(dialog));

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.START);
        dialogLayout.getStyle().set("width", "30rem").set("max-width", "100%");

        return dialogLayout;
    }

    private List<VehicleEntity> getVehicles(Long enterpriseId) {
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehiclesRepository.findAll()) {
            vehicleEntities.add(vehicleEntity);
        }
        vehicleEntities.removeIf(vehicleEntity -> !vehicleEntity.getEnterpriseId().equals(enterpriseId));
        return vehicleEntities;
    }

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
                        new ShowAllTripsDialogBuilder(vehicle.getId(), tripService, geoPointRepository, databaseController).createTripsDialog();
                        dialog.close();
                    });
                    Button updateVehicleButton = new Button(new Icon(VaadinIcon.PENCIL));
                    updateVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        List<BrandEntity> brandEntities = new ArrayList<>();
                        brandsRepository.findAll().forEach(brandEntities::add);
                        new CRUDVehicleDialogBuilder(brandEntities).createDialogForUpdateVehicle(vehicle, vehicleEntity -> {
                            //TODO save vehicle
                        });
                        dialog.close();
                    });
                    Button deleteVehicleButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                        vehiclesRepository.delete(vehicle);
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

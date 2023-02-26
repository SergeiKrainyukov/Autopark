package com.example.demo3.view;

import com.example.demo3.controller.DatabaseController;
import com.example.demo3.model.entity.DriverEntity;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.security.SecurityService;
import com.example.demo3.view.dialogs.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo3.common.Strings.*;

@Route("")
@RolesAllowed(ROLE_MANAGER)
public class MainView extends VerticalLayout {

    private final static String HELLO_GREETING = "Hello, ";
    private final static String HEADER_WIDTH = "100%";

    private final SecurityService securityService;
    private final DatabaseController databaseController;

    private final Grid<EnterpriseUi> enterprisesGrid = new Grid<>(EnterpriseUi.class, false);

    @Autowired
    public MainView(SecurityService securityService, DatabaseController databaseController) {
        this.databaseController = databaseController;
        this.securityService = securityService;

        setSpacing(true);
        add(createHeader(), new H2(ENTERPRISES_TITLE), createReportsButton(), enterprisesGrid);
        fillEnterprisesGrid();
    }

    private VerticalLayout createHeader() {
        H1 logo = new H1(APP_NAME);
        H2 userNameHeader = new H2(HELLO_GREETING + securityService.getAuthenticatedUser().getUsername());

        Button logout = new Button(LOGOUT_BUTTON, e -> securityService.logout());

        HorizontalLayout header = new HorizontalLayout(logo, logout);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(logo);
        header.setWidth(HEADER_WIDTH);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(header, userNameHeader);
        return verticalLayout;
    }

    private void fillEnterprisesGrid() {
        enterprisesGrid.setItems(getEnterprisesUi());
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getName).setHeader(new H4(NAME));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getCity).setHeader(new H4(CITY));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getVehicleNumbers).setHeader(createVehiclesHeader());
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getDriverNames).setHeader(createDriversHeader());
    }

    private Component createVehiclesHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4(VEHICLES_TITLE));

        Button addVehicleButton = new Button(new Icon(VaadinIcon.PLUS));
        addVehicleButton.addClickListener(event -> new CRUDVehicleDialogBuilder(databaseController.getAllBrands()).createDialogForNewVehicle(getEnterprisesUi(), databaseController::saveVehicle));

        Button showAllVehiclesButton = new Button(SHOW_ALL_BUTTON);
        showAllVehiclesButton.addClickListener(event -> new SelectEnterpriseDialogBuilder().createDialog(getEnterprisesUi(), enterpriseUi -> new ShowVehiclesDialogBuilder(
                databaseController.getAllBrands())
                .createDialogForShowingVehicles(enterpriseUi,
                        databaseController.getAllVehicles()
                                .stream()
                                .filter(vehicleEntity -> vehicleEntity.getEnterpriseId().equals(enterpriseUi.getId()))
                                .collect(Collectors.toList()),
                        databaseController::saveVehicle,
                        databaseController::deleteVehicle,
                        databaseController::getAllTripsDtoByVehicleIdAndDates,
                        databaseController::getAllGeopointsByVehicleIdAndDates)));

        horizontalLayout.add(addVehicleButton, showAllVehiclesButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        return horizontalLayout;
    }

    private Component createDriversHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4(DRIVERS_TITLE));

        Button showAllDriversButton = new Button(SHOW_ALL_BUTTON);
        showAllDriversButton.addClickListener(event -> new SelectEnterpriseDialogBuilder().createDialog(getEnterprisesUi(), enterpriseUi -> new ShowAllDriversDialogBuilder().createDialogForShowingDrivers(databaseController.getDriversByEnterpriseId(enterpriseUi.getId()))));

        horizontalLayout.add(showAllDriversButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        return horizontalLayout;
    }

    private Button createReportsButton() {
        Button reportsButton = new Button(REPORTS_BUTTON);
        reportsButton.addClickListener(event -> new ReportsDialogBuilder(databaseController::getVehicleIdByStateNumber, databaseController::getAllTripsByVehicleIdAndDates).createDialogForShowingReports());
        return reportsButton;
    }

    private List<EnterpriseUi> getEnterprisesUi() {
        List<EnterpriseEntity> enterpriseEntities = databaseController.getEnterprisesForCurrentManager();
        List<VehicleEntity> vehicleEntities = databaseController.getAllVehicles();
        List<DriverEntity> driverEntities = databaseController.getAllDrivers();
        List<EnterpriseUi> enterpriseUis = new ArrayList<>();
        enterpriseEntities.forEach(enterpriseEntity -> {
            String name = enterpriseEntity.getName();
            String city = enterpriseEntity.getCity();
            String vehicleNumbers = vehicleEntities.stream()
                    .filter(vehicleEntity -> vehicleEntity.getEnterpriseId().equals(enterpriseEntity.getId()))
                    .map(vehicleEntity -> vehicleEntity.getStateNumber().toString())
                    .collect(Collectors.joining(", "));
            String driverNames = driverEntities.stream()
                    .filter(driverEntity -> driverEntity.getEnterpriseId().equals(enterpriseEntity.getId()))
                    .map(DriverEntity::getName).collect(Collectors.joining(", "));
            enterpriseUis.add(new EnterpriseUi(enterpriseEntity.getId(), name, city, vehicleNumbers, driverNames));
        });
        return enterpriseUis;
    }
}

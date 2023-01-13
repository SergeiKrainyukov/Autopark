package com.example.demo3.view;

import com.example.demo3.repository.BrandsRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.security.SecurityService;
import com.example.demo3.view.dialogs.*;
import com.example.demo3.view.helpers.EnterprisesUIProvider;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
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

import static com.example.demo3.common.Strings.*;

@Route("")
@RolesAllowed(ROLE_MANAGER)
public class MainView extends VerticalLayout {

    private final static String HELLO_GREETING = "Hello, ";
    private final static String HEADER_WIDTH = "100%";

    private final SecurityService securityService;

    private final VehiclesRepository vehiclesRepository;
    private final BrandsRepository brandsRepository;
    private final EnterprisesUIProvider enterprisesUIProvider;

    private final Grid<EnterpriseUi> enterprisesGrid = new Grid<>(EnterpriseUi.class, false);

    private final ShowVehiclesDialogBuilder showVehiclesDialogBuilder;
    private final ShowAllDriversDialogBuilder showAllDriversDialogBuilder;
    private final ReportsDialogBuilder reportsDialogBuilder;

    @Autowired
    public MainView(
            VehiclesRepository vehiclesRepository,
            ShowVehiclesDialogBuilder showVehiclesDialogBuilder,
            ShowAllDriversDialogBuilder showAllDriversDialogBuilder,
            SecurityService securityService,
            BrandsRepository brandsRepository,
            EnterprisesUIProvider enterprisesUIProvider, ReportsDialogBuilder reportsDialogBuilder) {
        this.vehiclesRepository = vehiclesRepository;
        this.showVehiclesDialogBuilder = showVehiclesDialogBuilder;
        this.showAllDriversDialogBuilder = showAllDriversDialogBuilder;
        this.securityService = securityService;
        this.brandsRepository = brandsRepository;
        this.enterprisesUIProvider = enterprisesUIProvider;
        this.reportsDialogBuilder = reportsDialogBuilder;

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
        verticalLayout.add(header);
        verticalLayout.add(userNameHeader);

        return verticalLayout;
    }

    private void fillEnterprisesGrid() {
        enterprisesGrid.setItems(enterprisesUIProvider.getEnterprisesUIForCurrentManager());
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getName).setHeader(new H4(NAME));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getCity).setHeader(new H4(CITY));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getVehicles).setHeader(createVehiclesHeader());
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getDrivers).setHeader(createDriversHeader());
    }

    private Component createVehiclesHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4(VEHICLES_TITLE));

        Button addVehicleButton = new Button(new Icon(VaadinIcon.PLUS));
        addVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> new CRUDVehicleDialogBuilder(vehiclesRepository, brandsRepository).createDialogForNewVehicle(enterprisesUIProvider.getEnterprisesUIForCurrentManager()));

        Button showAllVehiclesButton = new Button(SHOW_ALL_BUTTON);
        showAllVehiclesButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> new SelectEnterpriseDialogBuilder().createDialog(enterprisesUIProvider.getEnterprisesUIForCurrentManager(), showVehiclesDialogBuilder::createDialogForShowingVehicles));

        horizontalLayout.add(addVehicleButton, showAllVehiclesButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        return horizontalLayout;
    }

    private Component createDriversHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4(DRIVERS_TITLE));

        Button showAllDriversButton = new Button(SHOW_ALL_BUTTON);
        showAllDriversButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> new SelectEnterpriseDialogBuilder().createDialog(enterprisesUIProvider.getEnterprisesUIForCurrentManager(), enterpriseUi -> showAllDriversDialogBuilder.createDialogForShowingDrivers(enterpriseUi.getId())));

        horizontalLayout.add(showAllDriversButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        return horizontalLayout;
    }

    private Button createReportsButton() {
        Button reportsButton = new Button(REPORTS_BUTTON);
        reportsButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> reportsDialogBuilder.createDialogForShowingReports());
        return reportsButton;
    }
}

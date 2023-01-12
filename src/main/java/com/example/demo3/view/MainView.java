package com.example.demo3.view;

import com.example.demo3.repository.BrandsRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.security.SecurityService;
import com.example.demo3.view.dialogs.CRUDVehicleDialogBuilder;
import com.example.demo3.view.dialogs.ReportsDialogBuilder;
import com.example.demo3.view.dialogs.ShowAllDriversDialogBuilder;
import com.example.demo3.view.dialogs.ShowVehiclesDialogBuilder;
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

    private final static String VEHICLES_KEY = "Vehicles";
    private final static String DRIVERS_KEY = "Drivers";
    private final static String NAME_KEY = "Name";
    private final static String CITY_KEY = "City";
    private final static String HELLO_GREETING = "Hello, ";
    private final static String HEADER_WIDTH = "100%";

    private final SecurityService securityService;

    private final VehiclesRepository vehiclesRepository;
    private final BrandsRepository brandsRepository;
    private final EnterprisesUIProvider enterprisesUIProvider;

    private final Grid<EnterpriseUi> enterprisesGrid = new Grid<>(EnterpriseUi.class, false);

    private final ShowVehiclesDialogBuilder showVehiclesDialogBuilder;
    private final ShowAllDriversDialogBuilder showAllDriversDialogBuilder;

    private Button reportsButton = new Button("Reports");
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

        createHeader();

        setSpacing(true);

        add(new H2(ENTERPRISES_TITLE));
        add(enterprisesGrid);

        add(reportsButton);
        reportsButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> reportsDialogBuilder.createDialogForShowingReports());

        fillEnterprisesGrid();
    }

    private void createHeader() {
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

        add(verticalLayout);
    }

    private void fillEnterprisesGrid() {
        enterprisesGrid.setItems(enterprisesUIProvider.getEnterprisesUIForCurrentManager());

        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getName).setHeader(new H4(NAME_KEY));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getCity).setHeader(new H4(CITY_KEY));
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getVehicles).setHeader(createVehiclesHeader());
        enterprisesGrid.addColumn((ValueProvider<EnterpriseUi, String>) EnterpriseUi::getDrivers).setHeader(new H4(DRIVERS_KEY));

        enterprisesGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<EnterpriseUi>>) enterpriseEntityItemDoubleClickEvent -> {
            Class componentType = enterpriseEntityItemDoubleClickEvent.getColumn().getHeaderComponent().getClass();
            if (componentType.equals(HorizontalLayout.class)) {
                showVehiclesDialogBuilder.createDialogForShowingVehicles(enterpriseEntityItemDoubleClickEvent.getItem());
            }
            if (componentType.equals(H4.class)) {
                showAllDriversDialogBuilder.createDialogForShowingDrivers(enterpriseEntityItemDoubleClickEvent.getItem().getId());
            }
        });
    }

    private Component createVehiclesHeader() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4(VEHICLES_KEY));
        Button addVehicleButton = new Button(new Icon(VaadinIcon.PLUS));
        addVehicleButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> new CRUDVehicleDialogBuilder(vehiclesRepository, brandsRepository).createDialogForNewVehicle(enterprisesUIProvider.getEnterprisesUIForCurrentManager()));
        horizontalLayout.add(addVehicleButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        return horizontalLayout;
    }
}

package com.example.demo3.view.dialogs;

import com.example.demo3.model.entity.DriverEntity;
import com.example.demo3.repository.DriversRepository;
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
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.demo3.common.Strings.DRIVERS_TITLE;
import static com.example.demo3.common.Strings.OK_BUTTON;

@SpringComponent
@UIScope
public class ShowAllDriversDialogBuilder {
    private static final String NAME = "Name: ";
    private static final String SALARY = "Salary: ";

    //TODO: remove it
    private final DriversRepository driversRepository;

    private final ComponentRenderer<Component, DriverEntity> driverEntityComponentRenderer = new ComponentRenderer<>(
            driver -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.getElement().appendChild(
                        ElementFactory.createStrong(NAME + driver.getName()));
                infoLayout.add(new Div(new Text(SALARY + driver.getSalary().toString())));
                cardLayout.add(infoLayout);
                return cardLayout;
            });

    @Autowired
    public ShowAllDriversDialogBuilder(DriversRepository driversRepository) {
        this.driversRepository = driversRepository;
    }

    public void createDialogForShowingDrivers(Long enterpriseId) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(DRIVERS_TITLE);

        VerticalLayout dialogLayout = createDialogLayout(enterpriseId);
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(Long enterpriseId) {

        VirtualList<DriverEntity> list = new VirtualList<>();
        list.setItems(driversRepository.getAllByEnterpriseId(enterpriseId));
        list.setRenderer(driverEntityComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}

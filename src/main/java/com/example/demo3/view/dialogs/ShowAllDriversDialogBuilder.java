package com.example.demo3.view.dialogs;

import com.example.demo3.model.entity.DriverEntity;
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

import java.util.List;

import static com.example.demo3.common.Strings.DRIVERS_TITLE;
import static com.example.demo3.common.Strings.OK_BUTTON;

public class ShowAllDriversDialogBuilder {
    private static final String NAME = "Name: ";
    private static final String SALARY = "Salary: ";

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

    public void createDialogForShowingDrivers(List<DriverEntity> driverEntityList) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(DRIVERS_TITLE);

        VirtualList<DriverEntity> list = new VirtualList<>();
        list.setItems(driverEntityList);
        list.setRenderer(driverEntityComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }
}

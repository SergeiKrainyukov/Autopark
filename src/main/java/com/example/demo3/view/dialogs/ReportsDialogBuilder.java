package com.example.demo3.view.dialogs;

import com.example.demo3.model.report.ReportType;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import static com.example.demo3.common.Strings.OK_BUTTON;

@SpringComponent
@UIScope
public class ReportsDialogBuilder {
    private final ComponentRenderer<Component, ReportType> driverEntityComponentRenderer = new ComponentRenderer<>(
            reportType -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
                infoLayout.setSpacing(false);
                infoLayout.setPadding(false);
                infoLayout.add(new Text("Report type: " + reportType));
                cardLayout.add(infoLayout);
                cardLayout.addClickListener((ComponentEventListener<ClickEvent<HorizontalLayout>>) horizontalLayoutClickEvent -> {
                    switch (reportType) {
                        case MILEAGE_BY_PERIOD: {
                            new MileageByPeriodReportDialogBuilder().createDialogForShowingReports(123L);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                });
                return cardLayout;
            });

    public void createDialogForShowingReports() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reports");

        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> dialog.close());

        dialog.getFooter().add(okButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout() {
        VirtualList<ReportType> list = new VirtualList<>();
        list.setItems(ReportType.values());
        list.setRenderer(driverEntityComponentRenderer);

        VerticalLayout dialogLayout = new VerticalLayout(list);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }
}

package com.example.demo3.view.dialogs;

import com.example.demo3.view.EnterpriseUi;
import com.example.demo3.view.dialogs.helpers.EnterpriseUIAction;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.demo3.common.Strings.*;

public class SelectEnterpriseDialogBuilder {

    AtomicReference<EnterpriseUi> selectedEnterprise = new AtomicReference<>();

    public void createDialog(List<EnterpriseUi> enterpriseUiList, EnterpriseUIAction action) {

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(SELECT_ENTERPRISE);

        VerticalLayout dialogLayout = createDialogLayout(enterpriseUiList);
        dialog.add(dialogLayout);

        Button showButton = new Button(SHOW_ALL_BUTTON, e -> action.call(selectedEnterprise.get()));
        Button cancelButton = new Button(CANCEL_BUTTON, e -> dialog.close());

        dialog.getFooter().add(showButton, cancelButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout(List<EnterpriseUi> enterpriseUiList) {
        ComboBox<EnterpriseUi> enterpriseUiComboBox = new ComboBox<>(ENTERPRISE_LABEL);
        enterpriseUiComboBox.setItems(enterpriseUiList);
        enterpriseUiComboBox.setItemLabelGenerator(EnterpriseUi::getName);
        enterpriseUiComboBox.setValue(enterpriseUiList.get(0));
        selectedEnterprise.set(enterpriseUiList.get(0));

        enterpriseUiComboBox.addValueChangeListener(event -> selectedEnterprise.set(event.getValue()));
        return new VerticalLayout(enterpriseUiComboBox);
    }
}

package com.example.demo3.view.dialogs;

import com.example.demo3.model.entity.BrandEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.BrandsRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.view.EnterpriseUi;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.*;

import static com.example.demo3.common.Strings.*;

public class CRUDVehicleDialogBuilder {

    private final VehiclesRepository vehiclesRepository;
    private final BrandsRepository brandsRepository;

    private final List<BrandEntity> brandEntities = new ArrayList<>();

    private VehicleEntity vehicleEntity;

    private static final String UTC_TIMEZONE = "UTC";

    public CRUDVehicleDialogBuilder(VehiclesRepository vehiclesRepository, BrandsRepository brandsRepository) {
        this.vehiclesRepository = vehiclesRepository;
        this.brandsRepository = brandsRepository;

        fetchDataFromRepo();
    }

    public void createDialogForNewVehicle(List<EnterpriseUi> enterpriseUiList) {
        vehicleEntity = new VehicleEntity();
        vehicleEntity.setPurchaseDate(Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE)).getTimeInMillis());

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(NEW_VEHICLE);

        VerticalLayout dialogLayout = createDialogLayout(enterpriseUiList);
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button(CANCEL_BUTTON, e -> dialog.close());

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }

    public void createDialogForUpdateVehicle(VehicleEntity vehicleEntity) {
        this.vehicleEntity = vehicleEntity;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(UPDATE_VEHICLE);

        VerticalLayout dialogLayout = createDialogLayout(null);
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = createCancelButton(dialog);

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        dialog.open();
    }

    private VerticalLayout createDialogLayout(List<EnterpriseUi> enterpriseUiList) {

        TextField priceField = new TextField(PRICE);
        TextField yearField = new TextField(YEAR);
        TextField mileageField = new TextField(MILEAGE);

        ComboBox<BrandEntity> brandEntityComboBox = new ComboBox<>(BRAND_LABEL);
        brandEntityComboBox.setItems(brandEntities);
        brandEntityComboBox.setItemLabelGenerator(BrandEntity::getName);

        ComboBox<EnterpriseUi> enterpriseUiComboBox = null;

        if (enterpriseUiList != null) {
            enterpriseUiComboBox = new ComboBox<>(ENTERPRISE_LABEL);
            enterpriseUiComboBox.setItems(enterpriseUiList);
            enterpriseUiComboBox.setItemLabelGenerator(EnterpriseUi::getName);
            enterpriseUiComboBox.setValue(enterpriseUiList.get(0));
            vehicleEntity.setEnterpriseId(enterpriseUiList.get(0).getId());

            enterpriseUiComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<EnterpriseUi>, EnterpriseUi>>) event -> vehicleEntity.setEnterpriseId(event.getValue().getId()));
        }

        priceField.setValue(vehicleEntity.getPrice() == null ? "" : vehicleEntity.getPrice().toString());
        yearField.setValue(vehicleEntity.getYear() == null ? "" : vehicleEntity.getYear().toString());
        mileageField.setValue(vehicleEntity.getMileage() == null ? "" : vehicleEntity.getMileage().toString());
        brandEntityComboBox.setValue(vehicleEntity.getBrandId() == null ? brandEntities.get(0) : findBrandById(vehicleEntity.getBrandId()));
        if (vehicleEntity.getBrandId() == null) vehicleEntity.setBrandId(brandEntities.get(0).getId());

        priceField.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>) event -> vehicleEntity.setPrice(Integer.valueOf(event.getValue())));
        yearField.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>) event -> vehicleEntity.setYear(Integer.valueOf(event.getValue())));
        mileageField.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<TextField, String>>) event -> vehicleEntity.setMileage(Integer.valueOf(event.getValue())));
        brandEntityComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<BrandEntity>, BrandEntity>>) event -> vehicleEntity.setBrandId(event.getValue().getId()));

        VerticalLayout dialogLayout;

        if (enterpriseUiComboBox != null) {
            dialogLayout = new VerticalLayout(enterpriseUiComboBox, priceField, yearField,
                    mileageField, brandEntityComboBox);
        } else {
            dialogLayout = new VerticalLayout(priceField, yearField,
                    mileageField, brandEntityComboBox);
        }

        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button(SAVE_BUTTON, e -> {
            vehiclesRepository.save(vehicleEntity);
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    private Button createCancelButton(Dialog dialog) {
        return new Button(CANCEL_BUTTON, e -> dialog.close());
    }

    private void fetchDataFromRepo() {
        for (BrandEntity brandEntity : brandsRepository.findAll()) {
            brandEntities.add(brandEntity);
        }
    }

    private BrandEntity findBrandById(Long brandId) {
        for (BrandEntity brandEntity : brandEntities) {
            if (Objects.equals(brandEntity.getId(), brandId)) return brandEntity;
        }
        return null;
    }
}

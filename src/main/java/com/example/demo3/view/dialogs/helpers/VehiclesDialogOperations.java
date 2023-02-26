package com.example.demo3.view.dialogs.helpers;

public class VehiclesDialogOperations {
    private final SaveVehicle saveVehicle;
    private final DeleteVehicle deleteVehicle;
    private final GetTripsDtoHelper getTripsDtoHelper;
    private final GetGeoPointsHelper getGeoPointsHelper;

    public VehiclesDialogOperations(SaveVehicle saveVehicle, DeleteVehicle deleteVehicle, GetTripsDtoHelper getTripsDtoHelper, GetGeoPointsHelper getGeoPointsHelper) {
        this.saveVehicle = saveVehicle;
        this.deleteVehicle = deleteVehicle;
        this.getTripsDtoHelper = getTripsDtoHelper;
        this.getGeoPointsHelper = getGeoPointsHelper;
    }

    public SaveVehicle getSaveVehicle() {
        return saveVehicle;
    }

    public DeleteVehicle getDeleteVehicle() {
        return deleteVehicle;
    }

    public GetTripsDtoHelper getGetTripsDtoHelper() {
        return getTripsDtoHelper;
    }

    public GetGeoPointsHelper getGetGeoPointsHelper() {
        return getGeoPointsHelper;
    }
}

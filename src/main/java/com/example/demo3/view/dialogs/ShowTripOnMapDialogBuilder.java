package com.example.demo3.view.dialogs;

import com.example.demo3.model.dto.TripDto;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.repository.GeoPointRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.demo3.common.Strings.OK_BUTTON;

public class ShowTripOnMapDialogBuilder {

    private static final String imageUrl = "https://static-maps.yandex.ru/1.x/?lang=en_US&l=map&pl=";
    private static final String destinationFile = "image.png";
    private static final String ALT_TEXT = "alt text";

    private static final String dateFormatPattern = "dd.MM.yyyy HH:mm:ss";

    public void createDialog(TripDto tripDto, GeoPointRepository geoPointRepository) {
        Dialog dialog = new Dialog();

        VerticalLayout dialogLayout = createDialogLayout(tripDto, geoPointRepository);
        dialog.add(dialogLayout);

        Button okButton = new Button(OK_BUTTON, event -> {
            File file = new File(destinationFile);
            if (!file.exists()) file.delete();
            dialog.close();
        });
        dialog.getFooter().add(okButton);

        dialog.open();
    }

    private VerticalLayout createDialogLayout(TripDto tripDto, GeoPointRepository geoPointRepository) {
        try {
            saveImage(tripDto, geoPointRepository);
            File file = new File(destinationFile);
            if (!file.exists()) return null;
            Image image = new Image(new StreamResource(file.getPath(), (InputStreamFactory) () -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }), ALT_TEXT);

            VerticalLayout dialogLayout = new VerticalLayout(image);
            dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
            dialogLayout.getStyle().set("width", "25rem").set("max-width", "100%");

            return dialogLayout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImage(TripDto tripDto, GeoPointRepository geoPointRepository) throws Exception {
        List<GeoPointEntity> geoPointEntityList = geoPointRepository.findAllBetweenDates(getLongDate(tripDto.getStartDate()), getLongDate(tripDto.getEndDate()));
        if (geoPointEntityList.size() == 0) return;

        StringBuilder query = new StringBuilder();
        for (int i = 0; i < geoPointEntityList.size(); i++) {
            GeoPointEntity geoPointEntity = geoPointEntityList.get(i);
            query.append(geoPointEntity.getGeoPoint().getX()).append(',').append(geoPointEntity.getGeoPoint().getY());
            if (i != geoPointEntityList.size() - 1) query.append(',');
        }

        URL url = new URL(imageUrl + query);
        InputStream is = url.openStream();
        File file = new File(destinationFile);
        OutputStream os = new FileOutputStream(file);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

    private Long getLongDate(String date) throws Exception {
        Date parsedDate = new SimpleDateFormat(dateFormatPattern).parse(date);
        return parsedDate.getTime();
    }
}

package com.example.demo3.bot;

import com.example.demo3.controller.DatabaseController;
import com.example.demo3.model.entity.ManagerEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.model.report.MileageByPeriodReport;
import com.example.demo3.model.report.ReportPeriod;
import com.example.demo3.model.report.ReportResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfig config;

    private ManagerEntity manager = new ManagerEntity();
    private boolean isAuthorized = false;

    private final DatabaseController databaseController;

    public Bot(BotConfig config, DatabaseController databaseController) {
        this.config = config;
        this.databaseController = databaseController;
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null || !message.hasText()) return;
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;
        try {
            if (update.getMessage() != null) {
                chatId = update.getMessage().getChatId().toString();
                builder.chatId(chatId);
                messageText = update.getMessage().getText();
            } else {
                chatId = update.getChannelPost().getChatId().toString();
                builder.chatId(chatId);
                messageText = update.getChannelPost().getText();
            }
            if (isAuthorized) {
                authorizedLogic(messageText, builder);
                return;
            }
            unAuthorizedLogic(messageText, builder);
        } catch (Exception e) {
            log.debug(e.toString());
        }
    }

    private void authorizedLogic(String messageText, SendMessage.SendMessageBuilder builder) throws Exception {
        if (messageText.contains(":")) {
            proceedMileageReportLogic(messageText, builder);
            return;
        }
        proceedCommands(messageText, builder);
    }

    private void unAuthorizedLogic(String messageText, SendMessage.SendMessageBuilder builder) throws Exception {
        switch (messageText) {
            case "/login": {
                if (manager.getUsername().isBlank()) {
                    builder.text("Write login:");
                    execute(builder.build());
                    return;
                } else if (manager.getPassword().isBlank()) {
                    builder.text("Write password:");
                    execute(builder.build());
                    return;
                }
            }
            default: {
                if (manager.getUsername().isBlank()) {
                    manager = new ManagerEntity();
                    manager.setUsername(messageText);
                    builder.text("Write password:");
                    execute(builder.build());
                } else if (manager.getPassword().isBlank()) {
                    manager.setPassword(messageText);
                    if (validateCredentials()) {
                        isAuthorized = true;
                        builder.text("You are successfully authorized");
                        execute(builder.build());
                    } else {
                        isAuthorized = false;
                        manager = new ManagerEntity();
                        builder.text("Authorization failed. Write login:");
                        execute(builder.build());
                    }
                }
            }
        }
    }

    private void proceedCommands(String messageText, SendMessage.SendMessageBuilder builder) throws Exception {
        switch (messageText) {
            case "/login": {
                builder.text("You have been authorized earlier as " + manager.getUsername());
                execute(builder.build());
            }
            case "/vehicle_mileage": {
                builder.text("Enter the period, state number and date in format: [period:state_number:date]." +
                        "\nFor day: DAY:123456:02.02.2022." +
                        "\nFor month: MONTH:123456:02.02.2022-02.03.2022.");
                execute(builder.build());
            }
        }
    }

    private void proceedMileageReportLogic(String messageText, SendMessage.SendMessageBuilder builder) throws Exception {
        String[] splittedMessage = messageText.split(":");
        if (splittedMessage.length != 3) {
            builder.text("Wrong data format. Try again.");
            execute(builder.build());
            return;
        }
        ReportPeriod reportPeriod = calculateReportPeriodFromString(splittedMessage[0]);
        if (reportPeriod == null) {
            builder.text("Wrong period format. Try again.");
            execute(builder.build());
            return;
        }
        int stateNumber = Integer.parseInt(splittedMessage[1]);
        switch (reportPeriod) {
            case DAY: {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date dateTo = sdf.parse(splittedMessage[2]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dateTo);
                    calendar.add(Calendar.DATE, -1);
                    Date dateFrom = calendar.getTime();
                    VehicleEntity vehicleEntity = databaseController.getVehicleByStateNumber(stateNumber);
                    if (vehicleEntity == null) {
                        builder.text("Vehicle not found. Try again.");
                        execute(builder.build());
                        return;
                    }
                    List<TripEntity> tripsByVehicleIdAndDates = databaseController.getAllTripsByVehicleIdAndDates(vehicleEntity.getId(), dateFrom.getTime(), dateTo.getTime());
                    List<ReportResult> reportResultList = new MileageByPeriodReport(ReportPeriod.DAY, dateFrom.getTime(), dateTo.getTime(), tripsByVehicleIdAndDates).getResult();
                    int mileageByDay = 0;
                    for (ReportResult reportResult : reportResultList) {
                        mileageByDay += Integer.parseInt(reportResult.getValue());
                    }
                    builder.text("Mileage for " + splittedMessage[2] + ": " + mileageByDay + " km");
                    execute(builder.build());
                } catch (ParseException e) {
                    builder.text("Wrong date format. Try again.");
                    execute(builder.build());
                    return;
                }
            }
            case MONTH: {
                String[] dates = splittedMessage[2].split("-");
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date dateFrom = sdf.parse(dates[0]);
                    Date dateTo = sdf.parse(dates[1]);
                    VehicleEntity vehicleEntity = databaseController.getVehicleByStateNumber(stateNumber);
                    if (vehicleEntity == null) {
                        builder.text("Vehicle not found. Try again.");
                        execute(builder.build());
                        return;
                    }
                    List<TripEntity> tripsByVehicleIdAndDates = databaseController.getAllTripsByVehicleIdAndDates(vehicleEntity.getId(), dateFrom.getTime(), dateTo.getTime());
                    List<ReportResult> reportResultList = new MileageByPeriodReport(ReportPeriod.MONTH, dateFrom.getTime(), dateTo.getTime(), tripsByVehicleIdAndDates).getResult();
                    double mileageByPeriod = 0;
                    for (ReportResult reportResult : reportResultList) {
                        mileageByPeriod += Double.parseDouble(reportResult.getValue());
                    }
                    builder.text("Mileage for " + dates[0] + "-" + dates[1] + ": " + mileageByPeriod + " km");
                    execute(builder.build());
                } catch (ParseException e) {
                    builder.text("Wrong date format. Try again.");
                    execute(builder.build());
                }
            }
        }
    }

    private boolean validateCredentials() {
        Iterable<ManagerEntity> managerEntities = databaseController.getManagers();
        for (ManagerEntity managerEntity : managerEntities) {
            if (manager.getUsername().equals(managerEntity.getUsername()) && manager.getPassword().equals(managerEntity.getPassword())) {
                manager = managerEntity;
                return true;
            }
        }
        return false;
    }


    public String getBotUsername() {
        return config.getBotUserName();
    }

    public String getBotToken() {
        return config.getToken();
    }

    @Nullable
    private ReportPeriod calculateReportPeriodFromString(String period) {
        switch (period) {
            case "DAY":
                return ReportPeriod.DAY;
            case "MONTH":
                return ReportPeriod.MONTH;
            default:
                return null;
        }
    }
}

package com.springboot.notificationsApp.dto;
import com.springboot.notificationsApp.validator.ValidDateRange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Getter
@ValidDateRange
public class RangeDTO extends PageDTO{
    @Pattern(message = "Please Use YYYY-MM-DD'T'HH:mm:ss format",regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
    String startDate;

    @Pattern(message = "Please Use YYYY-MM-DD'T'HH:mm:ss format",regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
    String endDate;

    public Date getStartDateTime() throws DateTimeParseException {
        LocalDateTime startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getEndDateTime() throws DateTimeParseException {
        LocalDateTime endDateTime =  LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}

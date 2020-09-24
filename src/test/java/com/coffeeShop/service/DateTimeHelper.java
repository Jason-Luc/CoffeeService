package com.coffeeShop.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

public class DateTimeHelper {
    public static Long getEpochSecond(String timestamp) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            return ZonedDateTime.of(LocalDateTime.parse(timestamp, formatter), ZoneOffset.UTC).toEpochSecond();
        } catch(DateTimeParseException ex) {
            if (!"".equals(timestamp)) {
                System.out.println(String.join("\n", Stream.of(ex.getStackTrace())
                        .map(trace -> trace.toString())
                        .collect(toList())));
                
                throw new Error(ex.toString());
            }
        }
        
        return 0L;
    }
    
    public static String getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

package com.main.model;

import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sensor {
    private String type;
    private List<Reading> readings = new ArrayList<>();
    private LocalDateTime timestamp; // Add a field to store the timestamp for the sensor

    public Sensor() {}

    public Sensor(String type) {
        this.type = type;
        this.timestamp = LocalDateTime.now(); // Initialize timestamp when the sensor is created
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void addReading(String value) {
        // Add a reading with the current timestamp in a specific time zone
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        this.readings.add(new Reading(value, zonedDateTime.toLocalDateTime()));
    }



    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp; // Set the timestamp for the sensor
    }

    // Method to set the timestamp for a specific reading by index
    public void setReadingTimestamp(LocalDateTime timestamp, int readingIndex) {
        if (readingIndex >= 0 && readingIndex < readings.size()) {
            readings.get(readingIndex).setTimestamp(timestamp);
        } else {
            throw new IndexOutOfBoundsException("Invalid reading index");
        }
    }

    // Method to get the most recent reading
    public Reading getMostRecentReading() {
        return readings.stream()
                       .max((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()))
                       .orElse(null);
    }
}


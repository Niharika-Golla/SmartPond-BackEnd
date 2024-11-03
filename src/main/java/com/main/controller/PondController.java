package com.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.main.model.Pond;
import com.main.model.Sensor;
import com.main.model.Reading;
import com.main.repository.PondRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/ponds")
@CrossOrigin(origins = "http://localhost:4200")
public class PondController {

    @Autowired
    private PondRepository pondRepository;

    @GetMapping
    public List<Pond> getAllPonds() {
        List<Pond> ponds = pondRepository.findAll();
        // Convert timestamps to local time zone
        ponds.forEach(pond -> {
            if (pond.getCreatedAt() != null) {
                pond.setCreatedAt(convertToLocalTime(pond.getCreatedAt()));
            }
        });
        return ponds;
    }

    @PostMapping("/add")
    public Pond addPond(@RequestBody Pond pond) {
        pond.setCreatedAt(LocalDateTime.now());  // Set the created time when adding a new pond
        Pond savedPond = pondRepository.save(pond);
        savedPond.setCreatedAt(convertToLocalTime(savedPond.getCreatedAt())); // Convert to local time for response
        return savedPond;
    }

    @GetMapping("/{pondId}/sensors")
    public List<Sensor> getSensorsByPond(@PathVariable String pondId) {
        Pond pond = pondRepository.findById(pondId)
                .orElseThrow(() -> new RuntimeException("Pond not found with ID: " + pondId));
        return pond.getSensors();
    }

    @PostMapping("/{pondId}/sensors")
    public Pond addOrUpdateSensorToPond(@PathVariable String pondId, @RequestBody Sensor sensor) {
        Pond pond = pondRepository.findById(pondId)
                .orElseThrow(() -> new RuntimeException("Pond not found with ID: " + pondId));

        pond.getSensors().removeIf(existingSensor -> existingSensor.getType().equals(sensor.getType()));
        sensor.setTimestamp(LocalDateTime.now());
        pond.addSensor(sensor);
        Pond updatedPond = pondRepository.save(pond);
        updatedPond.setCreatedAt(convertToLocalTime(updatedPond.getCreatedAt())); // Convert to local time for response
        return updatedPond;
    }

    @PostMapping("/{pondId}/sensors/{sensorType}/readings")
    public ResponseEntity<Pond> addReadingToSensor(@PathVariable String pondId, @PathVariable String sensorType, @RequestBody String readingValue) {
        Pond pond = pondRepository.findById(pondId)
                .orElseThrow(() -> new RuntimeException("Pond not found with ID: " + pondId));

        Sensor sensor = pond.getSensors().stream()
                .filter(s -> s.getType().equals(sensorType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sensor not found with type: " + sensorType));

        sensor.addReading(readingValue);
        Pond updatedPond = pondRepository.save(pond);
        return ResponseEntity.ok(updatedPond);
    }

    @GetMapping("/{pondId}/sensors/{sensorType}/most-recent")
    public ResponseEntity<Reading> getMostRecentReading(@PathVariable String pondId, @PathVariable String sensorType) {
        Pond pond = pondRepository.findById(pondId)
                .orElseThrow(() -> new RuntimeException("Pond not found with ID: " + pondId));

        Sensor sensor = pond.getSensors().stream()
                .filter(s -> s.getType().equals(sensorType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sensor not found with type: " + sensorType));

        Reading mostRecentReading = sensor.getMostRecentReading();
        if (mostRecentReading != null) {
            mostRecentReading.setTimestamp(convertToLocalTime(mostRecentReading.getTimestamp())); // Convert to local time
        }
        return ResponseEntity.ok(mostRecentReading);
    }

    @GetMapping("/{pondId}")
    public Optional<Pond> getPondById(@PathVariable String pondId) {
        Optional<Pond> pond = pondRepository.findById(pondId);
        pond.ifPresent(p -> p.setCreatedAt(convertToLocalTime(p.getCreatedAt()))); // Convert to local time if present
        return pond;
    }

    @DeleteMapping("/{pondId}")
    public ResponseEntity<?> deletePond(@PathVariable String pondId) {
        if (!pondRepository.existsById(pondId)) {
            return ResponseEntity.notFound().build();
        }
        pondRepository.deleteById(pondId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pondId}")
    public ResponseEntity<Pond> updatePond(@PathVariable String pondId, @RequestBody Pond updatedPond) {
        Pond existingPond = pondRepository.findById(pondId)
            .orElseThrow(() -> new RuntimeException("Pond not found with ID: " + pondId));

        existingPond.setName(updatedPond.getName());
        existingPond.setLocation(updatedPond.getLocation()); // Ensure this line is present

        // Save the updated pond with the new location
        Pond savedPond = pondRepository.save(existingPond);
        savedPond.setCreatedAt(convertToLocalTime(savedPond.getCreatedAt())); // Convert to local time for response
        return ResponseEntity.ok(savedPond);
    }

    private LocalDateTime convertToLocalTime(LocalDateTime utcDateTime) {
        return ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();
    }
}

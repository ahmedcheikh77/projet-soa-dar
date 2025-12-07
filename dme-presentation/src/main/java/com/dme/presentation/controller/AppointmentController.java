package com.dme.presentation.controller;

import com.dme.persistence.entity.Appointment;
import com.dme.persistence.repository.AppointmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@Slf4j
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Appointment not found"));
        }
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        log.info("Fetched {} appointments for patient: {}", appointments.size(), patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        log.info("Fetched {} appointments for doctor: {}", appointments.size(), doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> request) {
        try {
            Appointment appointment = Appointment.builder()
                    .patientId(Long.parseLong(request.get("patientId").toString()))
                    .doctorId(Long.parseLong(request.get("doctorId").toString()))
                    .appointmentDate(LocalDateTime.parse(request.get("appointmentDate").toString()))
                    .status(com.dme.persistence.entity.AppointmentStatus.SCHEDULED)
                    .notes(request.getOrDefault("notes", "").toString())
                    .build();

            Appointment savedAppointment = appointmentRepository.save(appointment);
            log.info("Appointment created: {}", savedAppointment.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error creating appointment: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Appointment not found"));
        }

        if (request.containsKey("status")) {
            appointment.setStatus(com.dme.persistence.entity.AppointmentStatus.valueOf(request.get("status").toString()));
        }
        if (request.containsKey("notes")) {
            appointment.setNotes(request.get("notes").toString());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated: {}", id);

        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Appointment not found"));
        }

        appointmentRepository.deleteById(id);
        log.info("Appointment deleted: {}", id);

        return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully"));
    }
}

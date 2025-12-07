package com.dme.presentation.controller;

import com.dme.persistence.entity.MedicalRecord;
import com.dme.persistence.repository.MedicalRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-records")
@Slf4j
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicalRecord(@PathVariable Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id).orElse(null);
        if (record == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Medical record not found"));
        }
        return ResponseEntity.ok(record);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientId(patientId);
        log.info("Fetched {} medical records for patient: {}", records.size(), patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getDoctorRecords(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorId(doctorId);
        log.info("Fetched {} medical records for doctor: {}", records.size(), doctorId);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createMedicalRecord(@RequestBody Map<String, Object> request) {
        try {
            MedicalRecord record = MedicalRecord.builder()
                    .patientId(Long.parseLong(request.get("patientId").toString()))
                    .doctorId(Long.parseLong(request.get("doctorId").toString()))
                    .diagnosis(request.getOrDefault("diagnosis", "").toString())
                    .treatment(request.getOrDefault("treatment", "").toString())
                    .prescription(request.getOrDefault("prescription", "").toString())
                    .hospitalId(request.containsKey("hospitalId") ? Long.parseLong(request.get("hospitalId").toString()) : null)
                    .encrypted(Boolean.parseBoolean(request.getOrDefault("encrypted", "false").toString()))
                    .build();

            MedicalRecord savedRecord = medicalRecordRepository.save(record);
            log.info("Medical record created: {}", savedRecord.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
        } catch (Exception e) {
            log.error("Error creating medical record", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error creating medical record: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        MedicalRecord record = medicalRecordRepository.findById(id).orElse(null);
        if (record == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Medical record not found"));
        }

        if (request.containsKey("diagnosis")) {
            record.setDiagnosis(request.get("diagnosis").toString());
        }
        if (request.containsKey("treatment")) {
            record.setTreatment(request.get("treatment").toString());
        }
        if (request.containsKey("prescription")) {
            record.setPrescription(request.get("prescription").toString());
        }

        MedicalRecord updatedRecord = medicalRecordRepository.save(record);
        log.info("Medical record updated: {}", id);

        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Medical record not found"));
        }

        medicalRecordRepository.deleteById(id);
        log.info("Medical record deleted: {}", id);

        return ResponseEntity.ok(Map.of("message", "Medical record deleted successfully"));
    }
}

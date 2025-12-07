package com.dme.soa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private Boolean encrypted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

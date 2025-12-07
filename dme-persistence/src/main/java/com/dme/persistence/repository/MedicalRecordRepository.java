package com.dme.persistence.repository;

import com.dme.persistence.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByHospitalId(Long hospitalId);
    List<MedicalRecord> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
}

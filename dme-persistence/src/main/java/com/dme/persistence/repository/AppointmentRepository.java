package com.dme.persistence.repository;

import com.dme.persistence.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);
}

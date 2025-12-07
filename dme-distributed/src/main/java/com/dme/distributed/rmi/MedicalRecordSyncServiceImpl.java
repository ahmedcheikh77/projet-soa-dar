package com.dme.distributed.rmi;

import com.dme.persistence.entity.MedicalRecord;
import com.dme.persistence.repository.MedicalRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Implementation of RMI Medical Record Synchronization Service
 */
@Service
@Slf4j
public class MedicalRecordSyncServiceImpl extends UnicastRemoteObject implements MedicalRecordSyncService {

    private static final long serialVersionUID = 1L;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordSyncServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void syncMedicalRecord(MedicalRecord record) throws RemoteException {
        try {
            log.info("Syncing medical record: {}", record.getId());
            medicalRecordRepository.save(record);
            log.info("Medical record synced successfully");
        } catch (Exception e) {
            log.error("Error syncing medical record", e);
            throw new RemoteException("Failed to sync medical record", e);
        }
    }

    @Override
    public MedicalRecord getRemoteMedicalRecord(Long recordId) throws RemoteException {
        try {
            log.info("Fetching remote medical record: {}", recordId);
            return medicalRecordRepository.findById(recordId).orElse(null);
        } catch (Exception e) {
            log.error("Error fetching medical record", e);
            throw new RemoteException("Failed to fetch medical record", e);
        }
    }

    @Override
    public List<MedicalRecord> getPatientRecordsFromNode(Long patientId) throws RemoteException {
        try {
            log.info("Fetching patient records from node: {}", patientId);
            return medicalRecordRepository.findByPatientId(patientId);
        } catch (Exception e) {
            log.error("Error fetching patient records", e);
            throw new RemoteException("Failed to fetch patient records", e);
        }
    }

    @Override
    public void replicateRecord(MedicalRecord record) throws RemoteException {
        try {
            log.info("Replicating medical record: {}", record.getId());
            medicalRecordRepository.save(record);
            log.info("Medical record replicated successfully");
        } catch (Exception e) {
            log.error("Error replicating medical record", e);
            throw new RemoteException("Failed to replicate medical record", e);
        }
    }

    @Override
    public boolean isNodeHealthy() throws RemoteException {
        try {
            log.info("Health check performed");
            return true;
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }
}

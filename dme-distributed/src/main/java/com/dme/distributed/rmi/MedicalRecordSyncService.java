package com.dme.distributed.rmi;

import com.dme.persistence.entity.MedicalRecord;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Interface for distributed medical record synchronization
 */
public interface MedicalRecordSyncService extends Remote {
    
    // Synchronize medical record between nodes
    void syncMedicalRecord(MedicalRecord record) throws RemoteException;
    
    // Retrieve medical record from remote node
    MedicalRecord getRemoteMedicalRecord(Long recordId) throws RemoteException;
    
    // Get all records for a patient across distributed nodes
    List<MedicalRecord> getPatientRecordsFromNode(Long patientId) throws RemoteException;
    
    // Replicate record to backup node
    void replicateRecord(MedicalRecord record) throws RemoteException;
    
    // Check node health
    boolean isNodeHealthy() throws RemoteException;
}

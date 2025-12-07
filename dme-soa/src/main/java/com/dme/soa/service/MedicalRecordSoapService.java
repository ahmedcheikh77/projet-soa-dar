package com.dme.soa.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://com.dme.soa/MedicalRecordService", name = "MedicalRecordService")
public interface MedicalRecordSoapService {

    @WebMethod(operationName = "getMedicalRecordById")
    String getMedicalRecordById(@WebParam(name = "recordId") Long recordId);

    @WebMethod(operationName = "getPatientMedicalRecords")
    String getPatientMedicalRecords(@WebParam(name = "patientId") Long patientId);

    @WebMethod(operationName = "createMedicalRecord")
    String createMedicalRecord(
            @WebParam(name = "patientId") Long patientId,
            @WebParam(name = "doctorId") Long doctorId,
            @WebParam(name = "diagnosis") String diagnosis);

    @WebMethod(operationName = "updateMedicalRecord")
    String updateMedicalRecord(
            @WebParam(name = "recordId") Long recordId,
            @WebParam(name = "treatment") String treatment);

    @WebMethod(operationName = "deleteMedicalRecord")
    String deleteMedicalRecord(@WebParam(name = "recordId") Long recordId);
}

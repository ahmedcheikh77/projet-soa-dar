-- Script d'initialisation de la base de données DME System

-- Créer les types si nécessaire
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('PATIENT', 'DOCTOR', 'ADMIN', 'HOSPITAL');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE appointment_status AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

-- Créer la table Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Créer la table Medical Records
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    encrypted BOOLEAN DEFAULT false,
    hospital_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Créer la table Appointments
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    status appointment_status DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Créer la table Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Créer les indices
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_medical_records_patient ON medical_records(patient_id);
CREATE INDEX IF NOT EXISTS idx_medical_records_doctor ON medical_records(doctor_id);
CREATE INDEX IF NOT EXISTS idx_medical_records_hospital ON medical_records(hospital_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);

-- Insérer des données de test
INSERT INTO users (username, email, password_hash, full_name, role, active)
VALUES 
    ('admin_user', 'admin@dme.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Administrator', 'ADMIN', true),
    ('doctor1', 'doctor1@hospital.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Dr. Jane Smith', 'DOCTOR', true),
    ('doctor2', 'doctor2@hospital.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Dr. John Doe', 'DOCTOR', true),
    ('patient1', 'patient1@email.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Alice Johnson', 'PATIENT', true),
    ('patient2', 'patient2@email.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Bob Wilson', 'PATIENT', true),
    ('hospital_admin', 'admin@hospital.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy', 'Hospital Administrator', 'HOSPITAL', true)
ON CONFLICT DO NOTHING;

-- Insérer des rendez-vous de test
INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, notes)
VALUES
    (4, 2, CURRENT_TIMESTAMP + INTERVAL '5 days 10:30:00', 'SCHEDULED', 'Consultation générale'),
    (5, 3, CURRENT_TIMESTAMP + INTERVAL '3 days 14:00:00', 'SCHEDULED', 'Suivi médical'),
    (4, 3, CURRENT_TIMESTAMP - INTERVAL '2 days', 'COMPLETED', 'Consultation effectuée')
ON CONFLICT DO NOTHING;

-- Insérer des dossiers médicaux de test
INSERT INTO medical_records (patient_id, doctor_id, diagnosis, treatment, prescription, hospital_id, encrypted)
VALUES
    (4, 2, 'Hypertension artérielle', 'Alimentation équilibrée, exercice régulier', 'Lisinopril 10mg 1x/jour', 1, false),
    (5, 3, 'Diabète type 2', 'Régime alimentaire adapté, activité physique', 'Metformine 500mg 2x/jour', 1, false)
ON CONFLICT DO NOTHING;

-- Vérifier les données
SELECT 'Users created: ' || COUNT(*) FROM users;
SELECT 'Appointments created: ' || COUNT(*) FROM appointments;
SELECT 'Medical records created: ' || COUNT(*) FROM medical_records;

COMMIT;

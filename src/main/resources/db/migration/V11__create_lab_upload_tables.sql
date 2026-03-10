CREATE TABLE IF NOT EXISTS lab_uploaded_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(320) NOT NULL,
    absolute_path VARCHAR(1000) NOT NULL,
    content_type VARCHAR(120),
    file_size BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_lab_uploaded_files_stored_name (stored_name),
    INDEX idx_lab_uploaded_files_uploaded_at (uploaded_at)
);

CREATE TABLE IF NOT EXISTS lab_execution_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    stored_name VARCHAR(320) NOT NULL,
    command_text VARCHAR(1000),
    simulated_executed BOOLEAN DEFAULT FALSE,
    simulated_output VARCHAR(1000),
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_lab_execution_attempts_stored_name (stored_name),
    INDEX idx_lab_execution_attempts_executed_at (executed_at)
);

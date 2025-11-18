package com.yudha.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hospital Management System - Main Application
 *
 * This is the main entry point for the HMS Backend application.
 * Designed for Indonesian healthcare facilities with BPJS and SATUSEHAT integration.
 *
 * Note: JPA Auditing is configured in JpaAuditConfiguration class
 *
 * @author Yudha
 * @version 1.0.0
 * @since 2025-01-18
 */
@SpringBootApplication
public class HmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsBackendApplication.class, args);
        System.out.println("""

            ========================================
            Hospital Management System (HMS)
            Version: 1.0.0
            Environment: Development
            Database: PostgreSQL 16.6
            Java Version: 21 LTS
            Spring Boot: 3.4.1
            Timezone: Asia/Jakarta
            Locale: id_ID
            ========================================
            Application is running successfully!
            Access API at: http://localhost:8080
            Health Check: http://localhost:8080/actuator/health
            ========================================
            """);
    }
}
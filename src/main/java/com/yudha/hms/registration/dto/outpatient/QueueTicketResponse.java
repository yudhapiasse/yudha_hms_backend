package com.yudha.hms.registration.dto.outpatient;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO for queue ticket printing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueTicketResponse {

    // ========== Registration Information ==========
    private String registrationNumber;
    private String queueCode;
    private Integer queueNumber;

    // ========== Patient Information ==========
    private String patientName;
    private String patientMrn;

    // ========== Polyclinic and Doctor ==========
    private String polyclinicName;
    private String polyclinicLocation;
    private String polyclinicBuilding;
    private String polyclinicFloor;

    private String doctorName;
    private String doctorTitle;

    // ========== Date and Time ==========
    private LocalDate registrationDate;
    private LocalTime registrationTime;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    // ========== Wait Time ==========
    private Integer estimatedWaitTimeMinutes;
    private Integer patientsAhead;

    // ========== Ticket Content ==========
    private String ticketText; // Plain text format
    private String ticketHtml; // HTML format for web display
    private String ticketPdfBase64; // Base64-encoded PDF

    // ========== Helper Methods ==========

    /**
     * Generate plain text ticket.
     */
    public String generatePlainTextTicket() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("════════════════════════════════════\n");
        ticket.append("    RUMAH SAKIT UMUM MAKASSAR      \n");
        ticket.append("                                    \n");
        ticket.append("        NOMOR ANTRIAN PASIEN        \n");
        ticket.append("                                    \n");
        ticket.append("            [  ").append(queueCode).append("  ]              \n");
        ticket.append("                                    \n");
        ticket.append(" Tanggal: ").append(formatDate(registrationDate)).append("\n");
        ticket.append(" Waktu: ").append(formatTime(registrationTime)).append("\n");
        ticket.append("                                    \n");
        ticket.append(" Poliklinik: ").append(polyclinicName).append("\n");
        ticket.append(" Dokter: ").append(doctorName).append("\n");
        ticket.append(" Lokasi: ").append(polyclinicBuilding).append(" - ").append(polyclinicFloor).append("\n");
        ticket.append("                                    \n");
        ticket.append(" No. Registrasi:                    \n");
        ticket.append(" ").append(registrationNumber).append("\n");
        ticket.append("                                    \n");
        ticket.append(" Pasien: ").append(patientName).append("\n");
        ticket.append(" No. RM: ").append(patientMrn).append("\n");
        ticket.append("                                    \n");

        if (estimatedWaitTimeMinutes != null) {
            ticket.append(" Estimasi Waktu Tunggu: ");
            ticket.append(formatWaitTime(estimatedWaitTimeMinutes)).append("\n");
        }

        if (patientsAhead != null && patientsAhead > 0) {
            ticket.append(" Pasien di Depan: ").append(patientsAhead).append("\n");
        }

        ticket.append("                                    \n");
        ticket.append(" Harap menunggu panggilan di        \n");
        ticket.append(" ruang tunggu poliklinik            \n");
        ticket.append("════════════════════════════════════\n");

        return ticket.toString();
    }

    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy", new java.util.Locale("id", "ID")));
    }

    private String formatTime(LocalTime time) {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) + " WITA";
    }

    private String formatWaitTime(int minutes) {
        if (minutes < 60) {
            return minutes + " menit";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        return hours + " jam " + mins + " menit";
    }
}
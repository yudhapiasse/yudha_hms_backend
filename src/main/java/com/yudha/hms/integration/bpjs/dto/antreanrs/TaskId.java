package com.yudha.hms.integration.bpjs.dto.antreanrs;

/**
 * BPJS Antrean RS Task ID Constants.
 *
 * Task IDs represent different stages in the patient journey:
 * - Task 1: Patient arrival at hospital (start admission waiting)
 * - Task 2: Registration desk service (end admission waiting/start admission service)
 * - Task 3: Polyclinic queue (end admission service/start poly waiting)
 * - Task 4: Doctor consultation (end poly waiting/start poly service)
 * - Task 5: Pharmacy queue (end poly service/start pharmacy waiting)
 * - Task 6: Prescription ready (end pharmacy waiting/start pharmacy service)
 * - Task 7: Billing (end pharmacy service)
 * - Task 99: No show/canceled
 *
 * Flow for new patients: 1 → 2 → 3 → 4 → 5 (+ 6 → 7 if prescription)
 * Flow for returning patients: 3 → 4 → 5 (+ 6 → 7 if prescription)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public enum TaskId {

    /**
     * Task 1: Patient arrival at hospital.
     * Mulai waktu tunggu admisi (check-in for new patients).
     */
    CHECKIN_ARRIVAL(1, "mulai waktu tunggu admisi", "Patient Arrival"),

    /**
     * Task 2: Registration desk service start.
     * Akhir waktu tunggu admisi/mulai waktu layan admisi.
     */
    ADMISSION_START(2, "mulai waktu layan admisi", "Admission Service Start"),

    /**
     * Task 3: Polyclinic queue start.
     * Akhir waktu layan admisi/mulai waktu tunggu poli.
     */
    POLY_WAITING(3, "mulai waktu tunggu poli", "Polyclinic Waiting"),

    /**
     * Task 4: Doctor consultation start.
     * Akhir waktu tunggu poli/mulai waktu layan poli.
     */
    POLY_SERVICE(4, "mulai waktu layan poli", "Doctor Consultation"),

    /**
     * Task 5: Pharmacy queue start.
     * Akhir waktu layan poli/mulai waktu tunggu farmasi.
     */
    PHARMACY_WAITING(5, "mulai waktu tunggu farmasi", "Pharmacy Waiting"),

    /**
     * Task 6: Prescription preparation start.
     * Akhir waktu tunggu farmasi/mulai waktu layan farmasi.
     */
    PHARMACY_SERVICE(6, "mulai waktu layan farmasi", "Prescription Preparation"),

    /**
     * Task 7: Billing/checkout.
     * Akhir waktu obat selesai dibuat.
     */
    FINISHED(7, "selesai dilayani", "Service Completed"),

    /**
     * Task 99: No show or canceled.
     * Tidak hadir/batal.
     */
    CANCELED(99, "tidak hadir/batal", "No Show/Canceled");

    private final int id;
    private final String taskName;
    private final String description;

    TaskId(int id, String taskName, String description) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get TaskId from integer value.
     *
     * @param id Task ID number
     * @return TaskId enum
     * @throws IllegalArgumentException if invalid task ID
     */
    public static TaskId fromId(int id) {
        for (TaskId task : values()) {
            if (task.id == id) {
                return task;
            }
        }
        throw new IllegalArgumentException("Invalid task ID: " + id);
    }

    /**
     * Check if this is the check-in task for new patients.
     */
    public boolean isCheckInTask() {
        return this == CHECKIN_ARRIVAL;
    }

    /**
     * Check if this is a completion task.
     */
    public boolean isCompletionTask() {
        return this == FINISHED || this == CANCELED;
    }
}

package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Physical Examination Entity.
 *
 * Comprehensive physical examination documentation following systematic approach.
 * Covers all body systems for complete patient assessment.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "physical_examination", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_physical_exam_encounter", columnList = "encounter_id"),
        @Index(name = "idx_physical_exam_patient", columnList = "patient_id"),
        @Index(name = "idx_physical_exam_date", columnList = "examination_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Physical examination findings and documentation")
public class PhysicalExamination extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @Column(name = "examination_date", nullable = false)
    @NotNull(message = "Examination date is required")
    @Builder.Default
    private LocalDateTime examinationDate = LocalDateTime.now();

    // ========== General Appearance ==========
    @Column(name = "general_appearance", columnDefinition = "TEXT")
    private String generalAppearance; // Well-nourished, alert, oriented, in distress, etc.

    @Column(name = "conscious_level", length = 50)
    private String consciousLevel; // ALERT, DROWSY, STUPOR, COMA

    @Column(name = "nutritional_status", length = 50)
    private String nutritionalStatus; // WELL_NOURISHED, UNDERNOURISHED, OBESE

    @Column(name = "hydration_status", length = 50)
    private String hydrationStatus; // WELL_HYDRATED, DEHYDRATED

    @Column(name = "distress_level", length = 50)
    private String distressLevel; // NONE, MILD, MODERATE, SEVERE

    // ========== Head, Eyes, Ears, Nose, Throat (HEENT) ==========
    @Column(name = "head", columnDefinition = "TEXT")
    private String head; // Normocephalic, atraumatic, etc.

    @Column(name = "eyes", columnDefinition = "TEXT")
    private String eyes; // Pupils equal and reactive to light (PERL), conjunctiva, sclera

    @Column(name = "ears", columnDefinition = "TEXT")
    private String ears; // Tympanic membranes intact, hearing

    @Column(name = "nose", columnDefinition = "TEXT")
    private String nose; // Patent nares, discharge, septum

    @Column(name = "throat", columnDefinition = "TEXT")
    private String throat; // Pharynx, tonsils, oral mucosa

    @Column(name = "neck", columnDefinition = "TEXT")
    private String neck; // Lymph nodes, thyroid, jugular venous distension

    // ========== Cardiovascular System ==========
    @Column(name = "cardiovascular", columnDefinition = "TEXT")
    private String cardiovascular; // Heart sounds, murmurs, rhythm

    @Column(name = "heart_sounds", length = 200)
    private String heartSounds; // S1 S2, regular rhythm, no murmurs

    @Column(name = "peripheral_pulses", length = 200)
    private String peripheralPulses; // Radial, dorsalis pedis, posterior tibial

    @Column(name = "edema", columnDefinition = "TEXT")
    private String edema; // Location, pitting, severity

    // ========== Respiratory System ==========
    @Column(name = "respiratory", columnDefinition = "TEXT")
    private String respiratory; // Breath sounds, effort, pattern

    @Column(name = "chest_inspection", columnDefinition = "TEXT")
    private String chestInspection; // Symmetric expansion, respiratory effort

    @Column(name = "chest_auscultation", columnDefinition = "TEXT")
    private String chestAuscultation; // Bilateral breath sounds, crackles, wheezes

    @Column(name = "chest_percussion", columnDefinition = "TEXT")
    private String chestPercussion; // Resonant, dull

    // ========== Gastrointestinal/Abdomen ==========
    @Column(name = "abdomen", columnDefinition = "TEXT")
    private String abdomen; // Inspection, auscultation, percussion, palpation

    @Column(name = "abdomen_inspection", columnDefinition = "TEXT")
    private String abdomenInspection; // Flat, distended, scars

    @Column(name = "abdomen_auscultation", columnDefinition = "TEXT")
    private String abdomenAuscultation; // Bowel sounds present/absent

    @Column(name = "abdomen_palpation", columnDefinition = "TEXT")
    private String abdomenPalpation; // Soft, tender, masses, organomegaly

    @Column(name = "abdomen_percussion", columnDefinition = "TEXT")
    private String abdomenPercussion; // Tympanic, shifting dullness

    // ========== Neurological System ==========
    @Column(name = "neurological", columnDefinition = "TEXT")
    private String neurological; // Mental status, cranial nerves, motor, sensory, reflexes

    @Column(name = "mental_status", columnDefinition = "TEXT")
    private String mentalStatus; // Oriented x3, memory, judgment

    @Column(name = "cranial_nerves", columnDefinition = "TEXT")
    private String cranialNerves; // CN I-XII assessment

    @Column(name = "motor_function", columnDefinition = "TEXT")
    private String motorFunction; // Strength, tone, coordination

    @Column(name = "sensory_function", columnDefinition = "TEXT")
    private String sensoryFunction; // Light touch, pain, temperature

    @Column(name = "reflexes", columnDefinition = "TEXT")
    private String reflexes; // Deep tendon reflexes, Babinski

    @Column(name = "gait", columnDefinition = "TEXT")
    private String gait; // Normal, antalgic, ataxic

    // ========== Musculoskeletal System ==========
    @Column(name = "musculoskeletal", columnDefinition = "TEXT")
    private String musculoskeletal; // Range of motion, deformities, tenderness

    @Column(name = "spine", columnDefinition = "TEXT")
    private String spine; // Curvature, tenderness, range of motion

    @Column(name = "joints", columnDefinition = "TEXT")
    private String joints; // Swelling, tenderness, range of motion

    @Column(name = "extremities", columnDefinition = "TEXT")
    private String extremities; // Upper and lower extremities assessment

    // ========== Skin and Integumentary ==========
    @Column(name = "skin", columnDefinition = "TEXT")
    private String skin; // Color, turgor, lesions, rashes

    @Column(name = "mucous_membranes", columnDefinition = "TEXT")
    private String mucousMembranes; // Moist, dry, cyanotic

    // ========== Genitourinary (if applicable) ==========
    @Column(name = "genitourinary", columnDefinition = "TEXT")
    private String genitourinary; // External genitalia, CVA tenderness

    // ========== Additional Systems ==========
    @Column(name = "lymphatic", columnDefinition = "TEXT")
    private String lymphatic; // Lymph node examination

    @Column(name = "breasts", columnDefinition = "TEXT")
    private String breasts; // If applicable

    // ========== Overall Assessment ==========
    @Column(name = "overall_impression", columnDefinition = "TEXT")
    private String overallImpression;

    @Column(name = "abnormal_findings", columnDefinition = "TEXT")
    private String abnormalFindings;

    @Column(name = "clinical_significance", columnDefinition = "TEXT")
    private String clinicalSignificance;

    // ========== Examiner Information ==========
    @Column(name = "examiner_id", nullable = false)
    @NotNull(message = "Examiner ID is required")
    private UUID examinerId;

    @Column(name = "examiner_name", nullable = false, length = 200)
    private String examinerName;

    @Column(name = "examiner_specialty", length = 100)
    private String examinerSpecialty;

    // ========== Signature/Verification ==========
    @Column(name = "is_signed")
    @Builder.Default
    private Boolean isSigned = false;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "digital_signature", columnDefinition = "TEXT")
    private String digitalSignature; // Base64 encoded signature or signature ID

    // ========== Business Methods ==========

    /**
     * Check if examination is complete.
     */
    public boolean isComplete() {
        return generalAppearance != null &&
               cardiovascular != null &&
               respiratory != null &&
               abdomen != null &&
               neurological != null;
    }

    /**
     * Sign the examination.
     */
    public void sign(String signature) {
        this.isSigned = true;
        this.signedAt = LocalDateTime.now();
        this.digitalSignature = signature;
    }

    /**
     * Check if has abnormal findings.
     */
    public boolean hasAbnormalFindings() {
        return abnormalFindings != null && !abnormalFindings.isEmpty();
    }
}

package com.lms.www.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_proctor_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamProctorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Builder.Default
    @Column(name = "enable_camera")
    private Boolean enableCamera = false;

    @Builder.Default
    @Column(name = "enable_microphone")
    private Boolean enableMicrophone = false;

    @Builder.Default
    @Column(name = "enable_screen_share")
    private Boolean enableScreenShare = false;

    @Builder.Default
    @Column(name = "capture_photo_interval")
    private Integer capturePhotoInterval = 0; // In seconds

    @Builder.Default
    @Column(name = "prevent_tab_switch")
    private Boolean preventTabSwitch = false;

    @Builder.Default
    @Column(name = "max_tab_switches")
    private Integer maxTabSwitches = 0;

    @Builder.Default
    @Column(name = "block_clipboard")
    private Boolean blockClipboard = false;

    @Builder.Default
    @Column(name = "enable_ai_face_detect")
    private Boolean enableAiFaceDetect = false;
}

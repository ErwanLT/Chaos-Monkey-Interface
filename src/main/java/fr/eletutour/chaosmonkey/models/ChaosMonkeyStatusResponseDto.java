package fr.eletutour.chaosmonkey.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

/**
 * ChaosMonkeyStatusResponseDto
 */
public class ChaosMonkeyStatusResponseDto {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime enabledAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime disabledAt;

    private ValueDuration enabledFor;

    private Boolean enabled;

    public ChaosMonkeyStatusResponseDto() {
    }

    public ChaosMonkeyStatusResponseDto(OffsetDateTime enabledAt, OffsetDateTime disabledAt, ValueDuration enabledFor, Boolean enabled) {
        this.enabledAt = enabledAt;
        this.disabledAt = disabledAt;
        this.enabledFor = enabledFor;
        this.enabled = enabled;
    }

    public OffsetDateTime getEnabledAt() {
        return enabledAt;
    }

    public void setEnabledAt(OffsetDateTime enabledAt) {
        this.enabledAt = enabledAt;
    }

    public OffsetDateTime getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(OffsetDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }

    public ValueDuration getEnabledFor() {
        return enabledFor;
    }

    public void setEnabledFor(ValueDuration enabledFor) {
        this.enabledFor = enabledFor;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ChaosMonkeyStatusResponseDto{" +
                "enabledAt=" + enabledAt +
                ", disabledAt=" + disabledAt +
                ", enabledFor=" + enabledFor +
                ", enabled=" + enabled +
                '}';
    }
}
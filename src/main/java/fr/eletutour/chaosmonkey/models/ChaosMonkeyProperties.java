package fr.eletutour.chaosmonkey.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * ChaosMonkeyProperties
 */
public class ChaosMonkeyProperties {

    private Boolean enabled;

    private Long lastEnabledToggleTimestamp;

    private String togglePrefix;

    public ChaosMonkeyProperties() {
    }

    public ChaosMonkeyProperties(Boolean enabled, Long lastEnabledToggleTimestamp, String togglePrefix) {
        this.enabled = enabled;
        this.lastEnabledToggleTimestamp = lastEnabledToggleTimestamp;
        this.togglePrefix = togglePrefix;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getLastEnabledToggleTimestamp() {
        return lastEnabledToggleTimestamp;
    }

    public void setLastEnabledToggleTimestamp(Long lastEnabledToggleTimestamp) {
        this.lastEnabledToggleTimestamp = lastEnabledToggleTimestamp;
    }

    public String getTogglePrefix() {
        return togglePrefix;
    }

    public void setTogglePrefix(String togglePrefix) {
        this.togglePrefix = togglePrefix;
    }

    @Override
    public String toString() {
        return "ChaosMonkeyProperties{" +
                "enabled=" + enabled +
                ", lastEnabledToggleTimestamp=" + lastEnabledToggleTimestamp +
                ", togglePrefix='" + togglePrefix + '\'' +
                '}';
    }
}


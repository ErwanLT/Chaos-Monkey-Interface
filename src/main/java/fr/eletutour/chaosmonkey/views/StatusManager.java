package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import fr.eletutour.chaosmonkey.models.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusManager.class);
    private final ChaosMonkeyService chaosMonkeyService;
    private boolean chaosMonkeyEnabled;
    private Span statusBadge;

    public StatusManager(ChaosMonkeyService chaosMonkeyService) {
        this.chaosMonkeyService = chaosMonkeyService;
    }

    public void loadStatus() throws Exception {
        ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
        this.chaosMonkeyEnabled = Boolean.TRUE.equals(status.getEnabled());
        if (statusBadge != null) {
            updateStatusBadge();
        }
    }

    public Span createStatusBadge() {
        statusBadge = new Span(getStatusText());
        updateStatusBadgeStyle();
        return statusBadge;
    }

    public boolean isChaosMonkeyEnabled() {
        return chaosMonkeyEnabled;
    }

    private void updateStatusBadge() {
        try {
            ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
            String statusText = Boolean.TRUE.equals(status.getEnabled()) ? "Actif" : "Inactif";
            statusBadge.setText(statusText);
            updateStatusBadgeStyle();
        } catch (Exception e) {
            Notification.show("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    private String getStatusText() {
        return chaosMonkeyEnabled ? "Actif" : "Inactif";
    }

    private void updateStatusBadgeStyle() {
        statusBadge.getStyle().set("padding", "var(--lumo-space-xs) var(--lumo-space-s)");
        statusBadge.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        statusBadge.getStyle().set("font-weight", "600");
        if (chaosMonkeyEnabled) {
            statusBadge.getStyle().set("background-color", "var(--lumo-success-color-10pct)");
            statusBadge.getStyle().set("color", "var(--lumo-success-text-color)");
        } else {
            statusBadge.getStyle().set("background-color", "var(--lumo-error-color-10pct)");
            statusBadge.getStyle().set("color", "var(--lumo-error-text-color)");
        }
    }
}
package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlPanelComponent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanelComponent.class);
    private final ChaosMonkeyService chaosMonkeyService;
    private final StatusManager statusManager;
    private final Runnable refreshCallback;
    private Button toggleButton;

    public ControlPanelComponent(ChaosMonkeyService chaosMonkeyService, StatusManager statusManager, Runnable refreshCallback) {
        this.chaosMonkeyService = chaosMonkeyService;
        this.statusManager = statusManager;
        this.refreshCallback = refreshCallback;

        add(new H2("Contrôle"));
        add(createControlPanel());
    }

    private Component createControlPanel() {
        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setAlignItems(FlexComponent.Alignment.CENTER);

        toggleButton = new Button("Activer Chaos Monkey", VaadinIcon.POWER_OFF.create(), e -> toggleChaosMonkey());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButtonState();

        Button refreshButton = new Button("Rafraîchir", VaadinIcon.REFRESH.create());
        refreshButton.getElement().setAttribute("title",
            "Utile uniquement si l'application Chaos Monkey a été modifiée par une autre source " +
            "ou si vous suspectez une désynchronisation entre l'interface et la configuration réelle.");
        refreshButton.addClickListener(e -> {
            try {
                refreshButton.setEnabled(false);
                refreshButton.setText("Chargement...");
                refreshCallback.run();
                Notification.show("Configurations rechargées avec succès", 3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Erreur lors du rechargement: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            } finally {
                refreshButton.setEnabled(true);
                refreshButton.setText("Rafraîchir");
            }
        });
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        controlPanel.add(toggleButton, refreshButton);
        return controlPanel;
    }

    private void toggleChaosMonkey() {
        try {
            if (!statusManager.isChaosMonkeyEnabled()) {
                chaosMonkeyService.enable();
            } else {
                chaosMonkeyService.disable();
            }
            Thread.sleep(500);
            statusManager.loadStatus();
            updateButtonState();
        } catch (Exception e) {
            Notification.show("Erreur lors du changement d'état: " + e.getMessage());
        }
    }

    private void updateButtonState() {
        if (statusManager.isChaosMonkeyEnabled()) {
            toggleButton.setText("Désactiver Chaos Monkey");
            toggleButton.setIcon(VaadinIcon.POWER_OFF.create());
            toggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        } else {
            toggleButton.setText("Activer Chaos Monkey");
            toggleButton.setIcon(VaadinIcon.POWER_OFF.create());
            toggleButton.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            toggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
    }
}
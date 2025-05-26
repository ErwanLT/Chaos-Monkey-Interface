package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.models.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.models.WatcherProperties;
import fr.eletutour.chaosmonkey.models.WatcherPropertiesUpdate;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Route(value = "")
@PageTitle("Chaos Monkey Dashboard")
public class ChaosMonkeyView extends AppLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyView.class);
    
    private boolean chaosMonkeyEnabled = false;
    private final ChaosMonkeyService chaosMonkeyService;
    private Span statusBadge;
    private VerticalLayout mainContent;
    private Button toggleButton;
    
    // Binders pour les formulaires
    private final Binder<WatcherPropertiesUpdate> watcherBinder = new Binder<>(WatcherPropertiesUpdate.class);
    private final Binder<AssaultPropertiesUpdate> assaultBinder = new Binder<>(AssaultPropertiesUpdate.class);
    
    // Modèles de données
    private WatcherPropertiesUpdate watcherModel;
    private AssaultPropertiesUpdate assaultModel;

    public ChaosMonkeyView(ChaosMonkeyService chaosMonkeyService) {
        this.chaosMonkeyService = chaosMonkeyService;
        
        // Initialiser les modèles avec les données actuelles
        try {
            // Initialiser le modèle watcher
            initWatcherProperties(chaosMonkeyService);

            // Initialiser le modèle assault
            this.assaultModel = chaosMonkeyService.getAssaultProperties();
            
            LOGGER.info("Modèles initialisés avec succès dans le constructeur");
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'initialisation des modèles", e);
            // On crée des modèles vides en cas d'erreur
            this.watcherModel = new WatcherPropertiesUpdate();
            this.assaultModel = new AssaultPropertiesUpdate();
        }
    }

    private void initWatcherProperties(ChaosMonkeyService chaosMonkeyService) throws Exception {
        WatcherProperties watcherProps = chaosMonkeyService.getWatcherProperties();
        this.watcherModel = new WatcherPropertiesUpdate();
        this.watcherModel.setController(watcherProps.getController());
        this.watcherModel.setRestController(watcherProps.getRestController());
        this.watcherModel.setService(watcherProps.getService());
        this.watcherModel.setRepository(watcherProps.getRepository());
        this.watcherModel.setComponent(watcherProps.getComponent());
        this.watcherModel.setRestTemplate(watcherProps.getRestTemplate());
        this.watcherModel.setWebClient(watcherProps.getWebClient());
        this.watcherModel.setActuatorHealth(watcherProps.getActuatorHealth());

        if (watcherProps.getBeans() != null) {
            this.watcherModel.setBeans(new ArrayList<>(watcherProps.getBeans()));
        } else {
            this.watcherModel.setBeans(new ArrayList<>());
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.statusBadge = createStatusBadge();
        this.mainContent = new VerticalLayout();
        
        try {
            // Charger le statut uniquement (les modèles sont déjà initialisés)
            ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
            this.chaosMonkeyEnabled = Boolean.TRUE.equals(status.getEnabled());
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement du statut", e);
            Notification.show("Erreur lors du chargement du statut: " + e.getMessage(), 
                    5000, Notification.Position.MIDDLE);
        }
        
        createHeader();
        createMainContent();
        
        setContent(mainContent);
        updateStatusBadge();
        updateButtonState();
    }

    private void loadCurrentConfiguration() {
        try {
            // Recharger le statut
            ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
            this.chaosMonkeyEnabled = Boolean.TRUE.equals(status.getEnabled());
            updateStatusBadge();
            updateButtonState();
            
            // Recharger et mettre à jour le modèle watcher
            try {
                WatcherProperties watcherProps = chaosMonkeyService.getWatcherProperties();
                this.watcherModel.setController(watcherProps.getController());
                this.watcherModel.setRestController(watcherProps.getRestController());
                this.watcherModel.setService(watcherProps.getService());
                this.watcherModel.setRepository(watcherProps.getRepository());
                this.watcherModel.setComponent(watcherProps.getComponent());
                this.watcherModel.setRestTemplate(watcherProps.getRestTemplate());
                this.watcherModel.setWebClient(watcherProps.getWebClient());
                this.watcherModel.setActuatorHealth(watcherProps.getActuatorHealth());
                
                if (watcherProps.getBeans() != null) {
                    this.watcherModel.setBeans(new ArrayList<>(watcherProps.getBeans()));
                }
                
                // Mettre à jour le binder
                watcherBinder.readBean(watcherModel);
                LOGGER.info("Modèle watcher rechargé avec succès");
            } catch (Exception e) {
                LOGGER.error("Erreur lors du rechargement du modèle watcher", e);
                Notification.show("Erreur lors du rechargement des propriétés watcher: " + e.getMessage(), 
                        5000, Notification.Position.MIDDLE);
            }
            
            // Recharger et mettre à jour le modèle assault
            try {
                this.assaultModel = chaosMonkeyService.getAssaultProperties();
                // Mettre à jour le binder
                assaultBinder.readBean(assaultModel);
                LOGGER.info("Modèle assault rechargé avec succès");
            } catch (Exception e) {
                LOGGER.error("Erreur lors du rechargement du modèle assault", e);
                Notification.show("Erreur lors du rechargement des propriétés assault: " + e.getMessage(), 
                        5000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors du rechargement de la configuration", e);
            Notification.show("Erreur lors du rechargement du statut: " + e.getMessage(), 
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        H1 brand = new H1("Chaos Monkey Dashboard");
        brand.addClassNames(
            LumoUtility.FontSize.XLARGE,
            LumoUtility.Margin.NONE
        );

        header.add(brand, statusBadge);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.SMALL,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Background.CONTRAST_5
        );

        addToNavbar(header);
    }

    private void createMainContent() {
        mainContent.setSpacing(false);
        mainContent.setPadding(true);
        
        // Créer un accordion pour organiser les sections
        Accordion mainAccordion = new Accordion();
        mainAccordion.setWidthFull();
        
        // Améliorer le style des en-têtes de l'accordion principal
        mainAccordion.getElement().getStyle().set("--lumo-font-size-m", "var(--lumo-font-size-l)");
        mainAccordion.getElement().getStyle().set("--lumo-font-weight-semibold", "900");
        
        // Section de contrôle (garder cette section toujours visible, pas dans l'accordion)
        VerticalLayout controlSection = new VerticalLayout();
        controlSection.add(new H2("Contrôle"));
        controlSection.add(createControlPanel());
        
        // Ajouter les sections à l'accordion avec des titres
        mainAccordion.add("Configuration des Watchers", new WatcherPanelComponent(chaosMonkeyService, watcherBinder, watcherModel));
        
        // Ajouter directement le panneau d'assaults
        mainAccordion.add("Configuration des Assaults", new AssaultPanelComponent(chaosMonkeyService, assaultBinder, assaultModel));
        
        // Ajouter le panneau de contrôle et l'accordion au contenu principal
        mainContent.add(controlSection, mainAccordion);
    }

    private Component createControlPanel() {
        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setAlignItems(FlexComponent.Alignment.CENTER);

        toggleButton = new Button("Activer Chaos Monkey", 
            VaadinIcon.POWER_OFF.create(),
            e -> toggleChaosMonkey());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButtonState();

        final Button refreshButton = new Button("Rafraîchir", VaadinIcon.REFRESH.create());
        refreshButton.getElement().setAttribute("title", 
            "Utile uniquement si l'application Chaos Monkey a été modifiée par une autre source " +
            "ou si vous suspectez une désynchronisation entre l'interface et la configuration réelle.");
        
        refreshButton.addClickListener(e -> {
            try {
                // Désactiver le bouton pendant le chargement
                refreshButton.setEnabled(false);
                refreshButton.setText("Chargement...");
                
                // Recharger les configurations
                loadCurrentConfiguration();
                
                Notification.show("Configurations rechargées avec succès", 
                        3000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Erreur lors du rechargement: " + ex.getMessage(), 
                        5000, Notification.Position.MIDDLE);
            } finally {
                // Réactiver le bouton
                refreshButton.setEnabled(true);
                refreshButton.setText("Rafraîchir");
            }
        });
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        controlPanel.add(toggleButton, refreshButton);
        return controlPanel;
    }

    private void updateButtonState() {
        if (chaosMonkeyEnabled) {
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

    private void toggleChaosMonkey() {
        try {
            if (!chaosMonkeyEnabled) {
                chaosMonkeyService.enable();
            } else {
                chaosMonkeyService.disable();
            }
            
            // Attendre un peu pour laisser le temps au changement d'état de se propager
            Thread.sleep(500);
            
            ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
            this.chaosMonkeyEnabled = Boolean.TRUE.equals(status.getEnabled());
            updateStatusBadge();
            updateButtonState();
        } catch (Exception e) {
            Notification.show("Erreur lors du changement d'état: " + e.getMessage());
        }
    }

    private Span createStatusBadge() {
        Span badge = new Span(getStatusText());
        updateStatusBadgeStyle(badge);
        return badge;
    }

    private void updateStatusBadge() {
        try {
            ChaosMonkeyStatusResponseDto status = chaosMonkeyService.getStatus();
            String statusText = Boolean.TRUE.equals(status.getEnabled()) ? "Actif" : "Inactif";
            statusBadge.setText(statusText);
            updateStatusBadgeStyle(statusBadge);
        } catch (Exception e) {
            Notification.show("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    private String getStatusText() {
        return chaosMonkeyEnabled ? "Actif" : "Inactif";
    }

    private void updateStatusBadgeStyle(Span badge) {
        badge.getStyle().set("padding", "var(--lumo-space-xs) var(--lumo-space-s)");
        badge.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        badge.getStyle().set("font-weight", "600");
        
        if (chaosMonkeyEnabled) {
            badge.getStyle().set("background-color", "var(--lumo-success-color-10pct)");
            badge.getStyle().set("color", "var(--lumo-success-text-color)");
        } else {
            badge.getStyle().set("background-color", "var(--lumo-error-color-10pct)");
            badge.getStyle().set("color", "var(--lumo-error-text-color)");
        }
    }
}

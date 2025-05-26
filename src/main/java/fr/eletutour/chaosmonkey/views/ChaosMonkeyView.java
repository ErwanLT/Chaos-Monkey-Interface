package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;
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

    private final ChaosMonkeyService chaosMonkeyService;
    private final StatusManager statusManager;
    private VerticalLayout mainContent;

    // Binders et modèles
    private final Binder<WatcherPropertiesUpdate> watcherBinder = new Binder<>(WatcherPropertiesUpdate.class);
    private final Binder<AssaultPropertiesUpdate> assaultBinder = new Binder<>(AssaultPropertiesUpdate.class);
    private WatcherPropertiesUpdate watcherModel;
    private AssaultPropertiesUpdate assaultModel;

    public ChaosMonkeyView(ChaosMonkeyService chaosMonkeyService) {
        this.chaosMonkeyService = chaosMonkeyService;
        this.statusManager = new StatusManager(chaosMonkeyService);

        // Initialiser les modèles
        try {
            this.watcherModel = initWatcherProperties();
            this.assaultModel = chaosMonkeyService.getAssaultProperties();
            LOGGER.info("Modèles initialisés avec succès dans le constructeur");
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'initialisation des modèles", e);
            this.watcherModel = new WatcherPropertiesUpdate();
            this.assaultModel = new AssaultPropertiesUpdate();
        }
    }

    private WatcherPropertiesUpdate initWatcherProperties() throws Exception {
        WatcherProperties watcherProps = chaosMonkeyService.getWatcherProperties();
        WatcherPropertiesUpdate model = new WatcherPropertiesUpdate();
        model.setController(watcherProps.getController());
        model.setRestController(watcherProps.getRestController());
        model.setService(watcherProps.getService());
        model.setRepository(watcherProps.getRepository());
        model.setComponent(watcherProps.getComponent());
        model.setRestTemplate(watcherProps.getRestTemplate());
        model.setWebClient(watcherProps.getWebClient());
        model.setActuatorHealth(watcherProps.getActuatorHealth());
        model.setBeans(watcherProps.getBeans() != null ? new ArrayList<>(watcherProps.getBeans()) : new ArrayList<>());
        return model;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.mainContent = new VerticalLayout();

        try {
            statusManager.loadStatus();
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement du statut", e);
            Notification.show("Erreur lors du chargement du statut: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }

        HeaderComponent header = new HeaderComponent(statusManager);
        ControlPanelComponent controlPanel = new ControlPanelComponent(chaosMonkeyService, statusManager, this::loadCurrentConfiguration);
        WatcherPanelComponent watcherPanel = new WatcherPanelComponent(chaosMonkeyService, watcherBinder, watcherModel);
        AssaultPanelComponent assaultPanel = new AssaultPanelComponent(chaosMonkeyService, assaultBinder, assaultModel);

        Accordion mainAccordion = new Accordion();
        mainAccordion.setWidthFull();
        mainAccordion.getElement().getStyle().set("--lumo-font-size-m", "var(--lumo-font-size-l)");
        mainAccordion.getElement().getStyle().set("--lumo-font-weight-semibold", "900");

        mainAccordion.add("Configuration des Watchers", watcherPanel);
        mainAccordion.add("Configuration des Assaults", assaultPanel);

        mainContent.setSpacing(false);
        mainContent.setPadding(true);
        mainContent.add(controlPanel, mainAccordion);

        addToNavbar(header);
        setContent(mainContent);
    }

    private void loadCurrentConfiguration() {
        try {
            statusManager.loadStatus();
            watcherModel = initWatcherProperties();
            watcherBinder.readBean(watcherModel);
            assaultModel = chaosMonkeyService.getAssaultProperties();
            assaultBinder.readBean(assaultModel);
            LOGGER.info("Configurations rechargées avec succès");
        } catch (Exception e) {
            LOGGER.error("Erreur lors du rechargement de la configuration", e);
            Notification.show("Erreur lors du rechargement: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }
}

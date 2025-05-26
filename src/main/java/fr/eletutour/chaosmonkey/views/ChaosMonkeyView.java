package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.eletutour.chaosmonkey.models.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.models.WatcherProperties;
import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.models.WatcherPropertiesUpdate;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.accordion.Accordion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        mainAccordion.add("Configuration des Watchers", createWatcherPanel());
        
        // Ajouter directement le panneau d'assaults
        mainAccordion.add("Configuration des Assaults", createAssaultPanel());
        
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

    private void updateBeansList(VerticalLayout beansList, List<String> beans) {
        beansList.removeAll();
        if (beans.isEmpty()) {
            beansList.add(new Span("Aucun bean personnalisé configuré"));
        } else {
            beans.forEach(bean -> {
                HorizontalLayout beanItem = new HorizontalLayout();
                beanItem.setAlignItems(FlexComponent.Alignment.CENTER);
                
                Span beanName = new Span(bean);
                Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> {
                    beans.remove(bean);
                    watcherModel.setBeans(new ArrayList<>(beans));
                    updateBeansList(beansList, beans);
                });
                deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                
                beanItem.add(beanName, deleteButton);
                beansList.add(beanItem);
            });
        }
    }

    private void updateServicesList(VerticalLayout servicesList, List<String> watchedServices) {
        servicesList.removeAll();
        if (watchedServices.isEmpty()) {
            servicesList.add(new Span("Aucun service personnalisé configuré"));
        } else {
            watchedServices.forEach(service -> {
                HorizontalLayout serviceItem = new HorizontalLayout();
                serviceItem.setAlignItems(FlexComponent.Alignment.CENTER);
                
                Span serviceName = new Span(service);
                Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> {
                    watchedServices.remove(service);
                    assaultModel.setWatchedCustomServices(new ArrayList<>(watchedServices));
                    updateServicesList(servicesList, watchedServices);
                });
                deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                
                serviceItem.add(serviceName, deleteButton);
                servicesList.add(serviceItem);
            });
        }
    }

    private Component createWatcherPanel() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("1000px", 3));
        
        // Checkboxes pour les différentes propriétés
        Checkbox controllerCheck = new Checkbox("Controller");
        Checkbox restControllerCheck = new Checkbox("Rest Controller");
        Checkbox serviceCheck = new Checkbox("Service");
        Checkbox repositoryCheck = new Checkbox("Repository");
        Checkbox componentCheck = new Checkbox("Component");
        Checkbox restTemplateCheck = new Checkbox("Rest Template");
        Checkbox webClientCheck = new Checkbox("Web Client");
        Checkbox actuatorHealthCheck = new Checkbox("Actuator Health");

        watcherBinder.forField(controllerCheck).bind(WatcherPropertiesUpdate::getController, WatcherPropertiesUpdate::setController);
        watcherBinder.forField(restControllerCheck).bind(WatcherPropertiesUpdate::getRestController, WatcherPropertiesUpdate::setRestController);
        watcherBinder.forField(serviceCheck).bind(WatcherPropertiesUpdate::getService, WatcherPropertiesUpdate::setService);
        watcherBinder.forField(repositoryCheck).bind(WatcherPropertiesUpdate::getRepository, WatcherPropertiesUpdate::setRepository);
        watcherBinder.forField(componentCheck).bind(WatcherPropertiesUpdate::getComponent, WatcherPropertiesUpdate::setComponent);
        watcherBinder.forField(restTemplateCheck).bind(WatcherPropertiesUpdate::getRestTemplate, WatcherPropertiesUpdate::setRestTemplate);
        watcherBinder.forField(webClientCheck).bind(WatcherPropertiesUpdate::getWebClient, WatcherPropertiesUpdate::setWebClient);
        watcherBinder.forField(actuatorHealthCheck).bind(WatcherPropertiesUpdate::getActuatorHealth, WatcherPropertiesUpdate::setActuatorHealth);
        
        // Section pour les beans personnalisés
        VerticalLayout beansSection = new VerticalLayout();
        beansSection.setSpacing(false);
        beansSection.setPadding(false);
        
        H3 beansTitle = new H3("Beans personnalisés");
        beansSection.add(beansTitle);
        
        // Liste des beans actuels avec boutons de suppression
        VerticalLayout beansList = new VerticalLayout();
        beansList.setSpacing(false);
        beansList.setPadding(false);
        
        // Référence à la liste des beans dans le modèle
        List<String> beans = new ArrayList<>();
        if (watcherModel.getBeans() != null) {
            beans.addAll(watcherModel.getBeans());
        }
        
        // Initialiser la liste des beans
        updateBeansList(beansList, beans);
        
        // Chargement des beans disponibles
        List<String> availableBeans = new ArrayList<>();
        try {
            availableBeans.addAll(chaosMonkeyService.getAvailableBeans());
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement des beans disponibles", e);
            // Continuer sans les beans disponibles
        }
        
        // Remplacer le ComboBox par un MultiSelectComboBox pour sélectionner depuis les beans disponibles
        MultiSelectComboBox<String> beanSelect = new MultiSelectComboBox<>("Sélectionner des beans");
        beanSelect.setItems(availableBeans);
        beanSelect.setWidth("400px"); // Agrandir le composant
        beanSelect.setHeight("250px"); // Définir une hauteur pour voir plusieurs éléments
        
        Button addBeanButton = new Button("Ajouter", e -> {
            Set<String> selectedBeans = beanSelect.getValue();
            if (selectedBeans != null && !selectedBeans.isEmpty()) {
                boolean added = false;
                for (String selectedBean : selectedBeans) {
                    if (!beans.contains(selectedBean)) {
                        beans.add(selectedBean);
                        added = true;
                    }
                }
                if (added) {
                    watcherModel.setBeans(new ArrayList<>(beans));
                    beanSelect.clear();
                    updateBeansList(beansList, beans);
                    Notification.show("Beans ajoutés avec succès");
                }
            }
        });
        
        HorizontalLayout beanSelectionLayout = new HorizontalLayout(beanSelect, addBeanButton);
        beanSelectionLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        
        beansSection.add(beansList, beanSelectionLayout);
        
        // Bouton de soumission
        final Button submitButton = new Button("Mettre à jour");
        submitButton.addClickListener(listener -> {
            try {
                watcherBinder.writeBean(watcherModel);
                chaosMonkeyService.updateWatcher(watcherModel);
                Notification success = new Notification("Configuration des watchers mise à jour avec succès",
                        3000, Notification.Position.MIDDLE);
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                success.open();
            } catch (Exception ex) {
                Notification error = new Notification("Erreur lors de la mise à jour: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                error.open();
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        // Charger les données dans le binder
        watcherBinder.readBean(watcherModel);
        
        // Ajout des composants au formulaire
        form.add(controllerCheck, restControllerCheck, serviceCheck);
        form.add(repositoryCheck, componentCheck, restTemplateCheck);
        form.add(webClientCheck, actuatorHealthCheck);
        form.add(beansSection, 3); // Occupe toute la largeur
        
        // Layout principal avec le formulaire et le bouton de soumission
        VerticalLayout layout = new VerticalLayout(form, submitButton);
        layout.setAlignSelf(FlexComponent.Alignment.END, submitButton);
        
        return layout;
    }

    private Component createAssaultPanel() {
        // Conteneur principal pour organiser les sections d'assaults
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        
        // Panneau de configuration générale
        FormLayout generalForm = new FormLayout();
        generalForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        IntegerField levelField = new IntegerField("Niveau");
        levelField.setMin(1);
        levelField.setMax(10000);
        
        Checkbox deterministicCheck = new Checkbox("Déterministe");
        
        // Liaison avec le binder
        assaultBinder.forField(levelField).bind(AssaultPropertiesUpdate::getLevel, AssaultPropertiesUpdate::setLevel);
        assaultBinder.forField(deterministicCheck).bind(AssaultPropertiesUpdate::getDeterministic, AssaultPropertiesUpdate::setDeterministic);
        
        generalForm.add(levelField, deterministicCheck);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Configuration générale"), generalForm);
        
        // Section latence
        FormLayout latencyForm = new FormLayout();
        latencyForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox latencyActiveCheck = new Checkbox("Activer latence");
        
        IntegerField latencyStartField = new IntegerField("Plage de début (ms)");
        latencyStartField.setMin(1);
        latencyStartField.setMax(Integer.MAX_VALUE);
        
        IntegerField latencyEndField = new IntegerField("Plage de fin (ms)");
        latencyEndField.setMin(1);
        latencyEndField.setMax(Integer.MAX_VALUE);
        
        // Désactiver les champs si la case n'est pas cochée
        latencyActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            latencyStartField.setEnabled(enabled);
            latencyEndField.setEnabled(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(latencyActiveCheck).bind(AssaultPropertiesUpdate::getLatencyActive, AssaultPropertiesUpdate::setLatencyActive);
        assaultBinder.forField(latencyStartField).bind(AssaultPropertiesUpdate::getLatencyRangeStart, AssaultPropertiesUpdate::setLatencyRangeStart);
        assaultBinder.forField(latencyEndField).bind(AssaultPropertiesUpdate::getLatencyRangeEnd, AssaultPropertiesUpdate::setLatencyRangeEnd);
        
        latencyForm.add(latencyActiveCheck, 2);
        latencyForm.add(latencyStartField, latencyEndField);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Latence"), latencyForm);
        
        // Section exceptions
        FormLayout exceptionsForm = new FormLayout();
        exceptionsForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox exceptionsActiveCheck = new Checkbox("Activer exceptions");
        TextField exceptionTypeField = new TextField("Type d'exception");
        exceptionTypeField.setPlaceholder("java.lang.RuntimeException");
        
        // Désactiver le champ si la case n'est pas cochée
        exceptionsActiveCheck.addValueChangeListener(event -> {
            exceptionTypeField.setEnabled(event.getValue());
        });
        
        // Liaison avec le binder
        assaultBinder.forField(exceptionsActiveCheck).bind(AssaultPropertiesUpdate::getExceptionsActive, AssaultPropertiesUpdate::setExceptionsActive);
        // La liaison pour exception.type nécessite un mapping spécial qu'on traitera plus tard
        
        exceptionsForm.add(exceptionsActiveCheck, 2);
        exceptionsForm.add(exceptionTypeField, 2);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Exceptions"), exceptionsForm);
        
        // Section Kill Application
        FormLayout killAppForm = new FormLayout();
        killAppForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox killAppActiveCheck = new Checkbox("Activer Kill Application");
        TextField killAppCronField = new TextField("Expression Cron");
        killAppCronField.setPlaceholder("0 0/15 * * * ?");
        
        // Label explicatif pour l'expression cron
        NativeLabel killAppCronExplanation = new NativeLabel("Planification: Non définie");
        killAppCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        killAppCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");
        
        // Mettre à jour l'explication lorsque la valeur change
        killAppCronField.addValueChangeListener(event -> {
            killAppCronExplanation.setText("Planification: " + explainCronExpression(event.getValue()));
        });
        
        // Désactiver le champ si la case n'est pas cochée
        killAppActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            killAppCronField.setEnabled(enabled);
            killAppCronExplanation.setVisible(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(killAppActiveCheck).bind(AssaultPropertiesUpdate::getKillApplicationActive, AssaultPropertiesUpdate::setKillApplicationActive);
        assaultBinder.forField(killAppCronField).bind(AssaultPropertiesUpdate::getKillApplicationCronExpression, AssaultPropertiesUpdate::setKillApplicationCronExpression);
        
        killAppForm.add(killAppActiveCheck, 2);
        killAppForm.add(killAppCronField, 2);
        killAppForm.add(killAppCronExplanation, 2);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Kill Application"), killAppForm);
        
        // Section Mémoire
        FormLayout memoryForm = new FormLayout();
        memoryForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox memoryActiveCheck = new Checkbox("Activer assault mémoire");
        TextField memoryCronField = new TextField("Expression Cron");
        memoryCronField.setPlaceholder("0 0/15 * * * ?");
        
        // Label explicatif pour l'expression cron
        NativeLabel memoryCronExplanation = new NativeLabel("Planification: Non définie");
        memoryCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        memoryCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");
        
        // Mettre à jour l'explication lorsque la valeur change
        memoryCronField.addValueChangeListener(event -> {
            memoryCronExplanation.setText("Planification: " + explainCronExpression(event.getValue()));
        });
        
        IntegerField memoryHoldField = new IntegerField("Durée de remplissage (ms)");
        memoryHoldField.setMin(1500);
        memoryHoldField.setMax(Integer.MAX_VALUE);
        
        IntegerField memoryWaitField = new IntegerField("Attente entre incréments (ms)");
        memoryWaitField.setMin(100);
        memoryWaitField.setMax(30000);
        
        NumberField memoryIncrementField = new NumberField("Fraction d'incrément");
        memoryIncrementField.setMin(0.0);
        memoryIncrementField.setMax(1.0);
        memoryIncrementField.setStep(0.05);
        
        NumberField memoryTargetField = new NumberField("Fraction cible");
        memoryTargetField.setMin(0.05);
        memoryTargetField.setMax(0.95);
        memoryTargetField.setStep(0.05);
        
        // Désactiver les champs si la case n'est pas cochée
        memoryActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            memoryCronField.setEnabled(enabled);
            memoryCronExplanation.setVisible(enabled);
            memoryHoldField.setEnabled(enabled);
            memoryWaitField.setEnabled(enabled);
            memoryIncrementField.setEnabled(enabled);
            memoryTargetField.setEnabled(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(memoryActiveCheck).bind(AssaultPropertiesUpdate::getMemoryActive, AssaultPropertiesUpdate::setMemoryActive);
        assaultBinder.forField(memoryCronField).bind(AssaultPropertiesUpdate::getMemoryCronExpression, AssaultPropertiesUpdate::setMemoryCronExpression);
        assaultBinder.forField(memoryHoldField).bind(AssaultPropertiesUpdate::getMemoryMillisecondsHoldFilledMemory, AssaultPropertiesUpdate::setMemoryMillisecondsHoldFilledMemory);
        assaultBinder.forField(memoryWaitField).bind(AssaultPropertiesUpdate::getMemoryMillisecondsWaitNextIncrease, AssaultPropertiesUpdate::setMemoryMillisecondsWaitNextIncrease);
        assaultBinder.forField(memoryIncrementField).bind(AssaultPropertiesUpdate::getMemoryFillIncrementFraction, AssaultPropertiesUpdate::setMemoryFillIncrementFraction);
        assaultBinder.forField(memoryTargetField).bind(AssaultPropertiesUpdate::getMemoryFillTargetFraction, AssaultPropertiesUpdate::setMemoryFillTargetFraction);
        
        memoryForm.add(memoryActiveCheck, 2);
        memoryForm.add(memoryCronField, 2);
        memoryForm.add(memoryCronExplanation, 2);
        memoryForm.add(memoryHoldField, memoryWaitField);
        memoryForm.add(memoryIncrementField, memoryTargetField);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Mémoire"), memoryForm);
        
        // Section CPU
        FormLayout cpuForm = new FormLayout();
        cpuForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox cpuActiveCheck = new Checkbox("Activer assault CPU");
        TextField cpuCronField = new TextField("Expression Cron");
        cpuCronField.setPlaceholder("0 0/15 * * * ?");
        
        // Label explicatif pour l'expression cron
        NativeLabel cpuCronExplanation = new NativeLabel("Planification: Non définie");
        cpuCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        cpuCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");
        
        // Mettre à jour l'explication lorsque la valeur change
        cpuCronField.addValueChangeListener(event -> {
            cpuCronExplanation.setText("Planification: " + explainCronExpression(event.getValue()));
        });
        
        IntegerField cpuHoldField = new IntegerField("Durée de charge (ms)");
        cpuHoldField.setMin(1500);
        cpuHoldField.setMax(Integer.MAX_VALUE);
        
        NumberField cpuLoadField = new NumberField("Fraction de charge");
        cpuLoadField.setMin(0.1);
        cpuLoadField.setMax(1.0);
        cpuLoadField.setStep(0.1);
        
        // Désactiver les champs si la case n'est pas cochée
        cpuActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            cpuCronField.setEnabled(enabled);
            cpuCronExplanation.setVisible(enabled);
            cpuHoldField.setEnabled(enabled);
            cpuLoadField.setEnabled(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(cpuActiveCheck).bind(AssaultPropertiesUpdate::getCpuActive, AssaultPropertiesUpdate::setCpuActive);
        assaultBinder.forField(cpuCronField).bind(AssaultPropertiesUpdate::getCpuCronExpression, AssaultPropertiesUpdate::setCpuCronExpression);
        assaultBinder.forField(cpuHoldField).bind(AssaultPropertiesUpdate::getCpuMillisecondsHoldLoad, AssaultPropertiesUpdate::setCpuMillisecondsHoldLoad);
        assaultBinder.forField(cpuLoadField).bind(AssaultPropertiesUpdate::getCpuLoadTargetFraction, AssaultPropertiesUpdate::setCpuLoadTargetFraction);
        
        cpuForm.add(cpuActiveCheck, 2);
        cpuForm.add(cpuCronField, 2);
        cpuForm.add(cpuCronExplanation, 2);
        cpuForm.add(cpuHoldField, cpuLoadField);
        
        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("CPU"), cpuForm);
        
        // Section services personnalisés
        VerticalLayout customServicesSection = new VerticalLayout();
        customServicesSection.setSpacing(false);
        customServicesSection.setPadding(false);
        
        // Liste des services actuels avec boutons de suppression
        VerticalLayout servicesList = new VerticalLayout();
        servicesList.setSpacing(false);
        servicesList.setPadding(false);
        
        // Référence à la liste des services dans le modèle
        List<String> watchedServices = new ArrayList<>();
        if (assaultModel.getWatchedCustomServices() != null) {
            watchedServices.addAll(assaultModel.getWatchedCustomServices());
        }

        // Initialiser la liste des services
        updateServicesList(servicesList, watchedServices);

        // Chargement des services disponibles
        List<String> availableServices = new ArrayList<>();
        try {
            availableServices.addAll(chaosMonkeyService.getAvailableServices());
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement des services disponibles", e);
            // Continuer sans les services disponibles
        }

        // MultiSelectComboBox pour sélectionner depuis les services disponibles
        MultiSelectComboBox<String> serviceSelect = new MultiSelectComboBox<>("Sélectionner des services");
        serviceSelect.setItems(availableServices);
        serviceSelect.setWidth("400px"); // Agrandir le composant
        serviceSelect.setHeight("250px"); // Définir une hauteur pour voir plusieurs éléments

        Button addServiceButton = new Button("Ajouter", e -> {
            Set<String> selectedServices = serviceSelect.getValue();
            if (selectedServices != null && !selectedServices.isEmpty()) {
                boolean added = false;
                for (String selectedService : selectedServices) {
                    if (!watchedServices.contains(selectedService)) {
                        watchedServices.add(selectedService);
                        added = true;
                    }
                }
                if (added) {
                    assaultModel.setWatchedCustomServices(new ArrayList<>(watchedServices));
                    serviceSelect.clear();
                    updateServicesList(servicesList, watchedServices);
                    Notification.show("Services ajoutés avec succès");
                }
            }
        });

        HorizontalLayout serviceSelectionLayout = new HorizontalLayout(serviceSelect, addServiceButton);
        serviceSelectionLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        customServicesSection.add(servicesList, serviceSelectionLayout);

        // Ajouter au layout principal avec un titre
        mainLayout.add(new H3("Services personnalisés"), customServicesSection);
        
        // Bouton de soumission
        final Button submitButton = new Button("Mettre à jour");
        submitButton.addClickListener(listener -> {
            try {
                assaultBinder.writeBean(assaultModel);
                chaosMonkeyService.updateAssault(assaultModel);
                Notification success = new Notification("Configuration des assaults mise à jour avec succès",
                        3000, Notification.Position.MIDDLE);
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                success.open();
            } catch (Exception ex) {
                Notification error = new Notification("Erreur lors de la mise à jour: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                error.open();
            }
        });
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        // Charger les données dans le binder
        assaultBinder.readBean(assaultModel);
        
        // Initialiser l'état des champs en fonction des cases à cocher
        latencyStartField.setEnabled(latencyActiveCheck.getValue());
        latencyEndField.setEnabled(latencyActiveCheck.getValue());
        exceptionTypeField.setEnabled(exceptionsActiveCheck.getValue());
        killAppCronField.setEnabled(killAppActiveCheck.getValue());
        killAppCronExplanation.setVisible(killAppActiveCheck.getValue());
        
        boolean memoryEnabled = memoryActiveCheck.getValue();
        memoryCronField.setEnabled(memoryEnabled);
        memoryCronExplanation.setVisible(memoryEnabled);
        memoryHoldField.setEnabled(memoryEnabled);
        memoryWaitField.setEnabled(memoryEnabled);
        memoryIncrementField.setEnabled(memoryEnabled);
        memoryTargetField.setEnabled(memoryEnabled);
        
        boolean cpuEnabled = cpuActiveCheck.getValue();
        cpuCronField.setEnabled(cpuEnabled);
        cpuCronExplanation.setVisible(cpuEnabled);
        cpuHoldField.setEnabled(cpuEnabled);
        cpuLoadField.setEnabled(cpuEnabled);

        mainLayout.add(submitButton);
        mainLayout.setAlignSelf(FlexComponent.Alignment.END, submitButton);
        
        // Ajouter un peu de marge entre les sections pour améliorer la lisibilité
        mainLayout.getChildren().forEach(component -> {
            if (component instanceof H3) {
                component.getElement().getStyle().set("margin-top", "1.5em");
            }
        });
        
        return mainLayout;
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

    /**
     * Analyse une expression cron et retourne une explication en français.
     */
    private String explainCronExpression(String cronExpression) {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return "Non définie";
        }
        
        try {
            String[] parts = cronExpression.trim().split("\\s+");
            if (parts.length < 6) {
                return "Expression incomplète";
            }
            
            // Analyse des parties standards d'une expression cron : secondes minutes heures jour_du_mois mois jour_de_semaine
            String seconds = parts[0];
            String minutes = parts[1];
            String hours = parts[2];
            String dayOfMonth = parts[3];
            String month = parts[4];
            String dayOfWeek = parts[5];
            
            // Construction d'une explication
            StringBuilder explanation = new StringBuilder();
            
            // Analyser la fréquence
            if ("0".equals(seconds) && "0/15".equals(minutes) && "*".equals(hours)) {
                explanation.append("Toutes les 15 minutes");
            } else if ("0".equals(seconds) && "0".equals(minutes) && "*/1".equals(hours)) {
                explanation.append("Toutes les heures (à l'heure pile)");
            } else if ("0".equals(seconds) && "0".equals(minutes) && "*".equals(hours)) {
                explanation.append("Chaque heure à minuit");
            } else if ("0".equals(seconds) && "0".equals(minutes) && "12".equals(hours)) {
                explanation.append("À midi tous les jours");
            } else if ("0".equals(seconds) && "0".equals(minutes) && "0".equals(hours)) {
                explanation.append("À minuit tous les jours");
            } else if ("0".equals(seconds) && "*/5".equals(minutes)) {
                explanation.append("Toutes les 5 minutes");
            } else if ("0".equals(seconds) && "*/10".equals(minutes)) {
                explanation.append("Toutes les 10 minutes");
            } else if ("0".equals(seconds) && "*/30".equals(minutes)) {
                explanation.append("Toutes les 30 minutes");
            } else {
                // Explication générale pour les cas non spécifiquement traités
                explanation.append("Secondes: ").append(seconds)
                          .append(", Minutes: ").append(minutes)
                          .append(", Heures: ").append(hours);
                
                if (!"*".equals(dayOfMonth) || !"*".equals(month) || !"?".equals(dayOfWeek)) {
                    explanation.append(", restreint par jour/mois");
                }
            }
            
            return explanation.toString();
        } catch (Exception e) {
            LOGGER.warn("Erreur lors de l'analyse de l'expression cron: {}", cronExpression, e);
            return "Expression non reconnue";
        }
    }
}

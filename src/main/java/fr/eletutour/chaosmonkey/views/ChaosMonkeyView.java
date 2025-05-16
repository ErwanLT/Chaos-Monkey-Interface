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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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
import fr.eletutour.chaosmonkey.consumer.model.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.consumer.model.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.consumer.model.WatcherProperties;
import fr.eletutour.chaosmonkey.consumer.model.WatcherPropertiesUpdate;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;

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
            WatcherProperties watcherProps = chaosMonkeyService.getWatcherProperties();
            this.watcherModel = new WatcherPropertiesUpdate()
                    .controller(watcherProps.getController())
                    .restController(watcherProps.getRestController())
                    .service(watcherProps.getService())
                    .repository(watcherProps.getRepository())
                    .component(watcherProps.getComponent())
                    .restTemplate(watcherProps.getRestTemplate())
                    .webClient(watcherProps.getWebClient())
                    .actuatorHealth(watcherProps.getActuatorHealth());
            
            if (watcherProps.getBeans() != null) {
                this.watcherModel.beans(new ArrayList<>(watcherProps.getBeans()));
            }
            
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
        
        // Section de contrôle
        VerticalLayout controlSection = new VerticalLayout();
        controlSection.add(new H2("Contrôle"));
        controlSection.add(createControlPanel());
        
        // Section de configuration des Watchers
        VerticalLayout watcherSection = new VerticalLayout();
        watcherSection.add(new H2("Configuration des Watchers"));
        watcherSection.add(createWatcherPanel());
        
        // Section de configuration des Assaults
        VerticalLayout assaultSection = new VerticalLayout();
        assaultSection.add(new H2("Configuration des Assaults"));
        assaultSection.add(createAssaultPanel());
        
        mainContent.add(controlSection, watcherSection, assaultSection);
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
        
        // Liaison des champs avec le binder
        watcherBinder.forField(controllerCheck).bind("controller");
        watcherBinder.forField(restControllerCheck).bind("restController");
        watcherBinder.forField(serviceCheck).bind("service");
        watcherBinder.forField(repositoryCheck).bind("repository");
        watcherBinder.forField(componentCheck).bind("component");
        watcherBinder.forField(restTemplateCheck).bind("restTemplate");
        watcherBinder.forField(webClientCheck).bind("webClient");
        watcherBinder.forField(actuatorHealthCheck).bind("actuatorHealth");
        
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
        submitButton.addClickListener(e -> {
            try {
                // Désactiver le bouton pendant le traitement
                submitButton.setEnabled(false);
                submitButton.setText("Mise à jour en cours...");
                
                // Le binder met déjà à jour le modèle, s'assurer que les beans sont à jour
                watcherModel.setBeans(new ArrayList<>(beans));
                
                // Utilisation de l'API asynchrone de Vaadin pour éviter le blocage de l'UI
                getUI().ifPresent(ui -> ui.access(() -> {
                    try {
                        String result = chaosMonkeyService.updateWatcher(watcherModel);
                        
                        // Afficher un message approprié en fonction de la réponse
                        if (result != null && result.startsWith("OK")) {
                            Notification.show("Configuration des watchers mise à jour avec succès", 
                                    3000, Notification.Position.MIDDLE);
                        } else {
                            Notification.show("Mise à jour effectuée: " + result, 
                                    3000, Notification.Position.MIDDLE);
                        }
                    } catch (Exception ex) {
                        Notification.show("Erreur lors de la mise à jour: " + ex.getMessage(), 
                                5000, Notification.Position.MIDDLE);
                    } finally {
                        // Réactiver le bouton quelle que soit l'issue
                        submitButton.setEnabled(true);
                        submitButton.setText("Mettre à jour");
                    }
                }));
            } catch (Exception ex) {
                // En cas d'erreur lors de la création de l'objet ou d'autres traitements
                Notification.show("Erreur lors de la préparation de la mise à jour: " + ex.getMessage(), 
                        5000, Notification.Position.MIDDLE);
                
                // Réactiver le bouton
                submitButton.setEnabled(true);
                submitButton.setText("Mettre à jour");
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
        // Conteneur principal
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        
        // Configuration générale
        FormLayout generalForm = new FormLayout();
        generalForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        IntegerField levelField = new IntegerField("Niveau");
        levelField.setMin(1);
        levelField.setMax(10000);
        
        Checkbox deterministicCheck = new Checkbox("Déterministe");
        
        // Liaison avec le binder
        assaultBinder.forField(levelField).bind("level");
        assaultBinder.forField(deterministicCheck).bind("deterministic");
        
        generalForm.add(levelField, deterministicCheck);
        
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
        assaultBinder.forField(latencyActiveCheck).bind("latencyActive");
        assaultBinder.forField(latencyStartField).bind("latencyRangeStart");
        assaultBinder.forField(latencyEndField).bind("latencyRangeEnd");
        
        latencyForm.add(latencyActiveCheck, 2);
        latencyForm.add(latencyStartField, latencyEndField);
        
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
        assaultBinder.forField(exceptionsActiveCheck).bind("exceptionsActive");
        // La liaison pour exception.type nécessite un mapping spécial qu'on traitera plus tard
        
        exceptionsForm.add(exceptionsActiveCheck, 2);
        exceptionsForm.add(exceptionTypeField, 2);
        
        mainLayout.add(new H3("Exceptions"), exceptionsForm);
        
        // Section Kill Application
        FormLayout killAppForm = new FormLayout();
        killAppForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox killAppActiveCheck = new Checkbox("Activer Kill Application");
        TextField killAppCronField = new TextField("Expression Cron");
        killAppCronField.setPlaceholder("0 0/15 * * * ?");
        
        // Désactiver le champ si la case n'est pas cochée
        killAppActiveCheck.addValueChangeListener(event -> {
            killAppCronField.setEnabled(event.getValue());
        });
        
        // Liaison avec le binder
        assaultBinder.forField(killAppActiveCheck).bind("killApplicationActive");
        assaultBinder.forField(killAppCronField).bind("killApplicationCronExpression");
        
        killAppForm.add(killAppActiveCheck, 2);
        killAppForm.add(killAppCronField, 2);
        
        mainLayout.add(new H3("Kill Application"), killAppForm);
        
        // Section Mémoire
        FormLayout memoryForm = new FormLayout();
        memoryForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox memoryActiveCheck = new Checkbox("Activer assault mémoire");
        TextField memoryCronField = new TextField("Expression Cron");
        memoryCronField.setPlaceholder("0 0/15 * * * ?");
        
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
            memoryHoldField.setEnabled(enabled);
            memoryWaitField.setEnabled(enabled);
            memoryIncrementField.setEnabled(enabled);
            memoryTargetField.setEnabled(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(memoryActiveCheck).bind("memoryActive");
        assaultBinder.forField(memoryCronField).bind("memoryCronExpression");
        assaultBinder.forField(memoryHoldField).bind("memoryMillisecondsHoldFilledMemory");
        assaultBinder.forField(memoryWaitField).bind("memoryMillisecondsWaitNextIncrease");
        assaultBinder.forField(memoryIncrementField).bind("memoryFillIncrementFraction");
        assaultBinder.forField(memoryTargetField).bind("memoryFillTargetFraction");
        
        memoryForm.add(memoryActiveCheck, 2);
        memoryForm.add(memoryCronField, 2);
        memoryForm.add(memoryHoldField, memoryWaitField);
        memoryForm.add(memoryIncrementField, memoryTargetField);
        
        mainLayout.add(new H3("Mémoire"), memoryForm);
        
        // Section CPU
        FormLayout cpuForm = new FormLayout();
        cpuForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));
        
        Checkbox cpuActiveCheck = new Checkbox("Activer assault CPU");
        TextField cpuCronField = new TextField("Expression Cron");
        cpuCronField.setPlaceholder("0 0/15 * * * ?");
        
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
            cpuHoldField.setEnabled(enabled);
            cpuLoadField.setEnabled(enabled);
        });
        
        // Liaison avec le binder
        assaultBinder.forField(cpuActiveCheck).bind("cpuActive");
        assaultBinder.forField(cpuCronField).bind("cpuCronExpression");
        assaultBinder.forField(cpuHoldField).bind("cpuMillisecondsHoldLoad");
        assaultBinder.forField(cpuLoadField).bind("cpuLoadTargetFraction");
        
        cpuForm.add(cpuActiveCheck, 2);
        cpuForm.add(cpuCronField, 2);
        cpuForm.add(cpuHoldField, cpuLoadField);
        
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
        
        // Remplacer le ComboBox par un MultiSelectComboBox pour sélectionner depuis les services disponibles
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
        
        customServicesSection.add(new H3("Services personnalisés"), servicesList, serviceSelectionLayout);
        mainLayout.add(customServicesSection);
        
        // Bouton de soumission
        final Button submitButton = new Button("Mettre à jour");
        submitButton.addClickListener(e -> {
            try {
                // Désactiver le bouton pendant le traitement
                submitButton.setEnabled(false);
                submitButton.setText("Mise à jour en cours...");
                
                // Le binder met déjà à jour le modèle, s'assurer que les services sont à jour
                assaultModel.setWatchedCustomServices(new ArrayList<>(watchedServices));
                
                // Utilisation de l'API asynchrone de Vaadin pour éviter le blocage de l'UI
                getUI().ifPresent(ui -> ui.access(() -> {
                    try {
                        String result = chaosMonkeyService.updateAssault(assaultModel);
                        
                        // Afficher un message approprié en fonction de la réponse
                        if (result != null && result.startsWith("OK")) {
                            Notification.show("Configuration des assaults mise à jour avec succès", 
                                    3000, Notification.Position.MIDDLE);
                        } else {
                            Notification.show("Mise à jour effectuée: " + result, 
                                    3000, Notification.Position.MIDDLE);
                        }
                    } catch (Exception ex) {
                        Notification.show("Erreur lors de la mise à jour: " + ex.getMessage(), 
                                5000, Notification.Position.MIDDLE);
                    } finally {
                        // Réactiver le bouton quelle que soit l'issue
                        submitButton.setEnabled(true);
                        submitButton.setText("Mettre à jour");
                    }
                }));
            } catch (Exception ex) {
                // En cas d'erreur lors de la création de l'objet ou d'autres traitements
                Notification.show("Erreur lors de la préparation de la mise à jour: " + ex.getMessage(), 
                        5000, Notification.Position.MIDDLE);
                
                // Réactiver le bouton
                submitButton.setEnabled(true);
                submitButton.setText("Mettre à jour");
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
        
        boolean memoryEnabled = memoryActiveCheck.getValue();
        memoryCronField.setEnabled(memoryEnabled);
        memoryHoldField.setEnabled(memoryEnabled);
        memoryWaitField.setEnabled(memoryEnabled);
        memoryIncrementField.setEnabled(memoryEnabled);
        memoryTargetField.setEnabled(memoryEnabled);
        
        boolean cpuEnabled = cpuActiveCheck.getValue();
        cpuCronField.setEnabled(cpuEnabled);
        cpuHoldField.setEnabled(cpuEnabled);
        cpuLoadField.setEnabled(cpuEnabled);

        mainLayout.add(submitButton);
        mainLayout.setAlignSelf(FlexComponent.Alignment.END, submitButton);
        
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
}

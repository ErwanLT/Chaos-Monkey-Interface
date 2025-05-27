package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
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
import com.vaadin.flow.data.binder.Validator;
import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import fr.eletutour.chaosmonkey.utils.CronExpressionExplainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssaultPanelComponent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssaultPanelComponent.class);
    private final ChaosMonkeyService chaosMonkeyService;
    private final Binder<AssaultPropertiesUpdate> binder;
    private final AssaultPropertiesUpdate model;

    public AssaultPanelComponent(ChaosMonkeyService chaosMonkeyService, Binder<AssaultPropertiesUpdate> binder, AssaultPropertiesUpdate model) {
        this.chaosMonkeyService = chaosMonkeyService;
        this.binder = binder;
        this.model = model;

        setSpacing(true);
        add(createGeneralSection(), createLatencySection(), createExceptionsSection(),
            createKillAppSection(), createMemorySection(), createCpuSection(), createCustomServicesSection(),
            createSubmitButton());
        binder.readBean(model);
        initializeFieldStates();
    }

    private Component createGeneralSection() {
        FormLayout generalForm = new FormLayout();
        generalForm.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2));

        IntegerField levelField = new IntegerField("Niveau");
        levelField.setMin(1);
        levelField.setMax(10000);
        Checkbox deterministicCheck = new Checkbox("Déterministe");

        binder.forField(levelField).bind(AssaultPropertiesUpdate::getLevel, AssaultPropertiesUpdate::setLevel);
        binder.forField(deterministicCheck).bind(AssaultPropertiesUpdate::getDeterministic, AssaultPropertiesUpdate::setDeterministic);

        generalForm.add(levelField, deterministicCheck);
        return new VerticalLayout(new H3("Configuration générale"), generalForm);
    }

    private Component createLatencySection() {
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

        latencyActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            latencyStartField.setEnabled(enabled);
            latencyEndField.setEnabled(enabled);
        });

        binder.forField(latencyActiveCheck).bind(AssaultPropertiesUpdate::getLatencyActive, AssaultPropertiesUpdate::setLatencyActive);
        binder.forField(latencyStartField).bind(AssaultPropertiesUpdate::getLatencyRangeStart, AssaultPropertiesUpdate::setLatencyRangeStart);
        binder.forField(latencyEndField).bind(AssaultPropertiesUpdate::getLatencyRangeEnd, AssaultPropertiesUpdate::setLatencyRangeEnd);

        latencyForm.add(latencyActiveCheck, 2);
        latencyForm.add(latencyStartField, latencyEndField);
        return new VerticalLayout(new H3("Latence"), latencyForm);
    }

    private Component createExceptionsSection() {
        FormLayout exceptionsForm = new FormLayout();
        exceptionsForm.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2));

        Checkbox exceptionsActiveCheck = new Checkbox("Activer exceptions");
        TextField exceptionTypeField = new TextField("Type d'exception");
        exceptionTypeField.setPlaceholder("java.lang.RuntimeException");

        exceptionsActiveCheck.addValueChangeListener(event -> exceptionTypeField.setEnabled(event.getValue()));
        binder.forField(exceptionsActiveCheck).bind(AssaultPropertiesUpdate::getExceptionsActive, AssaultPropertiesUpdate::setExceptionsActive);

        exceptionsForm.add(exceptionsActiveCheck, 2);
        exceptionsForm.add(exceptionTypeField, 2);
        return new VerticalLayout(new H3("Exceptions"), exceptionsForm);
    }

    private Component createKillAppSection() {
        FormLayout killAppForm = new FormLayout();
        killAppForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Checkbox killAppActiveCheck = new Checkbox("Activer Kill Application");
        CronExpressionEditor cronEditor = new CronExpressionEditor();
        NativeLabel killAppCronExplanation = new NativeLabel("Planification: Non définie");
        killAppCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        killAppCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");

        cronEditor.addValueChangeListener(event ->
                killAppCronExplanation.setText("Planification: " + CronExpressionExplainer.explainCronExpression(cronEditor.getValue())));

        killAppActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            cronEditor.setEnabled(enabled);
            killAppCronExplanation.setVisible(enabled);
        });

        binder.forField(killAppActiveCheck).bind(AssaultPropertiesUpdate::getKillApplicationActive, AssaultPropertiesUpdate::setKillApplicationActive);
        cronEditor.bind(binder,
                Validator.from(cron -> {
                    String explanation = CronExpressionExplainer.explainCronExpression(cron);
                    return !explanation.equals("Expression incomplète") && !explanation.equals("Expression non reconnue");
                }, "Expression Cron invalide"),
                AssaultPropertiesUpdate::getKillApplicationCronExpression,
                AssaultPropertiesUpdate::setKillApplicationCronExpression
        );

        killAppForm.add(killAppActiveCheck, 2);
        killAppForm.add(cronEditor, 2);
        killAppForm.add(killAppCronExplanation, 2);
        return new VerticalLayout(new H3("Kill Application"), killAppForm);
    }

    private Component createMemorySection() {
        FormLayout memoryForm = new FormLayout();
        memoryForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Checkbox memoryActiveCheck = new Checkbox("Activer assault mémoire");
        CronExpressionEditor cronEditor = new CronExpressionEditor();
        NativeLabel memoryCronExplanation = new NativeLabel("Planification: Non définie");
        memoryCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        memoryCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");

        cronEditor.addValueChangeListener(event ->
                memoryCronExplanation.setText("Planification: " + CronExpressionExplainer.explainCronExpression(cronEditor.getValue())));

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

        memoryActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            cronEditor.setEnabled(enabled);
            memoryCronExplanation.setVisible(enabled);
            memoryHoldField.setEnabled(enabled);
            memoryWaitField.setEnabled(enabled);
            memoryIncrementField.setEnabled(enabled);
            memoryTargetField.setEnabled(enabled);
        });

        binder.forField(memoryActiveCheck).bind(AssaultPropertiesUpdate::getMemoryActive, AssaultPropertiesUpdate::setMemoryActive);
        cronEditor.bind(binder,
                Validator.from(cron -> {
                    String explanation = CronExpressionExplainer.explainCronExpression(cron);
                    return !explanation.equals("Expression incomplète") && !explanation.equals("Expression non reconnue");
                }, "Expression Cron invalide"),
                AssaultPropertiesUpdate::getMemoryCronExpression,
                AssaultPropertiesUpdate::setMemoryCronExpression
        );
        binder.forField(memoryHoldField).bind(AssaultPropertiesUpdate::getMemoryMillisecondsHoldFilledMemory, AssaultPropertiesUpdate::setMemoryMillisecondsHoldFilledMemory);
        binder.forField(memoryWaitField).bind(AssaultPropertiesUpdate::getMemoryMillisecondsWaitNextIncrease, AssaultPropertiesUpdate::setMemoryMillisecondsWaitNextIncrease);
        binder.forField(memoryIncrementField).bind(AssaultPropertiesUpdate::getMemoryFillIncrementFraction, AssaultPropertiesUpdate::setMemoryFillIncrementFraction);
        binder.forField(memoryTargetField).bind(AssaultPropertiesUpdate::getMemoryFillTargetFraction, AssaultPropertiesUpdate::setMemoryFillTargetFraction);

        memoryForm.add(memoryActiveCheck, 2);
        memoryForm.add(cronEditor, 2);
        memoryForm.add(memoryCronExplanation, 2);
        memoryForm.add(memoryHoldField, memoryWaitField);
        memoryForm.add(memoryIncrementField, memoryTargetField);
        return new VerticalLayout(new H3("Mémoire"), memoryForm);
    }

    private Component createCpuSection() {
        FormLayout cpuForm = new FormLayout();
        cpuForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        Checkbox cpuActiveCheck = new Checkbox("Activer assault CPU");
        CronExpressionEditor cronEditor = new CronExpressionEditor();
        NativeLabel cpuCronExplanation = new NativeLabel("Planification: Non définie");
        cpuCronExplanation.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        cpuCronExplanation.getStyle().set("color", "var(--lumo-secondary-text-color)");

        cronEditor.addValueChangeListener(event ->
                cpuCronExplanation.setText("Planification: " + CronExpressionExplainer.explainCronExpression(cronEditor.getValue())));

        IntegerField cpuHoldField = new IntegerField("Durée de charge (ms)");
        cpuHoldField.setMin(1500);
        cpuHoldField.setMax(Integer.MAX_VALUE);
        NumberField cpuLoadField = new NumberField("Fraction de charge");
        cpuLoadField.setMin(0.1);
        cpuLoadField.setMax(1.0);
        cpuLoadField.setStep(0.1);

        cpuActiveCheck.addValueChangeListener(event -> {
            boolean enabled = event.getValue();
            cronEditor.setEnabled(enabled);
            cpuCronExplanation.setVisible(enabled);
            cpuHoldField.setEnabled(enabled);
            cpuLoadField.setEnabled(enabled);
        });

        binder.forField(cpuActiveCheck).bind(AssaultPropertiesUpdate::getCpuActive, AssaultPropertiesUpdate::setCpuActive);
        cronEditor.bind(binder,
                Validator.from(cron -> {
                    String explanation = CronExpressionExplainer.explainCronExpression(cron);
                    return !explanation.equals("Expression incomplète") && !explanation.equals("Expression non reconnue");
                }, "Expression Cron invalide"),
                AssaultPropertiesUpdate::getCpuCronExpression,
                AssaultPropertiesUpdate::setCpuCronExpression
        );
        binder.forField(cpuHoldField).bind(AssaultPropertiesUpdate::getCpuMillisecondsHoldLoad, AssaultPropertiesUpdate::setCpuMillisecondsHoldLoad);
        binder.forField(cpuLoadField).bind(AssaultPropertiesUpdate::getCpuLoadTargetFraction, AssaultPropertiesUpdate::setCpuLoadTargetFraction);

        cpuForm.add(cpuActiveCheck, 2);
        cpuForm.add(cronEditor, 2);
        cpuForm.add(cpuCronExplanation, 2);
        cpuForm.add(cpuHoldField, cpuLoadField);
        return new VerticalLayout(new H3("CPU"), cpuForm);
    }

    private Component createCustomServicesSection() {
        VerticalLayout customServicesSection = new VerticalLayout();
        customServicesSection.setSpacing(false);
        customServicesSection.setPadding(false);
        customServicesSection.add(new H3("Services personnalisés"));

        VerticalLayout servicesList = new VerticalLayout();
        servicesList.setSpacing(false);
        servicesList.setPadding(false);

        List<String> watchedServices = model.getWatchedCustomServices() != null ? new ArrayList<>(model.getWatchedCustomServices()) : new ArrayList<>();
        updateServicesList(servicesList, watchedServices);

        List<String> availableServices = new ArrayList<>();
        try {
            availableServices.addAll(chaosMonkeyService.getAvailableServices());
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement des services disponibles", e);
        }

        MultiSelectComboBox<String> serviceSelect = new MultiSelectComboBox<>("Sélectionner des services");
        serviceSelect.setItems(availableServices);
        serviceSelect.setWidth("400px");
        serviceSelect.setHeight("250px");

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
                    model.setWatchedCustomServices(new ArrayList<>(watchedServices));
                    serviceSelect.clear();
                    updateServicesList(servicesList, watchedServices);
                    Notification.show("Services ajoutés avec succès");
                }
            }
        });

        HorizontalLayout serviceSelectionLayout = new HorizontalLayout(serviceSelect, addServiceButton);
        serviceSelectionLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        customServicesSection.add(servicesList, serviceSelectionLayout);
        return customServicesSection;
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
                    model.setWatchedCustomServices(new ArrayList<>(watchedServices));
                    updateServicesList(servicesList, watchedServices);
                });
                deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                serviceItem.add(serviceName, deleteButton);
                servicesList.add(serviceItem);
            });
        }
    }

    private Button createSubmitButton() {
        Button submitButton = new Button("Mettre à jour");
        submitButton.addClickListener(listener -> {
            try {
                binder.writeBean(model);
                chaosMonkeyService.updateAssault(model);
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
        return submitButton;
    }

    private void initializeFieldStates() {
        getChildren().filter(component -> component instanceof VerticalLayout).forEach(section -> {
            // Vérifier si la section est "Configuration générale" en examinant le titre H3
            boolean isGeneralSection = section.getChildren()
                    .filter(c -> c instanceof H3)
                    .map(c -> ((H3) c).getText())
                    .anyMatch(text -> text.equals("Configuration générale"));

            if (!isGeneralSection) {
                FormLayout form = (FormLayout) section.getChildren()
                        .filter(c -> c instanceof FormLayout).findFirst().orElse(null);
                if (form != null) {
                    Checkbox activeCheck = (Checkbox) form.getChildren()
                            .filter(c -> c instanceof Checkbox).findFirst().orElse(null);
                    if (activeCheck != null) {
                        boolean enabled = activeCheck.getValue();
                        form.getChildren()
                                .filter(c -> !(c instanceof Checkbox) && !(c instanceof NativeLabel))
                                .forEach(c -> c.getElement().setEnabled(enabled));
                        form.getChildren()
                                .filter(c -> c instanceof NativeLabel)
                                .forEach(c -> c.getElement().setVisible(enabled));
                    }
                }
            }
        });
    }
}
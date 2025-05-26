package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import fr.eletutour.chaosmonkey.models.WatcherPropertiesUpdate;
import fr.eletutour.chaosmonkey.service.ChaosMonkeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WatcherPanelComponent extends VerticalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(WatcherPanelComponent.class);
    private final ChaosMonkeyService chaosMonkeyService;
    private final Binder<WatcherPropertiesUpdate> binder;
    private final WatcherPropertiesUpdate model;

    public WatcherPanelComponent(ChaosMonkeyService chaosMonkeyService, Binder<WatcherPropertiesUpdate> binder, WatcherPropertiesUpdate model) {
        this.chaosMonkeyService = chaosMonkeyService;
        this.binder = binder;
        this.model = model;

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2),
            new FormLayout.ResponsiveStep("1000px", 3));

        Checkbox controllerCheck = new Checkbox("Controller");
        Checkbox restControllerCheck = new Checkbox("Rest Controller");
        Checkbox serviceCheck = new Checkbox("Service");
        Checkbox repositoryCheck = new Checkbox("Repository");
        Checkbox componentCheck = new Checkbox("Component");
        Checkbox restTemplateCheck = new Checkbox("Rest Template");
        Checkbox webClientCheck = new Checkbox("Web Client");
        Checkbox actuatorHealthCheck = new Checkbox("Actuator Health");

        binder.forField(controllerCheck).bind(WatcherPropertiesUpdate::getController, WatcherPropertiesUpdate::setController);
        binder.forField(restControllerCheck).bind(WatcherPropertiesUpdate::getRestController, WatcherPropertiesUpdate::setRestController);
        binder.forField(serviceCheck).bind(WatcherPropertiesUpdate::getService, WatcherPropertiesUpdate::setService);
        binder.forField(repositoryCheck).bind(WatcherPropertiesUpdate::getRepository, WatcherPropertiesUpdate::setRepository);
        binder.forField(componentCheck).bind(WatcherPropertiesUpdate::getComponent, WatcherPropertiesUpdate::setComponent);
        binder.forField(restTemplateCheck).bind(WatcherPropertiesUpdate::getRestTemplate, WatcherPropertiesUpdate::setRestTemplate);
        binder.forField(webClientCheck).bind(WatcherPropertiesUpdate::getWebClient, WatcherPropertiesUpdate::setWebClient);
        binder.forField(actuatorHealthCheck).bind(WatcherPropertiesUpdate::getActuatorHealth, WatcherPropertiesUpdate::setActuatorHealth);

        VerticalLayout beansSection = createBeansSection();
        Button submitButton = createSubmitButton();

        form.add(controllerCheck, restControllerCheck, serviceCheck);
        form.add(repositoryCheck, componentCheck, restTemplateCheck);
        form.add(webClientCheck, actuatorHealthCheck);
        form.add(beansSection, 3);

        binder.readBean(model);
        add(form, submitButton);
        setAlignSelf(FlexComponent.Alignment.END, submitButton);
    }

    private VerticalLayout createBeansSection() {
        VerticalLayout beansSection = new VerticalLayout();
        beansSection.setSpacing(false);
        beansSection.setPadding(false);
        beansSection.add(new H3("Beans personnalisés"));

        VerticalLayout beansList = new VerticalLayout();
        beansList.setSpacing(false);
        beansList.setPadding(false);

        List<String> beans = model.getBeans() != null ? new ArrayList<>(model.getBeans()) : new ArrayList<>();
        updateBeansList(beansList, beans);

        List<String> availableBeans = new ArrayList<>();
        try {
            availableBeans.addAll(chaosMonkeyService.getAvailableBeans());
        } catch (Exception e) {
            LOGGER.error("Erreur lors du chargement des beans disponibles", e);
        }

        MultiSelectComboBox<String> beanSelect = new MultiSelectComboBox<>("Sélectionner des beans");
        beanSelect.setItems(availableBeans);
        beanSelect.setWidth("400px");
        beanSelect.setHeight("250px");

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
                    model.setBeans(new ArrayList<>(beans));
                    beanSelect.clear();
                    updateBeansList(beansList, beans);
                    Notification.show("Beans ajoutés avec succès");
                }
            }
        });

        HorizontalLayout beanSelectionLayout = new HorizontalLayout(beanSelect, addBeanButton);
        beanSelectionLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        beansSection.add(beansList, beanSelectionLayout);
        return beansSection;
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
                    model.setBeans(new ArrayList<>(beans));
                    updateBeansList(beansList, beans);
                });
                deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                beanItem.add(beanName, deleteButton);
                beansList.add(beanItem);
            });
        }
    }

    private Button createSubmitButton() {
        Button submitButton = new Button("Mettre à jour");
        submitButton.addClickListener(listener -> {
            try {
                binder.writeBean(model);
                chaosMonkeyService.updateWatcher(model);
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
        return submitButton;
    }
}
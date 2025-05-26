package fr.eletutour.chaosmonkey.models;

import jakarta.validation.Valid;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * WatcherProperties
 */
public class WatcherProperties {

    private Boolean controller;

    private Boolean restController;

    private Boolean service;

    private Boolean repository;

    private Boolean component;

    private Boolean restTemplate;

    private Boolean webClient;

    private Boolean actuatorHealth;

    private List<String> beans = new ArrayList<>();

    public Boolean getController() {
        return controller;
    }

    public void setController(Boolean controller) {
        this.controller = controller;
    }

    @Nullable
    public Boolean getRestController() {
        return restController;
    }

    public void setRestController(Boolean restController) {
        this.restController = restController;
    }

    @Nullable
    public Boolean getService() {
        return service;
    }

    public void setService(Boolean service) {
        this.service = service;
    }

    @Nullable
    public Boolean getRepository() {
        return repository;
    }

    public void setRepository(Boolean repository) {
        this.repository = repository;
    }

    @Nullable
    public Boolean getComponent() {
        return component;
    }

    public void setComponent(Boolean component) {
        this.component = component;
    }

    @Nullable
    public Boolean getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(Boolean restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Nullable
    public Boolean getWebClient() {
        return webClient;
    }

    public void setWebClient(Boolean webClient) {
        this.webClient = webClient;
    }

    @Nullable
    public Boolean getActuatorHealth() {
        return actuatorHealth;
    }

    public void setActuatorHealth(Boolean actuatorHealth) {
        this.actuatorHealth = actuatorHealth;
    }

    public List<String> getBeans() {
        return beans;
    }

    public void setBeans(List<String> beans) {
        this.beans = beans;
    }

    @Override
    public String toString() {
        return "WatcherProperties{" +
                "controller=" + controller +
                ", restController=" + restController +
                ", service=" + service +
                ", repository=" + repository +
                ", component=" + component +
                ", restTemplate=" + restTemplate +
                ", webClient=" + webClient +
                ", actuatorHealth=" + actuatorHealth +
                ", beans=" + beans +
                '}';
    }
}


package fr.eletutour.chaosmonkey.models;

/**
 * ChaosMonkeySettingsDto
 */
public class ChaosMonkeySettingsDto {

    private ChaosMonkeyProperties chaosMonkeyProperties;

    private AssaultPropertiesUpdate assaultProperties;

    private WatcherProperties watcherProperties;

    public ChaosMonkeySettingsDto() {
    }

    public ChaosMonkeySettingsDto(ChaosMonkeyProperties chaosMonkeyProperties, AssaultPropertiesUpdate assaultProperties, WatcherProperties watcherProperties) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
        this.assaultProperties = assaultProperties;
        this.watcherProperties = watcherProperties;
    }

    public ChaosMonkeyProperties getChaosMonkeyProperties() {
        return chaosMonkeyProperties;
    }

    public void setChaosMonkeyProperties(ChaosMonkeyProperties chaosMonkeyProperties) {
        this.chaosMonkeyProperties = chaosMonkeyProperties;
    }

    public AssaultPropertiesUpdate getAssaultProperties() {
        return assaultProperties;
    }

    public void setAssaultProperties(AssaultPropertiesUpdate assaultProperties) {
        this.assaultProperties = assaultProperties;
    }

    public WatcherProperties getWatcherProperties() {
        return watcherProperties;
    }

    public void setWatcherProperties(WatcherProperties watcherProperties) {
        this.watcherProperties = watcherProperties;
    }

    @Override
    public String toString() {
        return "ChaosMonkeySettingsDto{" +
                "chaosMonkeyProperties=" + chaosMonkeyProperties +
                ", assaultProperties=" + assaultProperties +
                ", watcherProperties=" + watcherProperties +
                '}';
    }
}


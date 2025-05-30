package fr.eletutour.chaosmonkey.models;

/**
 * ValueDuration
 */
public class ValueDuration {

    private String raw;

    private String formatted;

    public ValueDuration() {
    }

    public ValueDuration(String raw, String formatted) {
        this.raw = raw;
        this.formatted = formatted;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    @Override
    public String toString() {
        return "ValueDuration{" +
                "raw='" + raw + '\'' +
                ", formatted='" + formatted + '\'' +
                '}';
    }
}
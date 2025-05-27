package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.ValueProvider;
import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;

public class CronExpressionEditor extends CustomField<String> {
    private final TextField secondsField = new TextField("Secondes");
    private final TextField minutesField = new TextField("Minutes");
    private final TextField hoursField = new TextField("Heures");
    private final TextField dayOfMonthField = new TextField("Jour du mois");
    private final TextField monthField = new TextField("Mois");
    private final TextField dayOfWeekField = new TextField("Jour de la semaine");
    private final FormLayout formLayout;

    public CronExpressionEditor() {
        formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 3)
        );

        secondsField.setPlaceholder("0");
        minutesField.setPlaceholder("0/15");
        hoursField.setPlaceholder("*");
        dayOfMonthField.setPlaceholder("*");
        monthField.setPlaceholder("*");
        dayOfWeekField.setPlaceholder("?");

        secondsField.addValueChangeListener(e -> updateValue());
        minutesField.addValueChangeListener(e -> updateValue());
        hoursField.addValueChangeListener(e -> updateValue());
        dayOfMonthField.addValueChangeListener(e -> updateValue());
        monthField.addValueChangeListener(e -> updateValue());
        dayOfWeekField.addValueChangeListener(e -> updateValue());

        formLayout.add(secondsField, minutesField, hoursField, dayOfMonthField, monthField, dayOfWeekField);
        add(formLayout);
    }

    @Override
    protected String generateModelValue() {
        return String.join(" ",
                secondsField.getValue().isEmpty() ? "0" : secondsField.getValue(),
                minutesField.getValue().isEmpty() ? "*" : minutesField.getValue(),
                hoursField.getValue().isEmpty() ? "*" : hoursField.getValue(),
                dayOfMonthField.getValue().isEmpty() ? "*" : dayOfMonthField.getValue(),
                monthField.getValue().isEmpty() ? "*" : monthField.getValue(),
                dayOfWeekField.getValue().isEmpty() ? "?" : dayOfWeekField.getValue()
        );
    }

    @Override
    protected void setPresentationValue(String cronExpression) {
        if (cronExpression != null && !cronExpression.isEmpty()) {
            String[] parts = cronExpression.split(" ");
            if (parts.length == 6) {
                secondsField.setValue(parts[0]);
                minutesField.setValue(parts[1]);
                hoursField.setValue(parts[2]);
                dayOfMonthField.setValue(parts[3]);
                monthField.setValue(parts[4]);
                dayOfWeekField.setValue(parts[5]);
            } else {
                resetFields();
            }
        } else {
            resetFields();
        }
    }

    private void resetFields() {
        secondsField.setValue("0");
        minutesField.setValue("0/15");
        hoursField.setValue("*");
        dayOfMonthField.setValue("*");
        monthField.setValue("*");
        dayOfWeekField.setValue("?");
    }

    public void bind(Binder<AssaultPropertiesUpdate> binder, Validator<? super String> validator,
                     ValueProvider<AssaultPropertiesUpdate, String> getter, Setter<AssaultPropertiesUpdate, String> setter) {
        binder.forField(this)
                .withValidator(validator)
                .bind(getter, setter);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        secondsField.setEnabled(enabled);
        minutesField.setEnabled(enabled);
        hoursField.setEnabled(enabled);
        dayOfMonthField.setEnabled(enabled);
        monthField.setEnabled(enabled);
        dayOfWeekField.setEnabled(enabled);
        formLayout.setEnabled(enabled);
    }
}
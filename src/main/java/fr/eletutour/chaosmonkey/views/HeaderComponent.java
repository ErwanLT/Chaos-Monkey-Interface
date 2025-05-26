package fr.eletutour.chaosmonkey.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderComponent extends HorizontalLayout {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderComponent.class);
    private final StatusManager statusManager;

    public HeaderComponent(StatusManager statusManager) {
        this.statusManager = statusManager;
        H1 brand = new H1("Chaos Monkey Dashboard");
        brand.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        setWidthFull();
        addClassNames(
            LumoUtility.Padding.Vertical.SMALL,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Background.CONTRAST_5
        );

        add(brand, statusManager.createStatusBadge());
    }
}
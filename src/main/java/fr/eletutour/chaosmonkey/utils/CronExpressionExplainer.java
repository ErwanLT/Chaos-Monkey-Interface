package fr.eletutour.chaosmonkey.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronExpressionExplainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CronExpressionExplainer.class);

    public static String explainCronExpression(String cronExpression) {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return "Non définie";
        }

        try {
            String[] parts = cronExpression.trim().split("\\s+");
            if (parts.length < 6) {
                return "Expression incomplète";
            }

            String seconds = parts[0];
            String minutes = parts[1];
            String hours = parts[2];
            String dayOfMonth = parts[3];
            String month = parts[4];
            String dayOfWeek = parts[5];

            StringBuilder explanation = new StringBuilder();

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
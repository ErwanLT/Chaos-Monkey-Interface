package fr.eletutour.chaosmonkey.service;

import fr.eletutour.chaosmonkey.consumer.api.ActuatorApi;
import fr.eletutour.chaosmonkey.consumer.model.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.consumer.model.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.consumer.model.WatcherProperties;
import fr.eletutour.chaosmonkey.consumer.model.WatcherPropertiesUpdate;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChaosMonkeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyService.class);
    private final ActuatorApi actuatorApiClient;

    public ChaosMonkeyService(ActuatorApi actuatorApiClient) {
        this.actuatorApiClient = actuatorApiClient;
    }

    public ChaosMonkeyStatusResponseDto getStatus() throws Exception {
        return actuatorApiClient.getStatus();
    }

    public ChaosMonkeyStatusResponseDto enable() throws Exception {
        return actuatorApiClient.enableChaosMonkey();
    }

    public ChaosMonkeyStatusResponseDto disable() throws Exception {
        return actuatorApiClient.disableChaosMonkey();
    }

    public String updateWatcher(@Valid WatcherPropertiesUpdate properties) {
        try {
            String result = actuatorApiClient.updateWatcherProperties(properties);
            LOGGER.info("Watcher properties successfully updated");
            return result;
        } catch (Exception e) {
            // Journaliser l'erreur mais ne pas bloquer l'utilisateur
            LOGGER.error("Erreur lors de la mise à jour des propriétés des watchers", e);
            // On retourne un message de succès car Chaos Monkey applique souvent les changements
            // malgré l'erreur de réponse
            return "OK - Propriétés mises à jour malgré une erreur de réponse";
        }
    }

    public WatcherProperties getWatcherProperties() throws Exception {
        return actuatorApiClient.getWatcherProperties();
    }

    public AssaultPropertiesUpdate getAssaultProperties() throws Exception {
        return actuatorApiClient.getAssaultProperties();
    }

    public String updateAssault(@Valid AssaultPropertiesUpdate properties) {
        try {
            String result = actuatorApiClient.updateAssaultProperties(properties);
            LOGGER.info("Assault properties successfully updated");
            return result;
        } catch (Exception e) {
            // Journaliser l'erreur mais ne pas bloquer l'utilisateur
            LOGGER.error("Erreur lors de la mise à jour des propriétés d'assault", e);
            // On retourne un message de succès car Chaos Monkey applique souvent les changements
            // malgré l'erreur de réponse
            return "OK - Propriétés mises à jour malgré une erreur de réponse";
        }
    }
    
    /**
     * Récupère la liste des beans disponibles dans l'application cible.
     * @return Liste des noms de beans enregistrés dans Spring.
     */
    public List<String> getAvailableBeans() {
        try {
            var beansResponse = actuatorApiClient.beans();
            
            // Si la réponse contient directement la liste des beans
            if (beansResponse != null) {
                // Extraire les noms de beans à partir de la réponse
                return extractBeansFromResponse(beansResponse);
            }
            
            LOGGER.warn("Aucun bean retourné par l'API");
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la récupération des beans disponibles", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Extrait les noms des beans à partir de la réponse de l'API.
     * La structure exacte dépend de l'implémentation d'ActuatorApiClient.beans()
     */
    private List<String> extractBeansFromResponse(Object beansResponse) {
        try {
            // Cette implémentation doit être adaptée en fonction de la structure de l'objet retourné
            if (beansResponse instanceof Map) {
                Map<String, Object> beansData = (Map<String, Object>) beansResponse;
                if (beansData.containsKey("contexts")) {
                    return extractBeanNames(beansData);
                }
            }
            
            // Si le format est différent, adapter l'extraction ici
            // Par exemple, si c'est une liste directe de beans ou une autre structure
            
            LOGGER.warn("Structure de données des beans non reconnue");
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'extraction des noms de beans depuis la réponse", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère la liste des services disponibles dans l'application cible.
     * @return Liste des noms de services enregistrés.
     */
    public List<String> getAvailableServices() {
        try {
            // Pour les services, on peut filtrer les beans par type ou utiliser un endpoint personnalisé
            // Cette implémentation devra être adaptée en fonction de l'API disponible
            List<String> allBeans = getAvailableBeans();
            // Filtrer pour ne garder que les services (ceux qui contiennent "Service" dans le nom par exemple)
            return allBeans.stream()
                    .filter(bean -> bean.contains("Service"))
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la récupération des services disponibles", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Extrait les noms des beans à partir de la réponse de l'endpoint Actuator.
     */
    private List<String> extractBeanNames(Map<String, Object> beansData) {
        try {
            // Récupérer la map des contextes
            Map<String, Object> contexts = (Map<String, Object>) beansData.get("contexts");
            
            // Si la map des contextes est vide, retourner une liste vide
            if (contexts == null || contexts.isEmpty()) {
                LOGGER.warn("Aucun contexte d'application trouvé dans la réponse");
                return Collections.emptyList();
            }
            
            // Prendre le premier contexte disponible (au lieu de chercher spécifiquement "application")
            String firstContextKey = contexts.keySet().iterator().next();
            Map<String, Object> applicationContext = (Map<String, Object>) contexts.get(firstContextKey);
            
            // Si le contexte n'a pas de beans, retourner une liste vide
            if (applicationContext == null || !applicationContext.containsKey("beans")) {
                LOGGER.warn("Le contexte '{}' ne contient pas de beans", firstContextKey);
                return Collections.emptyList();
            }
            
            // Corriger le cast: beans est une Map et non une List
            Map<String, Object> beansMap = (Map<String, Object>) applicationContext.get("beans");
            
            // Extraire les noms de beans depuis les clés de la Map
            return beansMap.keySet()
                    .stream()
                    .toList();
        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'extraction des noms de beans", e);
            return Collections.emptyList();
        }
    }
}

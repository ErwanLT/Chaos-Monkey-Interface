package fr.eletutour.chaosmonkey.service;

import fr.eletutour.chaosmonkey.models.AssaultPropertiesUpdate;
import fr.eletutour.chaosmonkey.models.ChaosMonkeyStatusResponseDto;
import fr.eletutour.chaosmonkey.models.WatcherProperties;
import fr.eletutour.chaosmonkey.models.WatcherPropertiesUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChaosMonkeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChaosMonkeyService.class);
    private final WebClient webClient;

    public ChaosMonkeyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ChaosMonkeyStatusResponseDto getStatus() throws Exception {
        LOGGER.info("Récupération du statut de Chaos Monkey");
        return webClient
                .get()
                .uri("/actuator/chaosmonkey/status")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(ChaosMonkeyStatusResponseDto.class)
                .block();
    }

    public ChaosMonkeyStatusResponseDto enable() throws Exception {
        LOGGER.info("Activation de Chaos Monkey");
        return webClient
                .post()
                .uri("/actuator/chaosmonkey/enable")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(ChaosMonkeyStatusResponseDto.class)
                .block();
    }

    public ChaosMonkeyStatusResponseDto disable() throws Exception {
        LOGGER.info("Désactivation de Chaos Monkey");
        return webClient
                .post()
                .uri("/actuator/chaosmonkey/disable")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(ChaosMonkeyStatusResponseDto.class)
                .block();
    }

    public String updateWatcher(WatcherPropertiesUpdate properties) {
        LOGGER.info("Mise à jour des propriétés du watcher de Chaos Monkey");
        return webClient
                .post()
                .uri("/actuator/chaosmonkey/watchers")
                .bodyValue(properties)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(String.class)
                .block();
    }

    public WatcherProperties getWatcherProperties() throws Exception {
        LOGGER.info("Récupération des propriétés du watcher de Chaos Monkey");
        var watcherProperties = webClient
                .get()
                .uri("/actuator/chaosmonkey/watchers")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(WatcherProperties.class)
                .block();
        LOGGER.info("Propriétés du watcher récupérées : {}", watcherProperties);
        return watcherProperties;
    }

    public AssaultPropertiesUpdate getAssaultProperties() throws Exception {
        LOGGER.info("Récupération des propriétés des assaults de Chaos Monkey");
        var assaultProperties = webClient
                .get()
                .uri("/actuator/chaosmonkey/assaults")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(AssaultPropertiesUpdate.class)
                .block();
        LOGGER.info("Propriétés des assaults récupérées : {}", assaultProperties);
        return assaultProperties;
    }

    public String updateAssault(AssaultPropertiesUpdate properties) {
        LOGGER.info("Mise à jour des propriétés des assaults de Chaos Monkey");
        return webClient
                .post()
                .uri("/actuator/chaosmonkey/assaults")
                .bodyValue(properties)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                .bodyToMono(String.class)
                .block();
    }
    
    /**
     * Récupère la liste des beans disponibles dans l'application cible.
     * @return Liste des noms de beans enregistrés dans Spring.
     */
    public List<String> getAvailableBeans() {
        try {
            var beansResponse = webClient.get()
                    .uri("/actuator/beans")
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Erreur côté client")))
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Erreur côté serveur")))
                    .bodyToMono(Object.class) // Utiliser Object pour une flexibilité maximale
                    .block();
            
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

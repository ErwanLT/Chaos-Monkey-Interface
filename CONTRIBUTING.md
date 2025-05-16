# Guide de contribution

Merci de votre intérêt pour contribuer à l'interface d'administration Chaos Monkey ! Ce document vous guidera à travers le processus de contribution.

## Table des matières

- [Environnement de développement](#environnement-de-développement)
- [Structure du projet](#structure-du-projet)
- [Soumettre une contribution](#soumettre-une-contribution)
- [Standards de code](#standards-de-code)
- [Tests](#tests)
- [Fonctionnalités souhaitées](#fonctionnalités-souhaitées)

## Environnement de développement

### Prérequis

- Java 21 ou supérieur
- Maven 3.8+
- IDE compatible avec Spring Boot et Vaadin (IntelliJ IDEA, Eclipse, VS Code)

### Installation locale

1. Cloner le dépôt
```bash
git clone https://github.com/votre-utilisateur/interface-chaos-monkey.git
cd interface-chaos-monkey
```

2. Importer le projet dans votre IDE

3. Installer les dépendances
```bash
mvn clean install
```

4. Lancer l'application en mode développement
```bash
mvn spring-boot:run
```

### Mode développement Vaadin

Pour le développement de l'interface utilisateur, Vaadin dispose d'un mode de développement avec rechargement à chaud :

```bash
mvn vaadin:prepare-frontend vaadin:build-frontend spring-boot:run
```

## Structure du projet

- `src/main/java/fr/eletutour/chaosmonkey/consumer` - Clients API générés par OpenAPI
- `src/main/java/fr/eletutour/chaosmonkey/service` - Services métier
- `src/main/java/fr/eletutour/chaosmonkey/views` - Composants d'interface utilisateur Vaadin
- `src/main/resources` - Fichiers de configuration et ressources statiques
- `src/main/resources/swagger` - Spécifications OpenAPI

## Soumettre une contribution

### Processus de contribution

1. Créez une issue décrivant le problème ou la fonctionnalité que vous souhaitez aborder
2. Forkez le dépôt
3. Créez une branche pour votre contribution (`git checkout -b feature/ma-nouvelle-fonctionnalite`)
4. Committez vos changements avec des messages clairs (`git commit -m 'Ajout de la fonctionnalité X'`)
5. Poussez vos changements vers votre fork (`git push origin feature/ma-nouvelle-fonctionnalite`)
6. Soumettez une Pull Request vers la branche principale

### Bonnes pratiques pour les Pull Requests

- Limitez chaque Pull Request à une seule fonctionnalité ou correction
- Incluez une description détaillée de vos modifications
- Référencez l'issue correspondante dans la description
- Assurez-vous que le code compile et que les tests passent

## Standards de code

### Style de code

- Suivez les conventions de codage Java standard
- Utilisez des noms explicites pour les variables, méthodes et classes
- Commentez votre code lorsque nécessaire pour expliquer des parties complexes
- Respectez l'architecture existante du projet

### Gestion des erreurs

- Utilisez le système de journalisation pour les messages d'erreur
- Appliquez une gestion des exceptions appropriée
- Évitez les blocs catch vides

## Tests

### Tests unitaires

Pour exécuter les tests unitaires :

```bash
mvn test
```

### Tests manuels

Lors du test manuel de nouvelles fonctionnalités :

1. Vérifiez le comportement sur différentes configurations de Chaos Monkey
2. Testez la réactivité de l'interface utilisateur
3. Vérifiez que les appels API fonctionnent correctement

## Fonctionnalités souhaitées

Voici quelques idées de contributions que nous aimerions voir :

- Amélioration de l'interface utilisateur avec des thèmes personnalisés
- Support pour la visualisation des métriques d'exécution des assaults
- Graphiques de visualisation des impacts des assaults
- Support multi-langues pour l'interface
- Amélioration de la documentation utilisateur
- Support pour de nouveaux types d'assaults

## Questions et assistance

Si vous avez des questions ou besoin d'aide pour contribuer, n'hésitez pas à :

- Ouvrir une issue pour poser des questions techniques
- Contacter les mainteneurs du projet

Merci pour votre contribution !

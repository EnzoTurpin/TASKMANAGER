# TaskManager

Le TaskManager est une application Java permettant de gérer des tâches avec une interface utilisateur graphique. Les utilisateurs peuvent ajouter, modifier, et supprimer des tâches, avec la possibilité d'assigner une date, un titre, une description, une catégorie et un niveau de priorité à chaque tâche. Les catégories peuvent être prédéfinies ou personnalisées, avec un stockage dans un fichier texte, tandis que les tâches sont cryptées et sauvegardées dans un fichier JSON pour plus de sécurité.

## Fonctionnalités

- Ajouter, modifier et supprimer des tâches.
- Assigner un titre, une description, une catégorie, un niveau de priorité et une date à chaque tâche.
- Trois niveaux de priorité prédéfinis (Faible, Moyenne, Haute) avec une couleur correspondante pour chaque priorité.
- Possibilité de créer des catégories personnalisées, sauvegardées dans un fichier texte.
- Filtrage des tâches par catégorie et priorité.
- Sauvegarde cryptée des tâches dans un fichier JSON.

## Gestion des Données

Pour garantir la confidentialité des informations, les tâches sont stockées de manière sécurisée grâce à un cryptage robuste. Le projet gère deux versions du fichier de tâches :

- Une version cryptée, qui assure la protection des données et est utilisée par l'application pour la sauvegarde et le chargement des tâches.
- Une version non cryptée, générée pour le débogage et le développement, qui permet une inspection facile des données stockées.

## Dépendances

Ce projet utilise les librairies suivantes :

- Java Development Kit (JDK) version 8 ou supérieure.
- [JTattoo](http://www.jtattoo.net/) pour l'apparence de l'interface utilisateur.
- [JSON.org](https://www.json.org/json-fr.html) pour la manipulation des données JSON.
- [JCalendar](https://toedter.com/jcalendar/) pour la sélection de dates.

## Gestion des Dépendances

Les dépendances externes requises par ce projet ne sont pas incluses dans le dépôt. Pour les installer, suivez les étapes suivantes :

1. Rendez-vous sur le site web du Maven Repository pour télécharger les fichiers JAR des bibliothèques nécessaires.

2. Pour la bibliothèque `org.json`, utilisez le lien suivant pour obtenir la dernière version : [org.json JSON library JAR](https://mvnrepository.com/artifact/org.json/json)

3. Cliquez sur le lien correspondant à la version souhaitée, puis téléchargez le fichier JAR en cliquant sur le bouton "Download".

4. Pour la bibliothèque `JTattoo`, qui fournit des skins pour l'interface utilisateur, accédez à la page de téléchargement officielle : [JTattoo](http://www.jtattoo.net/Download.html), choisissez la version souhaitée et téléchargez le fichier JAR.

5. Pour la bibliothèque `JCalendar`, qui permet de choisir des dates, consultez la page suivante : [JCalendar](https://toedter.com/jcalendar/), sélectionnez la version nécessaire et téléchargez le fichier JAR.

Une fois que vous avez téléchargé tous les fichiers JAR nécessaires, placez-les dans le dossier `lib/` du projet TaskManager.

## Installation

Pour utiliser TaskManager, suivez ces étapes :

1. Assurez-vous que Java est installé sur votre système.
2. Téléchargez le code source du projet.
3. Installez toutes les librairies nécessaires comme décrit dans la section [Gestion des Dépendances](#gestion-des-dépendances).
4. Utilisez le Makefile inclus pour compiler et exécuter le projet.

## Compilation et Exécution

Avant d'exécuter les commandes suivantes, assurez-vous d'être à la racine du projet. Le Makefile inclus permet de simplifier la compilation et l'exécution de l'application :

- Pour décompresser les librairies et compiler le projet, exécutez : `make build`
- Pour exécuter l'application, utilisez : `make run`
- Pour nettoyer le projet (supprimer les fichiers compilés et les librairies décompressées), tapez : `make clean`

Ces commandes permettent de gérer facilement les différentes phases du cycle de développement du projet depuis la racine de celui-ci.

## Structure du Projet

Le projet est organisé comme suit :

- `src/`: Contient le code source du projet, y compris les classes Java pour l'interface utilisateur, la logique de gestion des tâches, le cryptage, etc.
- `lib/`: Doit contenir les fichiers JAR des bibliothèques externes nécessaires, telles que JTattoo, JSON, et JCalendar. (Notez que le fichier `.gitkeep` est présent pour maintenir la structure du dossier dans le dépôt et n'affecte pas la fonctionnalité du projet.)
- `bin/`: Dossier pour les fichiers compilés (créé par le Makefile).
- `resources/`: Contient les fichiers de données et de configuration nécessaires au fonctionnement de l'application :
  - `keyfile.txt` : Contient la clé de cryptage utilisée pour sécuriser les données des tâches.
  - `categories.txt` : Stocke les catégories créées à travers l'interface TaskManager, permettant une personnalisation par l'utilisateur.
  - `tasks.json` : Fichier JSON crypté contenant toutes les tâches sauvegardées par l'application.
  - `unencrypted_tasks.json` : Version non cryptée du fichier `tasks.json`, utilisée pour le débogage et le développement.

## Contribution

Les contributions à ce projet sont les bienvenues. Pour contribuer, veuillez suivre ces étapes :

1. Forkez le projet.
2. Créez votre branche de fonctionnalité (`git checkout -b feature/AmazingFeature`).
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`).
4. Poussez vers la branche (`git push origin feature/AmazingFeature`).
5. Ouvrez une Pull Request.

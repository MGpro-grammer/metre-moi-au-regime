# Mètre-Moi au Régime

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) ![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white) ![Markdown](https://img.shields.io/badge/Markdown-000000?style=flat&logo=markdown&logoColor=white)
![FXML](https://img.shields.io/badge/FXML-0078D7?style=flat&logo=java&logoColor=white) ![CSS](https://img.shields.io/badge/CSS-1572B6?style=flat&logo=css3&logoColor=white)

## Auteurs

- Groupe D131
- g62218 Mouratidis Georges
- g62265 Grande Ian

## Description du Projet

Mètre-Moi au Régime est une application JavaFX qui permet aux utilisateurs de suivre leur régime alimentaire et leur poids. 
L'application propose des fonctionnalités telles que la création d'un compte utilisateur, le suivi de l'alimentation quotidienne,
la gestion des régimes et le suivi du poids.

## Diagramme de Classe

Le diagramme de classe ci-dessous illustre la structure du modèle de l'application.
Nous donnons également la structure de la base de données utilisées par l'application.

![Diagramme de classe de la partie model](./images/model.png)
![Diagramme de classe de la partie view-model](./images/view_model.png)
![Diagramme de classe de la partie view](./images/view.png)
![Base de données](./images/base_de_donnees.png)

## Choix de l'Architecture

L'architecture retenue pour ce projet est le _model-view-view-model_ soit le MVVM.


## Plan de Tests Fonctionnels

Les tests fonctionnels élémentaires pour le projet sont les suivants :

- Tester la création d'un utilisateur et la connexion de l'utilisateur
- Test de suivi alimentaire et du poids
- Test d'ajout un produit alimentaire
- Test de recherche d'un aliment par un code EAN ou par un nom
- Test du scanner d'un code EAN
- Test d'affichage des produits alimentaires consommés
- Test de l'affichage des activités effectuées

## Calendrier Hebdomadaire des Tâches

### Semaine 1 - 4H

| Qui     | Description                                                   |
|---------|---------------------------------------------------------------|
| Tous    | Analyse du projet                                             |
| Georges | Construction du diagramme de classe de la base de données     |
| Ian     | Construction du diagramme de classe du model et du model-view |

### Semaine 2 - 12H

| Qui     | Description                                                                      |
|---------|----------------------------------------------------------------------------------|
| Tous    | Début du projet - Mise en commun partie 1                                        |
| Georges | Création des DB et des views de l'utilisateur et de son journal (avec des tests) |
| Ian     | Creation du back-end pour l'utilisateur et de son journal (avec des tests)       |

### Semaine 3 - 8H

| Qui     | Description                                                                                                                           |
|---------|---------------------------------------------------------------------------------------------------------------------------------------|
| Tous    | Ajout de la fonctionnalité principale - Mise en commun partie 2                                                                       |
| Georges | Création des DB et des views de l'ajout d'un aliment par la recherche (grâce à l'API, avec des tests pour la DB)                      |
| Ian     | Création du back-end pour l'ajout d'un produit (utilisation du multi-threads) <br> et enregistrement dans le journal (avec des tests) |

### Semaine 4 - 8H
| Qui     | Description                                                                                                                    |
|---------|--------------------------------------------------------------------------------------------------------------------------------|
| Tous    | Ajout de la fonctionnalité de scanner - Mise en commun partie 3                                                                |
| Georges | Creation de la view pour le scanner d'un code EAN et d'affichage d'un produit <br> avec la possibilité de supprimer le produit |
| Ian     | Création du back-end pour l'ajout d'un produit grâce à un scanner (OCR scanner, avec des tests)                                |

### Semaine 5 - 8H
| Qui     | Description                                                                  |
|---------|------------------------------------------------------------------------------|
| Tous    | Ajout des dernières fonctionnalités - Mise en commun partie 4                |
| Georges | Création de la possibilité de modifier son profil dans une view              |
| Ian     | Création du back-end pour modifier en conséquence le profil de l'utilisateur |

### Semaine 6 - 4H
| Qui     | Description                                                                                |
|---------|--------------------------------------------------------------------------------------------|
| Tous    | Finalisation du projet                                                                     |
| Georges | Mise au point et correction de certains bug venant de la view <br> ou des bases de données |
| Ian     | Mise au point et correction de certians bug venant du model  <br> ou du view-model         |

## Installation et utilisation

Pour utiliser l'application, suivez les étapes suivantes : 

1. Clonez ce repository :
   ```bash
   git clone ...
   ```

2. Avancez-vous dans le dossier du projet :
   ```bash
   cd 4prj1d-d131-62218-62265/metre_moi_au_regime/
   ```
3. Lancer l'application avec la commande suivante (si vous avez Maven installé) :
   ```bash
   mvn clean compile exec:java -Dexec.mainClass=be.esi.prj.Main
   ```
## Remerciements

Nous tenons à remercier nos enseignants pour leur soutien et leurs conseils tout au long de ce projet.
Je, Georges Mouratidis, remercie également Ian Grande pour sa collaboration et son travail acharné sur ce projet.
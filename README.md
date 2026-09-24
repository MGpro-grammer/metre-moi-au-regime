<div align="center">

# Mètre-moi au régime

**Desktop nutrition tracker built with JavaFX — log your meals, scan products and follow your daily calorie and macronutrient goals.**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17-3A75B0?style=flat&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white)
![CSS](https://img.shields.io/badge/CSS-1572B6?style=flat&logo=css3&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?style=flat&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-tested-78A641?style=flat)

</div>

## About

**Mètre-moi au régime** is a desktop application that helps users follow a diet. Based on the user's profile
(age, height, weight, activity level and goal), it computes daily calorie and macronutrient needs, lets the user
log the food they eat and the activities they do, and shows their progress towards their goals in real time.

Products can be found by name or barcode (EAN) through the [Open Food Facts](https://world.openfoodfacts.org/)
database, or recognised from a photo of the barcode thanks to OCR.

> **Context** — Academic team project carried out at HE2B ESI (Brussels) by **Georges Mouratidis** and
> **Ian Grande** over six weeks, with a strong focus on a clean layered architecture (MVVM) and automated testing.

## Features

- **Account management** — multi-step sign-up, login, logout and profile editing, with email format
  and password strength validation (uppercase, lowercase, digit and special character).
- **Personalised daily goals** — calories, proteins, fats and carbohydrates computed with the
  Mifflin-St Jeor equation, adjusted to the user's activity level and goal (lose, maintain or gain weight).
- **Food search** — search products by name or EAN code through the Open Food Facts API.
  Name searches fetch 10 result pages **in parallel** using multithreading.
- **Barcode scan from a photo** — the EAN number printed under the barcode is extracted from an image
  with **Tesseract OCR** (French and English models).
- **Food diary** — add or remove consumed products by serving, unit, quantity or percentage.
- **Activity tracking** — log physical activities; burned calories are deducted from the daily intake.
- **Dashboard** — circular gauges showing the progress of each nutrient towards its daily goal.

## Tech stack

| Area         | Technology                                                       |
|--------------|------------------------------------------------------------------|
| Language     | Java 21 (modular project with `module-info.java`)                |
| UI           | JavaFX 17 (FXML + CSS), Ikonli Material Design icons             |
| Persistence  | SQLite via JDBC (`sqlite-jdbc`)                                  |
| External API | Open Food Facts REST API (`java.net.http.HttpClient`)            |
| OCR          | Tess4J 5 (Java wrapper for Tesseract)                            |
| Build        | Maven, `javafx-maven-plugin`                                     |
| Tests        | JUnit 5, Mockito                                                 |

## Architecture

The application follows the **Model-View-ViewModel (MVVM)** pattern. Each layer only depends on the layer
below it, which keeps the UI thin and makes the business logic testable without launching JavaFX.

| Layer      | Package                          | Responsibility                                                            |
|------------|----------------------------------|---------------------------------------------------------------------------|
| View       | `resources/view`, `resources/style` | FXML layouts and CSS stylesheets                                       |
| Controller | `be.esi.prj.controller`          | Binds UI components to the ViewModels and handles navigation between screens |
| ViewModel  | `be.esi.prj.viewmodel`           | Exposes the UI state as observable JavaFX properties                      |
| Model      | `be.esi.prj.model`               | Business logic behind simple entry points (`UserFacade`, `DiaryFacade`, `FoodFacade`) |
| Service    | `be.esi.prj.service`             | Nutritional calculations, validation, session, OCR and Open Food Facts client |
| Repository | `be.esi.prj.repository`          | Data access through repositories (with in-memory cache) and DAOs (JDBC)   |
| DTO        | `be.esi.prj.dto`                 | Immutable Java `record`s exchanged between layers                         |

**Design patterns:** MVVM, Facade, Repository, DAO, DTO, Singleton (session, OCR engine) and Observer (JavaFX bindings).

### Class diagrams

**Model layer**

![Class diagram of the model layer](./images/model.png)

**ViewModel layer**

![Class diagram of the view-model layer](./images/view_model.png)

### Database

The data is stored in a local SQLite database. A user keeps one diary entry per day; each diary entry
contains the consumed products and the activities of that day. Products retrieved from Open Food Facts
are saved locally in the `Food` table.

![Database schema](./images/base_de_donnees.png)


## Project structure

```text
metre-moi-au-regime/
├── images/                              # Diagrams used in this README
└── metre_moi_au_regime/
    ├── pom.xml                          # Maven configuration (dependencies, JavaFX plugin)
    ├── external-data/
    │   └── metre-moi-au-regime.db       # SQLite database
    └── src/
        ├── main/
        │   ├── java/
        │   │   ├── module-info.java     # Java module declaration
        │   │   └── be/esi/prj/
        │   │       ├── Main.java        # Entry point, wires all the layers together
        │   │       ├── controller/      # JavaFX controllers (one per screen)
        │   │       ├── viewmodel/       # ViewModels (observable UI state)
        │   │       ├── model/           # Facades (business logic entry points)
        │   │       ├── service/         # Nutrition, validation, session, OCR, Open Food Facts
        │   │       ├── repository/      # Repositories, DAOs and connection manager
        │   │       ├── dto/             # Immutable records
        │   │       └── enumeration/     # Gender, GoalType, ActivityLevel, ConsumptionType
        │   └── resources/
        │       ├── view/                # FXML screens
        │       ├── style/               # CSS stylesheets
        │       ├── images/              # UI icons
        │       ├── data/                # Tesseract language models (fra, eng)
        │       └── dataForUnitTests/    # Sample images for the OCR tests
        └── test/java/be/esi/prj/        # Unit and integration tests
            ├── model/
            ├── repository/
            ├── service/
            └── viewmodel/
```

## Getting started

### Prerequisites

- **JDK 21** or later (`JAVA_HOME` set or `java` available in the `PATH`)
- An internet connection (Maven dependencies and Open Food Facts API)

Maven does not need to be installed: the project ships with the **Maven Wrapper**.

### Installation

```bash
git clone https://github.com/MGpro-grammer/metre-moi-au-regime.git
cd metre-moi-au-regime/metre_moi_au_regime
```

### Run

**Windows (PowerShell)**

```powershell
.\mvnw.cmd clean javafx:run
```

**Linux / macOS**

```bash
./mvnw clean javafx:run
```

> [!NOTE]
> Launch the application from the `metre_moi_au_regime/` folder: the SQLite database path
> (`external-data/metre-moi-au-regime.db`) is resolved relative to it.
>
> The application was developed and tested on Windows. On Linux and macOS, the OCR scan relies on
> a system installation of Tesseract (`sudo apt install tesseract-ocr` or `brew install tesseract`).

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

## Remerciements

Nous tenons à remercier nos enseignants pour leur soutien et leurs conseils tout au long de ce projet.
Je, Georges Mouratidis, remercie également Ian Grande pour sa collaboration et son travail acharné sur ce projet.

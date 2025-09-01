# EasyWarehouse

EasyWarehouse è un progetto **Kotlin Multiplatform (KMM)** che utilizza **Jetpack Compose Multiplatform** per offrire un'unica codebase compatibile con **Android**, **iOS** e **Desktop (JVM)**.

## Tecnologie principali

- **Kotlin Multiplatform (KMM)** → condivisione della logica tra Android, iOS e Desktop
- **Jetpack Compose Multiplatform** → UI dichiarativa cross-platform
- **Room Database** (solo Android/JVM) con **KSP**
- **SQLite Bundled** come motore SQL integrato
- **Coroutines & Lifecycle Compose** per gestione asincrona e stato
- **Compose Desktop** con supporto a DMG, MSI, DEB

## Struttura del progetto

- **commonMain** → codice condiviso tra tutte le piattaforme (UI, logica, DB astratto)
- **androidMain** → specifico per Android (Activity, integrazione Compose, Room)
- **jvmMain** → desktop application con Compose for Desktop
- **iosX64/iosArm64/iosSimulatorArm64** → target iOS con framework statico

## Requisiti

- **JDK 11**
- **Android Studio / IntelliJ IDEA** (ultima versione consigliata)
- **Xcode** per il build iOS
- **Gradle 8+**

## Build & Run

### Android
```bash
./gradlew :androidApp:installDebug

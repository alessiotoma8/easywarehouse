
# EasyWarehouse

Un’app multipiattaforma per la gestione del magazzino, sviluppata con **Kotlin Multiplatform (KMM)** e **Jetpack Compose Multiplatform**.  
Consente ai dipendenti di registrare cosa e quanto hanno prelevato dal magazzino e agli amministratori di consultare report dettagliati ed esportarli in **CSV**.

## Screenshot
<div style="display: flex; justify-content: space-between;">

  <img src="https://github.com/user-attachments/assets/62b4bb45-4121-404d-98ca-c67a791ddc4d" alt="Dipendente" style="width: 48%;" />
  <img src="https://github.com/user-attachments/assets/ecc3713e-b350-4b3e-95c3-9da072ff3822" alt="Admin" style="width: 48%;" />

</div>

## Built With

**[Kotlin Multiplatform (KMM)](https://kotlinlang.org/lp/multiplatform/)** → condivisione della logica tra Android, iOS e Desktop.  

**[Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** → interfaccia UI dichiarativa cross-platform.  

**[Room Database](https://developer.android.com/jetpack/androidx/releases/room)** (Android/JVM) con **KSP** per persistenza locale.  

**[SQLite Bundled](https://github.com/touchlab/SQLiter)** → motore SQL integrato.  

**[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** → gestione asincrona e flussi di dati.  

**[CSV Export](https://commons.apache.org/proper/commons-csv/)** → per generazione ed esportazione dei report.  

## Funzionalità

- 👷 **Dipendenti** → registrano prelievi dal magazzino (cosa e quantità).  
- 🗂️ **Amministratori** → consultano report dettagliati dei movimenti.  
- 📊 **Esportazione CSV** → salvataggio e condivisione dei report.    

## Installation

Per eseguire il progetto in locale:

```bash
git clone https://github.com/alessiotoma8/easy-warehouse.git
cd easy-warehouse
./gradlew build


Desktop jvm

./gradlew :desktopApp:run

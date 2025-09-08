

# EasyWarehouse

A cross-platform warehouse management app, built with **Kotlin Multiplatform (KMM)** and **Jetpack Compose Multiplatform**.  
It allows employees to record what and how much they have taken from the warehouse, while administrators can view detailed reports and export them in **CSV** format.

## Screenshot
<div style="display: flex; justify-content: space-between;">

  <img src="https://github.com/user-attachments/assets/e782c94e-5ebf-44cf-8c75-3c5f96f4c144" style="width: 48%;"/>
  <img src="https://github.com/user-attachments/assets/870a2609-ca8f-4732-b515-5f553a9ee7fb" style="width: 48%;"/>
  <img src="https://github.com/user-attachments/assets/ada6c44d-7092-465a-b865-e63ce8e03bb0" style="width: 48%;"/>
  <img src="https://github.com/user-attachments/assets/89ffe8fc-ccc7-44ec-95de-87d7a21c4b51" style="width: 48%;"/>

  

</div>

## Built With

**[Kotlin Multiplatform (KMM)](https://kotlinlang.org/lp/multiplatform/)** → shared business logic across Android, iOS, and Desktop.  

**[Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** → declarative UI for multiple platforms.  

**[Room Database](https://developer.android.com/jetpack/androidx/releases/room)** (Android/JVM) with **KSP** for local persistence.  

**[SQLite Bundled](https://github.com/touchlab/SQLiter)** → embedded SQL engine.  

**[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** → asynchronous programming and state management.  

**[CSV Export](https://commons.apache.org/proper/commons-csv/)** → for generating and exporting reports.  

## Features

- 👷 **Employees** → record warehouse withdrawals (item and quantity).  
- 🗂️ **Administrators** → view detailed movement reports.  
- 📊 **CSV Export** → save and share reports.  

## Installation

To run the project locally:

```bash
git clone https://github.com/alessiotoma8/easy-warehouse.git
cd easy-warehouse
./gradlew build

Desktop (JVM)

./gradlew :desktopApp:run





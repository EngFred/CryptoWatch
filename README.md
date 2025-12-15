# CryptoWatch

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**CryptoWatch** is a high-fidelity, offline-first cryptocurrency tracker built to demonstrate modern Android development practices. It features a robust Clean Architecture, seamless offline caching, infinite pagination, and background synchronization using the latest Jetpack libraries.

---

## Screenshots

| Market List (Infinite Scroll) | Coin Detail (Interactive Charts) |
|:-------------------------:|:-------------------------:|
| ![Market Screen](https://github.com/user-attachments/assets/35e90b46-0e7b-4e1b-90b7-7770b08517a7) | ![Detail Screen](https://github.com/user-attachments/assets/4c9ea322-9d72-4c80-afae-5315de21c8be) |
| *Real-time market data with Sparklines* | *Detailed stats with time-range visualization* |

---

## Download

Try the latest build on your device:

[![Download APK](https://img.shields.io/badge/Download_APK-GitHub_Release-2ea44f?style=for-the-badge&logo=github)](https://github.com/EngFred/CryptoWatch/releases/download/v1.0/CryptoWatch.apk)

---

## Key Features

* **Offline-First Architecture**: Uses a "Single Source of Truth" approach. The UI always observes the Database; the Network only refreshes the Database. Apps works flawlessly in Airplane mode.
* **Hybrid Search Engine**: Instantly searches the local cache. If no results are found, it transparently triggers a remote API search, caches the result, and updates the UI automatically.
* **Infinite Pagination**: Implemented using **Paging 3** with a `RemoteMediator` to handle network latency and caching seamlessly.
* **Smart Rate Limiting**: Custom handling for HTTP 429 errors using throttling strategies to stay within CoinGecko's free tier limits (30 calls/min).
* **Background Synchronization**: A **WorkManager** worker periodically syncs high-priority market data in the background to keep the cache fresh.
* **Custom UI Components**: Built entirely with **Jetpack Compose** and Material 3. Features custom-drawn Canvas **Sparkline Charts** for performance.

---

## Tech Stack

* **Language**: [Kotlin](https://kotlinlang.org/) (100%)
* **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Architecture**: Clean Architecture (MVVM + Repository Pattern)
* **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
* **Network**: [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/)
* **Local Storage**: [Room Database](https://developer.android.com/training/data-storage/room)
* **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3) (RemoteMediator)
* **Async**: [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
* **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
* **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)

---

## Architecture

The app follows strict **Clean Architecture** principles to ensure scalability and testability.

```text
graph TD
    UI[Presentation Layer<br>(Compose + ViewModel)] --> Domain[Domain Layer<br>(UseCases + Models)]
    Domain --> Data[Data Layer<br>(Repository Impl)]
    Data --> Remote[Remote Data Source<br>(Retrofit + API)]
    Data --> Local[Local Data Source<br>(Room + DAO)]
    Local --> Cache[(Device Storage)]
```

Domain Layer: Contains pure business logic (UseCases) and plain Kotlin models. No Android dependencies.
Data Layer: Handles data coordination. The RepositoryImpl decides whether to fetch from the API or return cached data.
Presentation Layer: ViewModels transform data flows into UIState for the Composables to render.

##Getting Started

1.  **Clone the repository**:
    ```bash
    git clone [https://github.com/EngFred/CryptoWatch.git](https://github.com/EngFred/CryptoWatch.git)
    ```
2.  **Open in Android Studio** (Ladybug or newer recommended).
3.  **Sync Gradle** to download dependencies.
4.  **Run** on an Emulator or Physical Device.

*Note: No API Key is required. The app uses the public CoinGecko API free tier.*

---

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

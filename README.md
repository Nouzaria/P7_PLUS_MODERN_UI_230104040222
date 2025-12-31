# 🔐 SecureAuth Modern UI

**SecureAuth** is a professional-grade Android security management application designed to demonstrate the implementation of **Material Design 3** principles alongside advanced authentication workflows. Built entirely with **Jetpack Compose**, this project showcases a unified design system, robust biometric integration, and a highly responsive dashboard architecture.

---

## 📖 Table of Contents

* [Overview](#-overview)
* [Key Features](#-key-features)
* [UI & Design System](#-ui--design-system)
* [Tech Stack](#-tech-stack)
* [Security & Architecture](#-security--architecture)
* [Installation](#-installation)

---

## 🚀 Overview

In modern mobile development, a visually appealing UI must be matched by sophisticated logic and security. **SecureAuth** serves as a comprehensive implementation of the **Material 3** design system. It goes beyond simple layouts by integrating multi-screen navigation, persistent local storage, and high-fidelity components like elevated cards, gradient surfaces, and interactive feedback mechanisms.

The application is structured around a centralized authentication engine that manages user sessions, biometric states, and app-wide security settings.

---

## ✨ Key Features

### 1. 🛡️ Advanced Security Dashboard

* **Security Health:** Real-time summary of the account's protection status using dynamic visual indicators.
* **Drill-down Details:** Comprehensive breakdown of security categories including sign-in methods, device sessions, and alerts.
* **Recent Activity:** A detailed log of system events like password changes and new device logins.

### 2. 🧬 Biometric Authentication

* **System Integration:** Powered by the **AndroidX Biometric API** for secure fingerprint and face recognition.
* **Dynamic Enrollment:** Smart detection of device hardware and user enrollment status to provide context-aware login options.
* **Session Continuity:** Seamless transition from biometric success to the home dashboard via an automated navigation host.

### 3. ⚙️ Centralized Settings & App Lock

* **Lifecycle-based Locking:** Implements an automated **App Lock** that triggers when the application is resumed after being idle.
* **Preference Management:** User-controlled toggles for notification alerts, public Wi-Fi warnings, and biometric preferences.
* **Persistence:** All configurations are saved locally via **SharedPreferences**, ensuring a consistent experience across app restarts.

---

## 🎨 UI & Design System

SecureAuth is built upon a modular **Design System** that ensures visual consistency across all modules:

* **Material 3 Core:** Utilizes the latest MD3 components including `ElevatedCard`, `OutlinedTextField`, and `CenterAlignedTopAppBar`.
* **Unified Theming:** A centralized theme engine managing a custom `ColorScheme` for both **Light and Dark Modes**.
* **Modern Geometry:** Consistent use of **RoundedCornerShape** (ranging from 12dp to 24dp) for buttons, cards, and input fields.
* **Visual Hierarchy:** Professional typography scaling using display, headline, and body styles to ensure maximum readability.

---

## 🛠 Tech Stack

* **Language:** Kotlin 
* **UI Toolkit:** Jetpack Compose (Material 3) 
* **Architecture:** ViewModel with StateFlow for reactive UI updates 
* **Navigation:** Navigation Compose (AppNavHost) 
* **Security API:** AndroidX Biometric 
* **Storage:** SharedPreferences for account and settings persistence 
* **Design Resources:** Material Icons Extended for intuitive navigation 

---

## 📦 Installation

### 1. **Clone the repository**
```bash
git clone https://github.com/username/SecureAuth_P7.git

```
### 2. **Open in Android Studio**
* Minimum SDK: API 24 (Android 7.0).
### 3. **Sync Gradle**
* Requires `androidx.compose.material3:material3:1.2.1` or higher.
### 4. **Run on Device**
* Use a device or emulator with biometric hardware support for full feature testing.

---

<p align="center">
  Created by <b>Nouzaria</b>
</p>

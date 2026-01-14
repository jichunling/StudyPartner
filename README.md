# StudyPartner

**A Smart Social Learning Platform Connecting Students Through Shared Academic Goals**

---

## Project Overview

**studyPartner** solves a critical challenge in education: **finding compatible study partners**. This Android app uses intelligent matchmaking algorithms combined with AI-powered assistance to transform isolated studying into a collaborative learning experience.

### The Problem It Solves

- **Isolation in Learning** → Students study alone, missing collaborative benefits
- **Inefficient Partner Discovery** → Finding compatible partners is time-consuming
- **Scheduling Conflicts** → Coordinating study sessions is difficult
- **Learning Style Mismatch** → Different difficulty levels create friction
- **Limited Resources** → Students need on-demand assistance

### The Solution

✓ **Smart matchmaking** based on topics, schedules, and difficulty levels
✓ **AI-powered study assistant** for instant help
✓ **Connection management** to build a study network

---

## Features

- **User Authentication:** Secure login/signup with password recovery
- **Smart Matchmaking:** Algorithm-based matching using topic, schedule, and difficulty preferences
- **Profile Management:** Customizable profiles with social media integration
- **Preference System:** Select topics (CS, Math, Biology, etc.), study times, and difficulty levels
- **AI Study Assistant:** Integrated ChatGPT for real-time Q&A and topic explanations
- **Connection Management:** Like/unlike system to build study partner network
- **Persistent Sessions:** SharedPreferences for seamless user experience

---

## Tech Stack

### Platform & Language
- **Android** (API 24+ / Android 7.0+) | Target SDK: **Android 14** (API 34)
- **Java** | **Gradle** (Kotlin DSL)

### Core Technologies
- **Architecture:** MVC Pattern
- **UI:** **Material Design**, **ConstraintLayout**, **RecyclerView**
- **Database:** **SQLite** with custom DatabaseHelper
- **Networking:** **OkHttp 4.12.0**
- **AI Integration:** **OpenAI ChatGPT API**
- **Email:** **JavaMail API**

### Key Dependencies
- **OkHttp 4.12.0** - HTTP client for networking
- **AndroidX Material** - Material Design components
- **AndroidX RecyclerView** - Efficient list displays
- **JavaMail API** - Email functionality

---

## How to Run

### Prerequisites
- **Android Studio** (Arctic Fox+)
- **JDK 8+**
- **Android SDK** (API 24-34)

### Quick Start

1. **Clone & Open**
   ```bash
   git clone https://github.com/jichunling/studyPartner.git
   ```
   Open project in Android Studio

2. **Configure SDK**

   Create `local.properties`:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   ```

3. **Build & Run**
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```
   Or click **Run** ▶️ in Android Studio

4. **[Optional] Add OpenAI API Key**

   Update `OpenAIClient.java` for AI features ([Get API key](https://platform.openai.com/))

---

## Project Structure

```
studyPartner/
├── app/src/main/java/com/example/studypartner/
│   ├── activities/          # UI Activities 
│   ├── adapter/             # RecyclerView Adapters
│   ├── data/
│   │   ├── database/        # SQLite Database Management
│   │   └── model/           # Data Models (User, Connections)
|   ├── fragments            # Fragments
│   └── utils/               # Validation and Utilities
│   
└── app/src/main/res/        # Layouts, Drawables, Values
```

---

## Usage

1. **Sign Up** → Create account with email/password
2. **Set Preferences** → Choose topics, study times, difficulty level
3. **Browse Matches** → View compatible study partners
4. **Connect** → Like profiles to build connections
5. **Get AI Help** → Access GenAI tab for instant assistance

---

## License

This project is licensed under the MIT License.

---

**Built for students seeking better collaborative learning experiences**

# Lost2Connect — Campus Lost & Found Management System

Lost2Connect is a full-stack campus Lost and Found web application built with Spring Boot, Spring Security (JWT), Cloud Firestore DB, and modern HTML5/CSS3 responsive UI.

## 🚀 Features

- **🔐 Authentication & User Roles**: JWT-based sign-in and registration with Firestore persistence.
- **🎒 Lost Item Reporting**: Report missing items with category tags, location landmarks, and photo uploads.
- **🔍 Found Item Reporting**: Report items found across campus with storage custody details.
- **🤝 Ownership Claims**: Verification claim submission with finder approval workflows and community points (+5 per reunite).
- **⚡ Smart Matching**: Intelligent matching engine correlating lost and found items.
- **🔔 Real-time Notifications**: Alerts for new reports, matches, and claim status updates.
- **👤 Student Profiles**: Customizable profiles showing activity, community points, and uploaded profile pictures.

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.3.4, Spring Security (JWT)
- **Database**: Google Cloud Firestore (Firebase)
- **Frontend**: HTML5, Vanilla CSS3, Client-side JavaScript
- **Build System**: Maven

## 💻 Running Locally

1. Prerequisites: JDK 21+ and Maven.
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```
3. Open your browser at:
   ```
   http://localhost:8080/index.html
   ```

## 📜 License
MIT License.

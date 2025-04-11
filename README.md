📡 Smart Home IoT and AI Integrated Early Warning System
🔍 Overview
This project is a graduation-level research and development effort to build an integrated smart home safety system. It leverages Internet of Things (IoT) and Artificial Intelligence (AI) to monitor home environments in real time and issue early warnings for fire risks, unauthorized intrusions, flooding, and other environmental hazards.
The system is designed to increase home safety, convenience, and automation, while being cost-effective and scalable for various types of users—from individual homeowners to property managers.

🎯 Objectives
-Enhance Home Safety: Detect hazards such as fire, gas leaks, and intrusions early.
- Improve Security: Prevent unauthorized access with real-time monitoring and alerts.
- Increase Convenience: Allow remote control and monitoring via a mobile app.
- Leverage AI: Use machine learning for predictive hazard detection.
- Support Sustainability: Improve energy usage efficiency and reduce manual intervention.

🧩 Key Components
🛠️ Hardware
- IoT Sensors: Temperature, humidity, gas leak, motion, and smoke detectors.
- Microcontroller: ESP32 with MQTT communication.
- Smart Devices: Smart lights, locks, alarms, and water leak sensors.

📱 Software
- Mobile App (Android): User dashboard, control panel, and real-time alerts.
- Backend: Spring Boot server, MySQL database, RESTful APIs.
- AI Engine: Embedded TensorFlow Lite models for local inference.
- Cloud Integration: MQTT broker, SMS/Email gateway.

💡 Features
- Real-Time Monitoring: Continuous environment tracking
- Early Warning Notifications: Via mobile app, SMS, and email.
- Device Control: Turn smart devices on/off remotely.
- User Management: Role-based login, registration, and authentication.
- AI Risk Assessment: Analyze sensor data for abnormal patterns.
- Maintenance Tools: Setup, diagnostics, and sensor troubleshooting.

🛠 Technologies Used
- Languages: Java, Kotlin (Android), Python (AI module)
- Frameworks: Spring Boot, TensorFlow Lite, Android SDK
- Protocols: MQTT, HTTP(S), I²C/SPI (IoT)
- Database: MySQL
- Version Control: GitHub
- UI Design: Figma (Wireframes, Prototypes)

📊 System Architecture
- IoT Sensors capture data →
- Microcontroller (ESP32) transmits via MQTT →
- Backend Server analyzes with AI →
- Alerts sent to users through the Mobile App/SMS/Email →
- Smart Devices respond (e.g., turning off gas valve, turning on alarm)

👥 User Roles
- Homeowner: Receives alerts, controls devices, views reports.
- Technician: Installs and configures sensors, handles maintenance.
- AI System: Assesses risk levels and generates alerts.

🧪 Testing and Security
✅ Unit & integration testing for each module

🔐 Multi-Factor Authentication (MFA)

🔒 Encrypted data transmission

📈 System logs for audit and diagnostics

📅 Project Timeline
Development is organized across several phases:
- Requirement Gathering
- System Design & Modeling
- Implementation
- Testing & Evaluation
- Final Delivery & Reporting
- Gantt chart and timeline are included in the official documentation.

📂 Documentation
- Software Requirement Specification (SRS)
- User Requirement Document (URD)
- System Analysis & Design Document
- Research Ethics & Proposal Forms
- All located in the /docs/ directory.

👨‍💻 Team
- Project Lead: Do Thanh Tung
- Team Members: Luu Thi Le, Sang, Cuong, Bien, Huy
- Supervisor: Mr. Trieu (BTEC IT Department)

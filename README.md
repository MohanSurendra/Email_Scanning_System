# Email Scanning System

This is a basic Email Scanning System developed as part of my summer internship during my college time in 2024.
The system is a Java-based web application designed to scan, monitor, and manage email activities for security purposes.
It provides administrators with tools to detect suspicious messages, maintain logs, and generate reports.

---

## 📌 Key Features

- Scan incoming emails using predefined security rules
- Generate and store logs of scanned messages
- View detailed reports of flagged emails
- Manage scanning rules via an intuitive interface
- Modular design for easy extension and integration

---

## 🛠 Technology Stack

- **Language**: Java  
- **Framework**: Swing (Java EE)  
- **Build Tool**: Apache Maven  
- **Database**: MySQL (via JDBC)  
- **IDE**: Eclipse    

---



## 📁 Project Structure
emailscanningsystem/
├── pom.xml
├── email_report.txt
├── .settings/
├── src/
│ └── main/
│ └── java/
│ └── com/example/
│ ├── database/
│ │ └── DatabaseConnection.java
│ └── web/
│ ├── EmailScannerApp.java
│ ├── LogForm.java
│ ├── ManageRulesForm.java
│ ├── ReportsForm.java
│ └── ViewLogsForm.java
│
└── webapp/
├── META-INF/
└── WEB-INF/
└── web.xml
---

## 🚀 Setup Instructions

1. **Install Prerequisites**
   - Java Development Kit (JDK 8 or higher)
   - Apache Maven
   - MySQL server
   - Eclipse IDE or compatible Java IDE

2. **Database Setup**
   - Create a new PostgreSQL database (e.g., `email_scanner`)
   - Update `DatabaseConnection.java` with your DB credentials
   - Import any SQL schema provided (if applicable)

3. **Build the Project**
   - Import the Maven project into Eclipse
   - Resolve dependencies using Maven
   - Clean and build the project

4. **Deploy**
   - Run the Main file and access the application via browser

---
## 🧪 Screenshots

Screenshots demonstrating the interface and scanning functionality are included in the root directory:
- Screenshot (365).png
- Screenshot (368).png
- ...
- Screenshot (376).png

---

## 📄 Documentation

Final Email Scanning System.docx



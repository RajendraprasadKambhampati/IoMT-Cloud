# Trust-Aware Secure Federated Cloud Storage for IoMT
## With Hybrid ABE, Blockchain, and Anomaly Detection

A fully functional J2EE web application implementing a secure federated cloud storage system for Internet of Medical Things (IoMT).

---

## 🧩 Tech Stack
- **Frontend:** JSP (JavaServer Pages)
- **Backend:** Java Servlets (J2EE)
- **Database:** MySQL
- **Server:** Apache Tomcat 9+
- **OS:** Windows Compatible

---

## 🏗️ System Modules

| Module | Description |
|--------|-------------|
| 👤 User Module | Registration, login, role-based access (Doctor/Hospital/Device/Admin) |
| 🔐 Hybrid ABE | AES encryption + Attribute-Based Encryption policy engine |
| ☁️ Cloud Storage | Encrypted file storage with metadata and integrity hashing |
| ⛓️ Blockchain | Immutable audit trail with hash chaining for all events |
| 🤝 Federated Learning | Simulated distributed model training across IoMT devices |
| 🚨 Anomaly Detection | Rule-based detection of brute force, data exfiltration, policy violations |
| 🛡️ Trust Management | Dynamic trust scoring affecting user access privileges |
| 👨‍💼 Admin Module | System monitoring, user management, analytics dashboard |

---

## 📋 Prerequisites

1. **Java JDK 8+** — [Download](https://www.oracle.com/java/technologies/javase-downloads.html)
2. **Apache Tomcat 9+** — [Download](https://tomcat.apache.org/download-90.cgi)
3. **MySQL 8.0+** — [Download](https://dev.mysql.com/downloads/installer/)
4. **MySQL Connector/J** — [Download](https://dev.mysql.com/downloads/connector/j/)

---

## 🚀 Setup Instructions

### Step 1: Database Setup

1. Open MySQL command line or MySQL Workbench
2. Run the schema script:
```sql
source C:/path/to/project/sql/schema.sql;
```
Or copy-paste the contents of `sql/schema.sql` into MySQL.

3. Verify:
```sql
USE iomt_cloud;
SHOW TABLES;
SELECT * FROM users;
```

### Step 2: Configure Database Connection

Edit `db.properties` with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/iomt_cloud?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
db.driver=com.mysql.cj.jdbc.Driver
```

### Step 3: MySQL Connector JAR

Download `mysql-connector-java-8.0.30.jar` from MySQL website and place it in:
```
web/WEB-INF/lib/mysql-connector-java-8.0.30.jar
```

### Step 4: Deploy to Tomcat (IDE Method — Recommended)

#### Using Eclipse/IntelliJ:
1. Import the project as a **Dynamic Web Project**
2. Set the source folder to `src/java`
3. Set the web content folder to `web`
4. Add Tomcat 9 as the target runtime
5. Add `mysql-connector-java-8.0.30.jar` to `WEB-INF/lib`
6. Right-click → Run on Server → Tomcat

#### Using Command Line:
1. Compile Java files:
```bash
# Create output directory
mkdir -p web/WEB-INF/classes

# Compile (adjust classpath for your Tomcat location)
javac -cp "C:/path/to/tomcat/lib/servlet-api.jar;web/WEB-INF/lib/mysql-connector-java-8.0.30.jar" -d web/WEB-INF/classes src/java/com/iomt/**/*.java
```

2. Copy `db.properties` to classes:
```bash
copy db.properties web/WEB-INF/classes/
```

3. Copy the `web` folder to Tomcat's webapps:
```bash
xcopy web "C:/path/to/tomcat/webapps/iomt/" /E /I
```

4. Start Tomcat and visit: `http://localhost:8080/iomt/`

### Step 5: Access the Application

Open your browser and go to:
```
http://localhost:8080/iomt/login
```

---

## 🔑 Test Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@iomt.com | admin123 |
| Doctor | doctor@iomt.com | admin123 |
| Hospital | hospital@iomt.com | admin123 |
| Device | device@iomt.com | admin123 |

---

## 📁 Project Structure

```
final year project/
├── src/java/com/iomt/
│   ├── model/            # Data models (POJOs)
│   │   ├── User.java
│   │   ├── FileRecord.java
│   │   ├── Block.java
│   │   ├── LogEntry.java
│   │   ├── FederatedUpdate.java
│   │   └── AccessRequest.java
│   ├── dao/              # Database access layer
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   ├── FileDAO.java
│   │   ├── BlockchainDAO.java
│   │   ├── LogDAO.java
│   │   ├── AccessRequestDAO.java
│   │   └── FederatedDAO.java
│   ├── servlet/          # Controllers
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── DashboardServlet.java
│   │   ├── FileUploadServlet.java
│   │   ├── FileDownloadServlet.java
│   │   ├── FileListServlet.java
│   │   ├── AccessRequestServlet.java
│   │   ├── BlockchainServlet.java
│   │   ├── FederatedServlet.java
│   │   ├── AnomalyServlet.java
│   │   ├── TrustServlet.java
│   │   └── AdminServlet.java
│   ├── crypto/           # Encryption
│   │   ├── AESUtil.java
│   │   └── ABEPolicy.java
│   ├── blockchain/       # Blockchain
│   │   └── Blockchain.java
│   ├── anomaly/          # Anomaly detection
│   │   └── AnomalyDetector.java
│   ├── trust/            # Trust management
│   │   └── TrustManager.java
│   ├── federated/        # Federated learning
│   │   └── FederatedLearning.java
│   └── filter/           # Auth filter
│       └── AuthFilter.java
├── web/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/          (place mysql-connector-java JAR here)
│   ├── css/style.css
│   ├── js/main.js
│   ├── login.jsp
│   ├── register.jsp
│   ├── dashboard.jsp
│   ├── admin-dashboard.jsp
│   ├── upload.jsp
│   ├── files.jsp
│   ├── blockchain.jsp
│   ├── anomaly.jsp
│   ├── trust.jsp
│   ├── federated.jsp
│   ├── access-requests.jsp
│   └── error.jsp
├── sql/schema.sql
├── db.properties
└── README.md
```

---

## 🗄️ Database Tables

| Table | Purpose |
|-------|---------|
| `users` | User accounts with roles, attributes, trust scores |
| `files` | Encrypted file storage with ABE policies |
| `access_requests` | File access request tracking |
| `blockchain` | Immutable event log chain |
| `logs` | System activity logs with anomaly flags |
| `federated_updates` | Federated learning model weights |

---

## ⚙️ Features

### 🔐 Hybrid ABE Encryption
- Files encrypted with AES-128-CBC
- AES key protected by CP-ABE policy
- Policy format: `role=doctor AND dept=cardiology AND level>=2`
- Decryption only if user attributes match policy

### ⛓️ Blockchain Audit Trail
- SHA-256 hash chaining
- Immutable logs for uploads, access, trust changes
- Chain integrity validation
- Visual block explorer

### 🚨 Anomaly Detection Rules
- **Brute Force:** 3+ failed logins in 10 minutes
- **Data Exfiltration:** 10+ file accesses in 5 minutes
- **Policy Violation:** Unauthorized access attempts
- **Account Abuse:** Blocked/restricted user activity

### 🛡️ Trust Scoring
- 80-100: HIGH (full access)
- 50-79: MEDIUM (standard access)
- 30-49: LOW (restricted)
- 0-29: BLOCKED (no access)

### 🤝 Federated Learning
- 5 simulated IoMT devices
- Local training with random weights
- FedAvg aggregation algorithm
- Local vs global weight comparison

---

## 📜 License
This project is developed for academic/educational purposes.

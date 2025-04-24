# 🏗️ BTO Management System

This is a **Command Line Interface (CLI)** Java application developed for managing Build-To-Order (BTO) projects, as part of the **SC2002 Object-Oriented Design & Programming** course (2024/2025 Semester 2) at NTU.

> 📁 This repository includes the complete source code, documentation, UML diagrams, sample data files, and a final report as required by the course deliverables.

---

## 🎯 Key Features by Role

### 👤 Applicant
- View eligible projects (based on visibility, age, and marital status)
- Apply for one project only
- View current application status: Pending, Successful, Unsuccessful, Booked
- Submit withdrawal requests
- Manage project enquiries (submit, edit, delete)

### 🧑‍💼 HDB Officer
- Inherits all applicant functionalities
- Register to handle a project (approval required)
- View and manage project bookings
- Generate receipts for booked flats
- Respond to project enquiries

### 👨‍💼 HDB Manager
- Create, edit, and delete BTO projects
- Toggle project visibility
- Approve/reject officer registrations and application requests
- Generate reports based on filters (e.g., marital status, flat types)
- View and respond to enquiries

---

## 🔗 Quick Links

- 📄 [Project Report (PDF)](./docs/FDAE-grp1.docx)
- 🖼️ [UML Class Diagram](./docs/class-diagram.png)
- 🧬 [UML Sequence Diagram](./docs/sequence-diagram.png)
- 📚 [Javadoc API Documentation](https://vishvaaj001.github.io/SC2002-project/)

---

## 🔧 Prerequisites

To run the application, ensure the following is installed:

- **Java JDK 8 or higher**  
  Verify with:

  ```bash
  java -version
  ```

> (Optional) To build the project yourself, Maven is required:
> 
> ```bash
> mvn -version
> ```

---

## 🚀 Setup & Execution Instructions

1. **Clone the Repository**

   ```bash
   git clone <repository-url>
   cd SC2002-project
   ```

2. **Run the Application**

   Navigate to the JAR file and run it:

   ```bash
   java -jar fdae-group1-bto-project-1.0.jar ./data
   ```

   > If `fdae-group1-bto-project-1.0.jar` is not present, you can generate it using:

   ```bash
   mvn clean package
   ```

---

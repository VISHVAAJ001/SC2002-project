# BTO Management System

This is a **Command Line Interface (CLI)** Java application developed for managing Build-To-Order (BTO) projects, as part of the **SC2002 Object-Oriented Design & Programming** course (2024/2025 Semester 2) at NTU.

> This repository includes the complete source code, documentation, UML diagrams, sample data files, and a final report as required by the course deliverables.

---

## Key Features by Role

### Applicant
- View eligible projects (based on visibility, age, and marital status)
- Apply for one project only
- View current application status: Pending, Successful, Unsuccessful, Booked
- Submit withdrawal requests
- Manage project enquiries (submit, edit, delete)

### HDB Officer
- Inherits all applicant functionalities
- Register to handle a project (approval required)
- View and manage project bookings
- Generate receipts for booked flats
- Respond to project enquiries

### HDB Manager
- Create, edit, and delete BTO projects
- Toggle project visibility
- Approve/reject officer registrations and application requests
- Generate reports based on filters (e.g., marital status, flat types)
- View and respond to enquiries

---

## Quick Links

- [UML Class Diagram - Overview](./docs/FDAE_grp1_Overview-UML-Diagram.jpg)
- [UML Class Diagram - Entities](./docs/FDAE_grp1_Entities-UML-Diagram.jpg)
- [UML Class Diagram - Controllers](./docs/FDAE_grp1_Entities-UML-Diagram.jpg)
- [UML Class Diagram - Views](./docs/FDAE_grp1_Views-UML-Diagram.jpg)
- [UML Class Diagram - Repositories](./docs/FDAE_grp1_Repositories-UML-Diagram.jpg)
- [UML Class Diagram - Services](./docs/FDAE_grp1_Services-UML-Diagram.jpg)
- [UML Sequence Diagram](./docs/FDAE_grp1_HDB-Officer-Apply-and-Registration.jpg)
- [UML Sequence Diagram - HDB Manager creating a new project](./docs/FDAE_grp1_HDB-Manager-creating-a-new-project.jpg)
- [UML Sequence Diagram](./docs/FDAE_grp1_User-Logging-in(Success-and-Fail).jpg)
- [Javadoc API Documentation](https://vishvaaj001.github.io/SC2002-project/)

---

## Prerequisites

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

## Setup & Execution Instructions

1. **Clone the Repository**

   ```bash
   git clone https://github.com/VISHVAAJ001/SC2002-project
   cd SC2002-project
   ```

2. **Run the Application**

   Navigate to the JAR file and run it:

   ```bash
   java -jar FDAE_group1-bto-project-1.0.jar ./data
   ```

   > If `FDAE_group1-bto-project-1.0.jar` is not present, you can generate it using:

   ```bash
   mvn clean package
   ```

---

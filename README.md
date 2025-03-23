# BTO Management System

This is a Command Line Interface (CLI) application for managing Build-To-Order (BTO) projects. The system is designed for applicants and HDB staff to view, apply, and manage BTO projects. Follow the instructions below to set up the project for development.

## Overview of the System

The BTO Management System acts as a centralized hub for applicants and HDB staff. The system is implemented as a CLI application and requires **Java** and **Maven** to run.

## Prerequisites

Before starting, ensure you have the following installed on your computer:

1. **Java Development Kit (JDK)**: Download and install **JDK 8 or higher** from [AdoptOpenJDK](https://adoptopenjdk.net/).  
   After installation, verify it by running the following command in your terminal:

   ```bash
   java -version
   ```

   You should see the installed version of Java.

2. **Apache Maven**: Download and install **Maven** from [Maven's official website](https://maven.apache.org/).  
   Verify the installation by running:

   ```bash
   mvn -version
   ```

   This should display the Maven version.

3. **GitHub Desktop**: Download and install [GitHub Desktop](https://desktop.github.com/).  
   This tool provides an easy-to-use interface for managing Git repositories.

## Setup Instructions

Follow these steps to set up the project on your local machine:

1. **Clone the Repository**

   Open **GitHub Desktop** and follow these steps:

   - Click on **File > Clone Repository**.
   - Select the **URL** tab and paste the repository URL (e.g., `<repository-url>`).
   - Choose a local path where you want to save the project and click **Clone**.

   Replace `<repository-url>` with the actual URL of the Git repository.

2. **Open the Project in a Terminal**

   After cloning, open the project folder in a terminal. You can do this directly from **GitHub Desktop** by clicking **Repository > Open in Terminal**.

3. **Build the Project**

   Use **Maven** to download the required dependencies and build the project. Run:

   ```bash
   mvn clean install
   ```

   This will **compile the code**, **run tests**, and **package the application**.

4. **Run the Application**

   To start the application, use the following command:

   ```bash
   mvn exec:java -Dexec.mainClass="com.ntu.fdae.group1.bto.App"
   ```

   Replace `com.ntu.fdae.group1.bto.App` with the **actual main class** of your application if it differs.

5. **Run Tests**

   To ensure everything is working correctly, you can run the unit tests with:

   ```bash
   mvn test
   ```

   This will execute all the tests in the project and display the results.

## Project Structure

Here’s an overview of the project structure:

- `src/main/java`: Contains the **main application code**.
- `src/test/java`: Contains **unit tests** for the application.
- `pom.xml`: The **Maven configuration file**, which manages dependencies and build settings.

## Troubleshooting

- If you encounter issues with Maven commands, ensure that **`JAVA_HOME`** is set correctly in your system environment variables.  
  You can check it by running:

  ```bash
  echo $JAVA_HOME
  ```

  It should point to your **JDK installation directory**.

- If dependencies fail to download, check your **internet connection** and try running:

  ```bash
  mvn clean install -U
  ```

  The `-U` flag forces Maven to **update dependencies**.

## Contributing

If you want to contribute to this project:

1. **Create a new branch** for your feature or bug fix:

   - In **GitHub Desktop**, click **Branch > New Branch**.
   - Enter a branch name (e.g., `feature-name`) and click **Create Branch**.

2. **Make your changes and commit them**:

   - After making changes, go to **GitHub Desktop** and write a clear commit message.
   - Click **Commit to <branch-name>**.

3. **Push your branch and create a pull request**:

   - Click **Push Origin** in **GitHub Desktop** to upload your branch.
   - Go to the repository on GitHub and click **Compare & pull request** to create a pull request.

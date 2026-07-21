# PaardenWiskunde (Horse Maths)

PaardenWiskunde is a Kotlin command-line (TUI) application for managing Wynncraft horse data, and determining the most optimal food plan. 

## Requirements

To develop and deploy this application, you need:

* Java JDK installed
* Gradle wrapper included in the project
* Windows (for the current `.bat` launcher)

Verify your Java installation:

```powershell
java -version
```

## Running the Application During Development

To run the application directly from the project:

```powershell
./gradlew run
```

This starts the application using the Gradle development environment.

## Building and Deploying

For normal usage, the application is built as a standalone executable JAR.

Run:

```powershell
./gradlew deploy
```

This task will:

1. Build the application as a Shadow JAR
2. Copy the required files to the local installation directory
3. Replace an existing installation automatically

After a successful deployment, the application will be available at:

```text
%LOCALAPPDATA%\PaardenWiskunde\
├── PaardenWiskunde.jar
└── PaardenWiskunde.bat
```

## Starting the Application

Run:

```text
%LOCALAPPDATA%\PaardenWiskunde\PaardenWiskunde.bat
```

The batch file automatically starts the correct JAR located in the same directory.

## User Data

User-specific data is stored separately from the application files.

Location:

```text
%LOCALAPPDATA%\PaardenWiskunde\
```

Example:

```text
%LOCALAPPDATA%\PaardenWiskunde\
└── horses.json
```

This data is preserved when the application is updated or redeployed.

## Application Data

Static, read-only application data is bundled inside the JAR as resources.

Example:

```text
src/main/resources/
└── materials.json
```

These files are not stored externally and cannot be modified by normal application usage.

## Deployment Workflow

The normal development workflow is:

```text
Modify code
     |
     v
./gradlew deploy
     |
     v
Start PaardenWiskunde from the Windows Start Menu
```

The previous installation is automatically updated with the new version.

## Troubleshooting

### The application cannot find JSON files

Make sure the application is started through the deployed `.bat` launcher instead of directly from IntelliJ or another working directory.

The application uses fixed paths inside `%LOCALAPPDATA%`.

### The new version is not running

Check that deployment completed successfully and that:

```text
%LOCALAPPDATA%\PaardenWiskunde\PaardenWiskunde.jar
```

has an updated modification date.

## Available Gradle Tasks

| Task                  | Description                                          |
| --------------------- | ---------------------------------------------------- |
| `./gradlew run`       | Run the application from the development environment |
| `./gradlew shadowJar` | Build a standalone executable JAR                    |
| `./gradlew deploy`    | Build and install the application locally            |

# PaardenWiskunde

PaardenWiskunde is a Kotlin command-line (TUI) application for managing Wynncraft horse data and determining the most optimal feeding plan.

## Running the application

The easiest way to use PaardenWiskunde is to download the latest Windows installer from the GitHub Releases page.

After installation, PaardenWiskunde can be started from the Windows Start menu.

Horse data is saved automatically between sessions.

## Development

The application can be run directly using Gradle:

```bash
./gradlew run
```

## Building the installer
```bash
./gradlew packageInstaller
```
The resulting ```.exe``` can be found in the ```build/installer``` directory.
Building the installer requires JDK 21 and WiX Toolset to be installed.
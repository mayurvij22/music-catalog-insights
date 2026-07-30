# Java Maven TestNG Calculator Project

A clean, template-ready Java Maven TestNG project designed to demonstrate basic calculator operations, structured unit testing, and continuous integration via GitHub Actions.

## Features
- **Java 21**: Utilizes modern Java standards.
- **Maven**: Dependency management and build lifecycle.
- **TestNG**: Powerful testing framework using annotations like `@BeforeMethod`, `@AfterMethod`, `@DataProvider`, and custom XML suites.
- **CI/CD Integrated**: Configured GitHub Actions workflow for automated testing and test report artifacts uploading.

---

## Project Structure

```text
java-testng-calculator/
├── .github/
│   └── workflows/
│       └── test.yml         # GitHub Actions workflow for automated builds & tests
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── calculator/
│   │                   └── Calculator.java       # Core Calculator implementation
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── calculator/
│                       └── CalculatorTest.java   # TestNG unit tests
├── testng.xml               # TestNG suite execution configuration
├── pom.xml                  # Maven build configuration
├── .gitignore               # Excluded paths for Git
└── README.md                # Documentation (this file)
```

---

## Prerequisites

Ensure you have the following installed on your system:
- **Java Development Kit (JDK) 21**
- **Apache Maven 3.8+**
- **Git** (optional, for version control)

---

## Getting Started

### 1. Clone the Project
If cloning from a remote repository:
```bash
git clone <repository-url>
cd java-testng-calculator
```

### 2. Build the Project
Compile the Java source code and download required dependencies:
```bash
mvn clean compile
```

### 3. Run the Tests
Execute the tests defined in the `testng.xml` file using Maven Surefire:
```bash
mvn clean test
```

---

## Test Execution and Reports

### Custom Test Suites
The tests are run via the `testng.xml` suite definition file. You can configure tests, suites, classes, and parameters inside it.

### TestNG Reports
After executing the tests, Maven Surefire & TestNG will generate HTML reports in the target folder:
- **HTML Report Index**: `target/surefire-reports/index.html`
- **Emailable Report**: `target/surefire-reports/emailable-report.html`

To view the reports, open `target/surefire-reports/index.html` in your web browser.

---

## GitHub Actions CI/CD Workflow
The project includes a pre-configured CI/CD workflow (`.github/workflows/test.yml`):
- Runs automatically on every **push** or **pull request**.
- Uses an **Ubuntu** runner.
- Installs **Java 21**.
- Caches Maven dependencies to speed up future runs.
- Executes `mvn clean test`.
- Uploads the Surefire test reports as a downloadable workflow artifact.

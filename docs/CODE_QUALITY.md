# Code Quality Guidelines

This document outlines the code quality standards and tools used in the CDK Common library project.

## Quality Tools Overview

| Tool | Purpose | Configuration |
|------|---------|---------------|
| **Checkstyle** | Code style enforcement | Google Java Style Guide (140 char limit) |
| **SpotBugs** | Static bug detection | `spotbugs-exclude.xml` |
| **PMD** | Code analysis | Security, performance, best practices |
| **JaCoCo** | Code coverage | Coverage reporting |
| **SonarCloud** | Continuous inspection | `sonar-project.properties` |
| **OWASP Dependency Check** | Security vulnerabilities | `dependency-check-suppressions.xml` |
| **Spotless** | Code formatting | Google Java Format |

## Running Quality Checks

### Quick Check (All Tools)

```bash
# Run all essential checks
./mvnw clean verify

# Or if you prefer standard Maven
mvn clean verify
```

### Individual Tools

#### Checkstyle
```bash
# Check code style
./mvnw checkstyle:check

# Generate HTML report
./mvnw checkstyle:checkstyle
open target/site/checkstyle.html
```

Common issues:
- Line length > 140 characters
- Missing JavaDoc for public methods
- Incorrect indentation (use 2 spaces)
- Unused imports
- Wrong order of imports

#### SpotBugs
```bash
# Run analysis
./mvnw spotbugs:check

# Generate report
./mvnw clean compile spotbugs:spotbugs
# View XML report at: target/spotbugsXml.xml

# Open GUI viewer
./mvnw spotbugs:gui
```

Common issues:
- Null pointer dereferences
- Resource leaks
- Security vulnerabilities
- Performance inefficiencies
- Incorrect synchronization

#### PMD
```bash
# Run analysis
./mvnw pmd:check

# Check for copy-paste
./mvnw pmd:cpd-check

# Generate reports
./mvnw pmd:pmd pmd:cpd
open target/site/pmd.html
open target/site/cpd.html
```

Common issues:
- Empty catch blocks
- Unused variables/parameters
- Overly complex methods
- Duplicate code blocks
- Missing braces

#### Code Coverage
```bash
# Run tests with coverage
./mvnw clean test jacoco:report

# View HTML report
open target/site/jacoco/index.html
```

Coverage targets:
- Aim for meaningful coverage rather than percentage goals
- Focus on critical business logic
- Exclude generated code from metrics

#### Dependency Analysis
```bash
# Check for unused dependencies
./mvnw dependency:analyze

# Check for security vulnerabilities
./mvnw dependency-check:check

# View vulnerability report
open reports/dependency-check-report.html

# Check for duplicate dependencies
./mvnw dependency:analyze-duplicate

# Check for available updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
```

#### Code Formatting
```bash
# Check formatting
./mvnw spotless:check

# Auto-format code
./mvnw spotless:apply
```

## Project Configuration

### Code Style Settings
- **Style Guide**: Google Java Style Guide
- **Line Length**: 140 characters maximum
- **Indentation**: 2 spaces for Java code
- **Continuation Indent**: 4 spaces
- **Java Version**: Java 21
- **Character Encoding**: UTF-8

### File-Specific Settings (via .editorconfig)
- **Java files**: 2-space indent, 140 char line limit
- **XML files**: 4-space indent (including pom.xml)
- **YAML files**: 2-space indent
- **Properties files**: Latin-1 encoding
- **All files**: LF line endings (except .bat/.cmd files)

## IDE Setup

### IntelliJ IDEA

1. **Import Code Style**:
   - Settings → Editor → Code Style → Java
   - Import scheme → Checkstyle Configuration
   - Select `checkstyle.xml` from project root

2. **Install Plugins**:
   - Checkstyle-IDEA
   - SpotBugs
   - SonarLint
   - EditorConfig

3. **Configure Checkstyle Plugin**:
   - Settings → Tools → Checkstyle
   - Add configuration file: `checkstyle.xml`
   - Set Checkstyle version to match Maven plugin

4. **Enable EditorConfig**:
   - Should be automatic with EditorConfig plugin
   - Ensures consistent formatting across team

### Visual Studio Code

1. **Install Extensions**:
   - Extension Pack for Java
   - Checkstyle for Java
   - SonarLint
   - EditorConfig for VS Code
   - Spotless Gradle (if applicable)

2. **Configure settings.json**:
```json
{
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  "java.checkstyle.configuration": "${workspaceFolder}/checkstyle.xml",
  "java.checkstyle.version": "10.21.0",
  "editor.rulers": [140],
  "files.trimTrailingWhitespace": true,
  "files.insertFinalNewline": true
}
```

## Pre-commit Checks

Before committing code, run these checks:

```bash
# Format code
./mvnw spotless:apply

# Run all quality checks
./mvnw clean verify

# Quick validation (compile + test)
./mvnw clean compile test
```

### Validation Script

Create a `validate.sh` script for comprehensive validation:

```bash
#!/bin/bash
set -e

echo "🔍 Running complete validation..."

echo "📦 Clean and compile..."
mvn clean compile -B

echo "🧪 Running tests with coverage..."
mvn test jacoco:report -B

echo "✅ Running Checkstyle..."
mvn checkstyle:check -B

echo "🔎 Running PMD..."
mvn pmd:check pmd:cpd-check -B

echo "🐛 Running SpotBugs..."
mvn spotbugs:check -B

echo "🔒 Running OWASP Dependency Check..."
mvn dependency-check:check -B -DskipTests

echo "📊 Analyzing dependencies..."
mvn dependency:analyze -B

echo "✨ All validations passed!"
```

## Continuous Integration

GitHub Actions automatically runs quality checks on:

### On Every Push
- Compilation and tests
- All code quality tools
- Security scanning
- Coverage analysis

### On Pull Requests
- All push checks plus:
- PR title validation
- Quality gates enforcement
- Coverage reporting to PR

### Weekly Scheduled
- Full dependency vulnerability scan
- Plugin update checks
- Dependency update analysis

See `.github/workflows/` for detailed configurations:
- `test-and-analyze.yml`: Main quality checks
- `pull-request-checks.yml`: PR-specific validations
- `dependency-management.yml`: Dependency updates
- `scheduled-maintenance.yml`: Weekly maintenance tasks

## Quality Metrics

### SonarCloud Dashboard
- Project: [tinstafl_cdk-common](https://sonarcloud.io/project/overview?id=tinstafl_cdk-common)
- Organization: tinstafl
- Quality Gate: Enabled

### Target Metrics

| Metric | Target | Notes |
|--------|--------|-------|
| Code Coverage | Context-dependent | Focus on critical paths |
| Technical Debt | < 5 days | Keep it manageable |
| Duplicated Lines | < 3% | Use abstraction |
| Cyclomatic Complexity | < 10 per method | Refactor complex methods |
| Cognitive Complexity | < 15 per method | Keep code readable |
| Security Hotspots | 0 | Address immediately |
| Bugs | 0 | Fix before merge |
| Code Smells | Minimize | Refactor regularly |

## Troubleshooting

### Common Issues and Solutions

**Checkstyle failures:**
```bash
# Auto-format to fix most issues
./mvnw spotless:apply

# For remaining issues, check:
# - Line length (140 chars max)
# - JavaDoc comments
# - Import order
```

**SpotBugs false positives:**
- Add exclusions to `spotbugs-exclude.xml`
- Use `@SuppressFBWarnings("RULE_NAME")` annotation sparingly
- Document why suppression is necessary

**PMD violations:**
- Refactor code to address issues
- For false positives: `@SuppressWarnings("PMD.RuleName")`
- Update PMD rules if needed

**Coverage issues:**
- Write unit tests for new code
- Focus on business logic coverage
- Exclude non-testable code (DTOs, configs)

**Dependency conflicts:**
```bash
# Display dependency tree
./mvnw dependency:tree

# Find duplicate classes
./mvnw dependency:analyze-duplicate
```

**OWASP false positives:**
- Add suppressions to `dependency-check-suppressions.xml`
- Include justification comments
- Regular review of suppressions

## Best Practices

1. **Run checks locally** before pushing
   - Minimum: `mvn clean compile test`
   - Preferred: `mvn clean verify`

2. **Fix issues immediately**
   - Don't accumulate technical debt
   - Address security issues first

3. **Document suppressions**
   - Explain why warnings are suppressed
   - Review suppressions regularly

4. **Keep dependencies updated**
   - Review Dependabot PRs promptly
   - Test thoroughly after updates

5. **Write tests first**
   - TDD helps maintain coverage
   - Tests document intended behavior

6. **Refactor regularly**
   - Keep complexity low
   - Extract methods/classes when needed

7. **Use formatting tools**
   - Run `spotless:apply` before commits
   - Configure IDE to match project style

## Getting Help

- **Build failures**: Check GitHub Actions logs
- **SonarCloud issues**: Review detailed reports on dashboard
- **Tool documentation**:
  - [Checkstyle](https://checkstyle.org/)
  - [SpotBugs](https://spotbugs.github.io/)
  - [PMD](https://pmd.github.io/)
  - [JaCoCo](https://www.jacoco.org/jacoco/)
- **Project specific**: See `LOCAL_VALIDATION.md` for detailed commands

## Related Documentation

- [`LOCAL_VALIDATION.md`](LOCAL_VALIDATION.md) - Detailed validation commands
- [`CHECKSTYLE_SETUP.md`](CHECKSTYLE_SETUP.md) - Checkstyle configuration guide
- [`CONTRIBUTING.md`](CONTRIBUTING.md) - Contribution guidelines
- [`COMMIT_CONVENTIONS.md`](COMMIT_CONVENTIONS.md) - Commit message standards
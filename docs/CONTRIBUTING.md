# Contributing to CDK Common

Thank you for your interest in contributing to CDK Common! We welcome
contributions from the community and are grateful for any help you can provide.

## Code of Conduct

Please note that this project is released with
a [Code of Conduct](CODE_OF_CONDUCT.md). By participating in this project you
agree to abide by its terms.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates.
When you create a bug report, include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples**
- **Include stack traces and logs**
- **Describe the behavior you observed and expected**
- **Include your environment details** (Java version, OS, etc.)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an
enhancement suggestion:

- **Use a clear and descriptive title**
- **Provide a detailed description of the suggested enhancement**
- **Provide specific examples to demonstrate the enhancement**
- **Describe the current behavior and expected behavior**
- **Explain why this enhancement would be useful**

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Follow the coding standards** (see below)
3. **Write tests** for your changes
4. **Ensure all tests pass** (`./mvnw test`)
5. **Update documentation** as needed
6. **Follow commit conventions** (see below)
7. **Submit your pull request**

## Development Setup

### Prerequisites

- Java 21+
- Maven 3.9.6+
- Git
- AWS CLI (configured)
- AWS CDK CLI

### Getting Started

```bash
# Clone your fork
git clone https://github.com/your-username/cdk-common.git
cd cdk-common

# Add upstream remote
git remote add upstream https://github.com/tinstafl/cdk-common.git

# Install dependencies
./mvnw clean install

# Run tests
./mvnw test
```

## Coding Standards

### Java Style Guide (Google Java Style)

This project follows
the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
with the following specifications:

#### Formatting

- **Indentation**: 2 spaces (no tabs)
- **Line length**: Maximum 100 characters
- **Braces**: Opening braces on same line, closing braces on new line
- **Line wrapping**: Break after operators, indent continuation lines by 4
  spaces

#### Naming Conventions

- **Packages**: lowercase, no underscores (`io.tinstafl.cdk.common`)
- **Classes/Interfaces**: UpperCamelCase
- **Methods**: lowerCamelCase starting with lowercase letter (
  `^[a-z][a-z0-9][a-zA-Z0-9_]*$`)
- **Variables/Parameters**: lowerCamelCase (`^[a-z]([a-z0-9][a-zA-Z0-9]*)?$`)
- **Constants**: UPPER_SNAKE_CASE
- **Type parameters**: Single capital letter or name ending with 'T' (`T`, `E`,
  `KeyT`)

#### Import Statements

- No wildcard imports (`import java.util.*` not allowed)
- Order: static imports first, then third-party packages
- Alphabetically sorted within groups
- Empty line between import groups

#### Whitespace

- Space after keywords (`if`, `for`, `while`, `catch`)
- Space around operators (`=`, `+`, `-`, `*`, `/`, etc.)
- No space before comma, semicolon, or method parameters
- Empty line between class members (fields, methods, constructors)

#### JavaDoc Requirements

- Required for all public classes and interfaces
- Required for public methods with 2+ lines of code
- Format: Summary sentence, blank line, then details
- Tag order: `@param`, `@return`, `@throws`, `@deprecated`

#### Other Requirements

- One statement per line
- Switch statements must have `default` case
- No empty catch blocks (must have comment or variable named "expected")
- Abbreviations discouraged in names (use `getUrl()` not `getURL()`)
- Overloaded methods must be grouped together

### Code Quality Tools

Before submitting, run these checks:

```bash
# Run Checkstyle (Google Java Style)
./mvnw checkstyle:check

# Run SpotBugs static analysis
./mvnw spotbugs:check

# Run PMD code analysis
./mvnw pmd:check

# Run all validations
./mvnw clean verify
```

### Suppressing Checkstyle Warnings

If you must suppress a Checkstyle warning:

```java
// Single line suppression
// CHECKSTYLE.SUPPRESS: RuleName
problematicCode();

// Block suppression
// CHECKSTYLE.OFF: RuleName
multiple lines of code
that need suppression
// CHECKSTYLE.ON: RuleName
```

## Commit Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

[optional body]

[optional footer(s)]
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation only
- **style**: Code style changes
- **refactor**: Code refactoring
- **perf**: Performance improvements
- **test**: Adding or updating tests
- **build**: Build system changes
- **ci**: CI configuration changes
- **chore**: Other changes

### Examples

```bash
feat(eks): add support for Karpenter autoscaling
fix(lambda): correct IAM permission for S3 access
docs: update README with new construct examples
test(vpc): add unit tests for security groups
```

## Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ClassName

# Run with coverage
./mvnw clean test jacoco:report
```

### Writing Tests

- Write unit tests for all new functionality
- Maintain test coverage above 70%
- Use meaningful test names that describe the scenario
- Follow the Arrange-Act-Assert pattern

## Documentation

- Update README.md if adding new features
- Add JavaDoc comments for public APIs
- Update relevant documentation in the `docs/` directory
- Include examples for new constructs

## Review Process

1. **Automated checks** run on all PRs
2. **Code review** by maintainers
3. **Testing** in multiple environments
4. **Documentation review**
5. **Final approval and merge**

## Release Process

Releases are managed by maintainers:

1. Version bump in `pom.xml`
2. Update CHANGELOG.md
3. Create git tag
4. GitHub Actions publishes release

## Questions?

- Open a [GitHub Discussion](https://github.com/tinstafl/cdk-common/discussions)
- Check existing [Issues](https://github.com/tinstafl/cdk-common/issues)
- Review the [Documentation](docs/)

## Recognition

Contributors are recognized in:

- CHANGELOG.md for each release
- GitHub contributors page
- Project documentation

Thank you for contributing to CDK Common! 🎉

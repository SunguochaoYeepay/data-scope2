# DataScope - Contribution Guidelines

## Overview

Thank you for your interest in contributing to the DataScope project! This document provides guidelines and instructions for contributing to the project. By following these guidelines, you can help ensure that your contributions are effectively integrated into the project.

## Code of Conduct

All contributors are expected to adhere to the project's Code of Conduct. We are committed to providing a welcoming and inclusive environment for all contributors regardless of background or identity.

Key principles:
- Be respectful and inclusive
- Exercise empathy and kindness
- Provide and gracefully accept constructive feedback
- Focus on what is best for the community
- Show courtesy and respect in all communications

## Getting Started

### Prerequisites

Before you begin contributing, ensure you have:

1. A GitHub account
2. Git installed on your local machine
3. JDK 17 or higher installed
4. Maven 3.8+ installed
5. MySQL 8.0+ installed (for local development)
6. Redis 6.0+ installed (for local development)

### Setting Up the Development Environment

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/your-username/data-scope.git
   cd data-scope
   ```
3. Add the original repository as an upstream remote:
   ```bash
   git remote add upstream https://github.com/original-org/data-scope.git
   ```
4. Create a database for local development:
   ```sql
   CREATE DATABASE datascope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'datascope'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON datascope.* TO 'datascope'@'localhost';
   FLUSH PRIVILEGES;
   ```
5. Configure your local application properties in `main/src/main/resources/application-dev.yml`
6. Build the project:
   ```bash
   mvn clean install
   ```

## Development Workflow

### Branching Strategy

We follow a feature branch workflow:

1. Ensure your main branch is up to date:
   ```bash
   git checkout main
   git pull upstream main
   ```
2. Create a new branch for your feature or bugfix:
   ```bash
   git checkout -b feature/your-feature-name
   ```
   or
   ```bash
   git checkout -b fix/issue-description
   ```
3. Make your changes on this branch
4. Regularly commit your changes with clear, descriptive commit messages

### Commit Message Guidelines

Good commit messages help with project maintenance and understanding changes. Please follow these guidelines:

- Use the imperative mood ("Add feature" not "Added feature")
- First line should be 50 characters or less
- Start with a capital letter
- Do not end with a period
- Separate subject from body with a blank line
- Use the body to explain what and why, not how

Example:
```
Add relationship inference engine

Implement the relationship inference engine that automatically detects
relationships between tables based on naming conventions and data patterns.
This helps users discover connections in their data without manual configuration.

Resolves: #123
```

### Pull Request Process

1. Update your branch with the latest changes from main:
   ```bash
   git checkout main
   git pull upstream main
   git checkout your-branch-name
   git rebase main
   ```
2. Resolve any conflicts that arise during the rebase
3. Push your branch to your fork:
   ```bash
   git push origin your-branch-name
   ```
4. Create a Pull Request (PR) from your branch to the main repository's main branch
5. Fill in the PR template with all required information
6. Request a review from appropriate team members
7. Address any feedback or requested changes
8. Once approved, your PR will be merged by a maintainer

## Coding Standards

### Java Code Style

- Follow standard Java naming conventions
- Use 4 spaces for indentation (not tabs)
- Maximum line length of 120 characters
- Use meaningful names for classes, methods, and variables
- Add Javadoc comments for all public classes and methods
- Follow the principle of least surprise
- Keep methods small and focused on a single responsibility
- Avoid unnecessary comments (code should be self-explanatory)

### Domain-Driven Design Principles

- Place code in the appropriate module based on DDD layers:
  - Domain: Core business logic and entities
  - Application: Orchestration of domain operations
  - Infrastructure: Technical implementations
  - Facade: API controllers and DTOs
- Use value objects for concepts with no identity
- Implement rich domain models with behavior
- Use repository interfaces in the domain layer
- Keep the domain layer free of infrastructure concerns

### Testing Standards

- Write unit tests for all business logic
- Use integration tests for repository implementations
- Name tests clearly to indicate what they're testing
- Follow the Arrange-Act-Assert pattern
- Keep tests independent of each other
- Mock external dependencies
- Aim for high code coverage, but prioritize test quality over quantity

## Documentation

### Code Documentation

- Add Javadoc comments to all public classes and methods
- Document non-obvious behavior
- Update existing documentation when changing code
- Use `@param`, `@return`, and `@throws` tags appropriately

### Project Documentation

When adding new features or making significant changes, update the relevant documentation:

- Update README.md if necessary
- Add or update documentation in the docs directory
- Update API documentation if changing or adding endpoints
- Add examples for new features

## Review Process

### Code Review Guidelines

When reviewing code, focus on:

1. **Correctness**: Does the code work as intended?
2. **Design**: Is the code well-designed and appropriate for the system?
3. **Complexity**: Could the code be made simpler?
4. **Tests**: Are there appropriate tests, and do they cover the changes?
5. **Naming**: Are names clear and consistent?
6. **Comments**: Are comments clear and useful?
7. **Style**: Does the code follow our style guidelines?
8. **Documentation**: Is the documentation updated?

### Responding to Reviews

- Respond to all comments
- Be open to feedback and suggestions
- Explain your reasoning if you disagree with a suggestion
- Make requested changes or explain why they shouldn't be made
- Thank reviewers for their time and feedback

## Issue Reporting

### Bug Reports

When reporting a bug, include:

1. A clear, descriptive title
2. Steps to reproduce the issue
3. Expected behavior
4. Actual behavior
5. Screenshots or logs if applicable
6. Environment information (OS, Java version, etc.)
7. Any additional context

### Feature Requests

When requesting a feature, include:

1. A clear, descriptive title
2. A detailed description of the proposed feature
3. The problem the feature would solve
4. Any alternatives you've considered
5. Any additional context

## Release Process

### Version Numbering

We follow semantic versioning (MAJOR.MINOR.PATCH):

- MAJOR: Incompatible API changes
- MINOR: Backwards-compatible functionality additions
- PATCH: Backwards-compatible bug fixes

### Release Checklist

Before a release:

1. Ensure all tests pass
2. Update version numbers
3. Update CHANGELOG.md
4. Create release notes
5. Tag the release in Git
6. Build and publish artifacts

## Continuous Integration

We use CI/CD pipelines to automate testing and deployment:

- All commits trigger a build and test run
- Pull requests must pass all tests before merging
- Code quality checks are run automatically
- Security scans are performed on dependencies

## License and Legal

### Contributor License Agreement

By contributing to this project, you agree that your contributions will be licensed under the project's license.

### Third-Party Code

When including third-party code:

1. Ensure it has a compatible license
2. Document the source and license in your PR
3. Include any required attribution notices
4. Update the project's dependencies list

## Getting Help

If you need help with contributing:

- Check the project documentation
- Ask questions in the project's discussion forum
- Reach out to project maintainers
- Join the project's communication channels

## Acknowledgements

We appreciate all contributions to the DataScope project, whether they're bug reports, feature requests, documentation improvements, or code contributions. Thank you for helping make this project better!

---

These guidelines are adapted from best practices in open-source software development and are designed to make the contribution process smooth and effective for everyone involved.
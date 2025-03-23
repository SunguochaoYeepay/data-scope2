# Contributing to DataScope

## Welcome!

Thank you for considering contributing to DataScope! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct. Please read [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) before contributing.

## How Can I Contribute?

### Reporting Bugs

1. **Check Existing Issues** - Search the issue tracker to avoid duplicates.

2. **Create a Bug Report** including:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - System information
   - Screenshots if applicable

3. **Use the Bug Report Template**:
```markdown
### Bug Description
[Describe the bug]

### Steps to Reproduce
1. [First Step]
2. [Second Step]
3. [And so on...]

### Expected Behavior
[What should happen]

### Actual Behavior
[What actually happens]

### System Information
- OS: [e.g., Ubuntu 22.04]
- Java Version: [e.g., 17.0.2]
- DataScope Version: [e.g., 1.0.0]
```

### Suggesting Enhancements

1. **Check Existing Suggestions** - Search for similar ideas.

2. **Create a Feature Request** including:
   - Use case description
   - Expected benefits
   - Possible implementation approach
   - Alternative solutions considered

3. **Use the Feature Request Template**:
```markdown
### Feature Description
[Describe the feature]

### Use Case
[Explain when and why this would be useful]

### Proposed Solution
[Describe your suggested implementation]

### Alternatives Considered
[List other approaches you've considered]
```

## Development Process

### Setting Up Development Environment

1. **Fork and Clone**:
```bash
git clone https://github.com/yourusername/data-scope.git
cd data-scope
```

2. **Install Dependencies**:
```bash
./mvnw clean install
```

3. **Configure Development Environment**:
```bash
cp data-scope-app/src/main/resources/application.example.yml \
   data-scope-app/src/main/resources/application.yml
```

### Making Changes

1. **Create a Branch**:
```bash
git checkout -b feature/your-feature-name
```

2. **Code Style**:
- Follow Java code conventions
- Use meaningful variable names
- Add comments for complex logic
- Include unit tests
- Update documentation

3. **Commit Guidelines**:
```
type(scope): description

[optional body]

[optional footer]
```

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Formatting
- refactor: Code restructuring
- test: Adding tests
- chore: Maintenance

Example:
```
feat(query): add natural language processing support

Implement OpenRouter API integration for converting
natural language to SQL queries.

Closes #123
```

### Testing

1. **Run Unit Tests**:
```bash
./mvnw test
```

2. **Run Integration Tests**:
```bash
./mvnw verify
```

3. **Check Code Style**:
```bash
./mvnw checkstyle:check
```

### Submitting Changes

1. **Push Changes**:
```bash
git push origin feature/your-feature-name
```

2. **Create Pull Request**:
- Use clear title and description
- Reference related issues
- Include test results
- Add screenshots if applicable

3. **Review Process**:
- Address review comments
- Update documentation
- Ensure CI passes
- Get approval from maintainers

## Documentation

### Code Documentation
- Add JavaDoc comments
- Document public APIs
- Include usage examples
- Explain complex algorithms

### Technical Documentation
- Update README.md
- Add architecture diagrams
- Document configuration
- Include deployment guides

### User Documentation
- Update user guides
- Add feature descriptions
- Include screenshots
- Provide examples

## Community

### Communication Channels
- GitHub Issues
- Discussion Forum
- Slack Channel
- Mailing List

### Getting Help
- Check documentation
- Search existing issues
- Ask in discussions
- Contact maintainers

## Project Structure

```
data-scope/
├── data-scope-app/        # Application layer
├── data-scope-domain/     # Domain layer
├── data-scope-facade/     # API layer
├── data-scope-infrastructure/  # Infrastructure layer
├── docs/                  # Documentation
└── ui/                    # UI templates
```

## License

By contributing to DataScope, you agree that your contributions will be licensed under the MIT License. See [LICENSE](LICENSE) for details.
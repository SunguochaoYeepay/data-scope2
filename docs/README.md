# DataScope Documentation

## Overview

This directory contains all documentation for the DataScope project. The documentation is organized into several
categories to make it easier to find relevant information.

## Documentation Structure

- **[index.md](index.md)**: Main documentation index in English
- **[文档索引.md](文档索引.md)**: Main documentation index in Chinese
- **Core Documentation**: Architecture, database design, API design, etc.
- **Development Guidelines**: Coding standards, contributing guidelines, testing strategy, etc.
- **Integration and Deployment**: Deployment guide, low-code integration, etc.
- **Reference Materials**: Glossary, Maven modules, domain model, etc.

## Documentation Standards

When creating or updating documentation, please follow these standards:

1. **File Naming**: Use snake_case for English documentation (e.g., `api_design.md`) and descriptive Chinese names for
   Chinese documentation (e.g., `系统架构.md`).
2. **Document Structure**: Each document should have a clear structure with headings, subheadings, and concise content.
3. **Cross-References**: Use relative links to reference other documents (e.g., `[API Design](api_design.md)`).
4. **Code Examples**: Include code examples where appropriate, using proper syntax highlighting.
5. **Images**: Store images in an `images` subdirectory and reference them using relative paths.

## Handling Duplicate Documents

There are currently some duplicate documents with different naming conventions. Please use the canonical paths listed in
the [index.md](index.md) file. The duplicates will be gradually consolidated to reduce confusion.

## Documentation Maintenance

All documentation should be kept up-to-date as the project evolves. When making significant changes to the codebase,
ensure that the relevant documentation is updated accordingly. If you find any inconsistencies or outdated information,
please create an issue or submit a pull request with the necessary updates.

## Language Considerations

The project documentation is available in both English and Chinese:

- English documentation is the primary technical reference
- Chinese documentation provides localized context and user-facing information

When updating documentation, consider whether the changes need to be reflected in both languages.

## Contributing to Documentation

To contribute to the documentation:

1. Fork the repository
2. Make your changes
3. Submit a pull request with a clear description of the changes

Please ensure your contributions follow the documentation standards outlined above.

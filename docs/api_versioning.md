# API Versioning Guidelines

## Overview
This document outlines the API versioning strategy for the DataScope system to ensure backward compatibility and smooth evolution of APIs.

## Version Format
- API versions are specified in the URL path
- Format: `/api/v{major}/{resource}`
- Example: `/api/v1/datasources`

## Versioning Rules

### When to Create a New Version
1. Breaking Changes:
   - Removing or renaming fields in request/response
   - Changing field types
   - Removing endpoints
   - Changing URL structure
   - Modifying authentication requirements
   - Changing error response format

2. Non-Breaking Changes (No Version Change):
   - Adding new optional fields
   - Adding new endpoints
   - Adding new query parameters
   - Extending enums with new values
   - Adding new response fields
   - Performance improvements

### Version Lifecycle

1. Active Development (v1)
   - Current stable version
   - Receives new features and bug fixes
   - Fully supported and documented

2. Maintenance Mode (Previous Version)
   - Only receives critical bug fixes
   - No new features
   - Supported for 12 months after new version release

3. Deprecated
   - No updates
   - Continues to work but may be removed
   - 6 months notice before removal

4. Sunset
   - API version is removed
   - Returns 410 Gone status
   - Clients must upgrade

## Implementation Guidelines

### URL Structure
```
/api/v1/datasources                    # List/Create datasources
/api/v1/datasources/{id}              # Get/Update/Delete datasource
/api/v1/datasources/{id}/activate     # Activate datasource
/api/v1/datasources/{id}/deactivate   # Deactivate datasource
```

### Request/Response Headers
```
Accept: application/json
Content-Type: application/json
X-API-Version: 1                      # Optional header for version
```

### Version Documentation
- Each version has separate OpenAPI/Swagger documentation
- Documentation includes version status (active/maintenance/deprecated)
- Migration guides between versions
- Change logs for each version

### Error Handling
```json
{
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": [...],
    "apiVersion": "1"
}
```

## Migration Strategy

### For API Providers
1. Maintain multiple versions simultaneously
2. Implement version routing middleware
3. Use separate controllers for different versions
4. Share common business logic
5. Monitor version usage
6. Communicate deprecation plans

### For API Consumers
1. Use latest stable version for new integrations
2. Plan migrations before versions are deprecated
3. Test thoroughly in non-production environments
4. Monitor deprecation notices
5. Update client libraries

## Low Code Platform Integration

### Version Mapping
- Low code platform configurations include API version
- Separate configurations for each version
- Automatic detection of version compatibility

### Configuration Example
```json
{
    "apiVersion": "1",
    "endpoint": "/api/v1/datasources",
    "displayConfig": {
        "queryForm": {...},
        "resultTable": {...}
    },
    "compatibility": {
        "minVersion": "1.0.0",
        "maxVersion": "1.9.9"
    }
}
```

## Monitoring and Metrics

### Version Usage
- Track requests per version
- Monitor error rates by version
- Identify clients using deprecated versions
- Alert on version-specific issues

### Health Checks
- Version-specific health endpoints
- Dependency status per version
- Performance metrics by version

## Communication

### Documentation
- Clear version support policy
- Migration guides between versions
- Version-specific API references
- Known issues and workarounds

### Notifications
- Email notifications for deprecation
- Release notes for new versions
- Security advisories
- Breaking change alerts

## Testing

### Version Compatibility
- Test suite for each version
- Cross-version integration tests
- Backward compatibility checks
- Performance benchmarks

### Automation
- Automated version compatibility tests
- CI/CD pipeline for each version
- Automated documentation generation
- API contract testing
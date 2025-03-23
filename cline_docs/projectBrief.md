## Project Brief

DataScope is a comprehensive data management and query system with the following features:

### Data Source Management

- Seamless integration of different database systems (MySQL, DB2) as data sources
- Automated extraction and persistent storage of comprehensive metadata
- Robust, configurable synchronization mechanism to maintain data consistency

### Intelligent Data Discovery and Query

- Intuitive interface for exploring available schemas, tables, and columns
- Support for data retrieval through SQL queries and natural language descriptions
- Optimization loop allowing users to iteratively refine natural language queries
- Machine learning to infer and learn table relationships from historical query patterns
- Manual definition and maintenance of relationships

### Low-Code Integration and Rapid Application Development

- Synchronization of query results, associated SQL, and page configuration
- Simplified application development through tools for defining query conditions and results presentation
- AI-assisted enhancement of the configuration process

### Technical Stack and Constraints

- Java, Maven, SpringBoot, MyBatis, MySQL, Redis
- Internal information management system for employees
- Maximum 100 data sources (primarily MySQL and DB2)
- Each data source has up to 100 tables with data volumes ranging from thousands to hundreds of millions
- Integration with a specific low-code platform via JSON-based interaction protocol
- Local deployment
- Password security with salt and AES encryption
- Separate system aspect for access control
- Easy extension for more data source types
- Support for various data presentation forms (query forms, view pages, chart displays)
- Natural language query functionality using OpenRouter's LLM interface

### Additional Requirements

- Advanced features like automatic foreign key recognition or relationship inference through data analysis
- AI-assisted functionality
- Query history and favorites
- Unified API for data querying
- Versioned SQL and API
- Detailed LowCodeConfig design for interaction with low-code platforms
- HTML + Tailwind CSS for prototype interfaces with FontAwesome
- User stories documentation
- Intelligent inference of table relationships
- Display attribute settings with sensitive data masking
- Configurable operation columns
- User-specific display attribute settings
- Query condition configuration with required conditions
- Automatic hiding of infrequently used conditions
- Database column data type to UI element mapping
- API timeout of 30 seconds
- Reasonable query frequency limits
- Data download functionality (CSV format, max 50,000 rows)

## Technical Context

### Technologies used?

* Java
* Maven
* SpringBoot
* MyBatis
* MySQL
* Redis
* OpenRouter (for LLM integration)
* HTML
* Tailwind CSS
* FontAwesome (or other open-source UI component library)

### Development setup?

The project uses Maven for dependency management and building. The development environment should include:

* JDK 17 or later
* Maven 3.6 or later
* MySQL server
* Redis server
* A suitable IDE (e.g., IntelliJ IDEA, Eclipse)

### Technical constraints?

* The system should not synchronize data from the data sources, only metadata.
* Password should be encrypted with salt and AES.
* Access control is handled separately.
* The system should be easily extensible to support more data source types.
* The system should provide a unified API for querying data.
* SQL and API should be versioned.
* API interfaces should be designed with low-code platform integration in mind.
* Each API interface should have a default timeout of 30 seconds.
* User query frequency should be limited.
* Data download should be limited to 50000 rows.
* Data masking should be applied to sensitive information according to configured rules.
* Query execution should be tracked and managed with proper status handling.
* Natural language queries should be converted to SQL using OpenRouter's LLM interface.

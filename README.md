# Exchange Rate Challenge

## Create an API that fetches exchange-rates from one or more publicly available APIs and uses them for conversion calculations.
### 1. Data source
   - Fetch data from this publicly available API, https://exchangerate.host
   - Any additional datasource you may want to add (optional)
### 2. API operations
   - Get exchange rate from Currency A to Currency B
   - Get all exchange rates from Currency A
   - Get value conversion from Currency A to Currency B
   - Get value conversion from Currency A to a list of supplied currencies
### 3. Documentation
   - Use Swagger to provide documentation and a testing interface for the API, like in this example: https://petstore.swagger.io/

### Extra Credits
1. Introduce a mechanism to do as little calls as possible to the external provider, while
   still providing meaningful/valid data. Assume that the clients don’t require real-time
   data and that they are fine to receive data with up to 1 min of delay.
2. Add unit tests to cover your code.
   
### Delivery
   - You should use Java and the Spring framework.
   - Host the project in a repository in your favourite Git provider: GitHub, GitLab or Bitbucket - and commit frequently.
   - The project should have a README that explains how to run it.

### Tools used
   - Intellij IDEA 2022.2.3
   - Java version: 17.0.1,
   - OS: Ubuntu 22.04
   - Apache Maven 3.8.5

### How to run?
1. Run as Spring Boot application
   - configure the application as maven project in favourite idea and run it
2. Use maven command line
   - mvn spring-boot:run

- Postman collection: https://www.getpostman.com/collections/77ad0d6a0e13de9cb978 
- Notes: Enable annotation processors from IDEA if you've chosen option 1

### Solution  
   - Spring Boot Webflux API was implemented and the full application flow is performed non-blocking
   - For retrieving exchange rates following 3rd party client has been used: https://api.exchangerate.host/latest 
   - Consuming downstream dependencies was done via WebClient 
   - API is documented via swagger and its models and API specifications are generated using openapi generator
   - After retrieving data successfully from 3rd party, it is stored in cache with ttl of 1 minute using default Spring concurrent HashMap
   - Cache eviction is done using a scheduler once at every minute

### Technical debts / improvements to be done
   - Integration tests are not working properly. A mock for stubbing application WebClient requests is needed to be added
   - Unit tests are not complete from a coverage perspective
   - A second downstream dependency for retrieving exchange rates should be added as a fallback using resilience patterns (CircuitBreaker)
   - Client input data validation and integrity is not handled
   - Maybe Docker? Kafka Streams?
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
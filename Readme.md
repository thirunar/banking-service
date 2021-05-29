# Fake Banking Service

## Use case
APIs for creating Accounts and making transactions

## Tools & Libraries
- Java
- Spring Boot
- Spring JPA
- Postgres  
- Maven
- Liquibase 
- Docker (TestContainers)
- Github actions


## Dev Notes

### Running Tests
* Install `maven`
```java_holder_method_tree
mvn clean test
```
### Running application
* Install `maven`
```java_holder_method_tree
mvn spring-boot:run
```
## Implementation
* The account and transaction details are stored in the postgres database
* When an account is created, user can either give the balance or a default balance is assigned
* When a transaction is initiated, account balances are updated and entry is added to `transaction` table which acts as a ledger
* To handle concurrent transactions, optimistic locking is used

## CI
[Here](https://github.com/thirunar/banking-service/actions)

## Postman collection
[Here](https://www.getpostman.com/collections/80d462edf3efff477616)

## API Documentation
[Here](http://localhost:8080/swagger-ui.html)

## Project Board
[Here](https://github.com/thirunar/banking-service/projects/1)


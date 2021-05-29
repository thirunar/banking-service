# Fake Banking Service

## Use case
APIs for creating Accounts and making transaction

## Tools & Libraries
- Spring Boot
- Spring JPA
- Postgres  
- Maven
- Docker (TestContainers)


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
* When the account is been created, the user can either give the balance or the default value will be assigned
* When the transaction is initiated, the account balances are updated and added `transaction` table acts as a ledger
* To handle concurrent transaction and also to avoid concurrency issues, optimistic locking is used when the transaction is done

## Postman collection
[Here](https://www.getpostman.com/collections/80d462edf3efff477616)

## API Documentation
[Here](http://localhost:8080/swagger-ui.html)

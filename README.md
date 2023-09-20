This project has 4 microservices:
Order-Microservice - contains CRUD operations for orders
Custimer-Microservice - contains CRUD operations for customers
Product-Microservice - contains CRUD operations for products
Event-Consumer-Service - It has kafka broker for interactions between microservices
 
The DB layer is MongoDB
 
In Order-Microservice, multithreading is implemented to handle mutiple orders using TaskExecutor.
The task executor's configuration can be changed from application.yml file as needed.
 
In order to run the project:
1. all the microservices needs to be started separately
2. MongoDB needs to be setup
3. Kafka server needs to be started

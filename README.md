# Aplicação Spring Boot com Apache Camel

Foi criado dois microserviços com Spring Boot + Apache Camel com o objetivo de testar os recursos da ferramenta.

Para gerenciamento de Queues, foi utilizado ActiveMQ.

Para mensageria, foi utilizado Kafka.

Projeto feito da prática do curso de Apache Camel da in28Minutes.

## Docker
* docker run -p 61616:61616 -p 8161:8161 rmohr/activemq
* docker-compose up
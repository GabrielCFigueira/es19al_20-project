# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/prototype-2019.svg?token=xDPBAaQ2epnFt9PRstYY&branch=master)](https://travis-ci.com/tecnico-softeng/prototype-2019)[![codecov](https://codecov.io/gh/tecnico-softeng/prototype-2019/branch/master/graph/badge.svg?token=bB74DA0VHo)](https://codecov.io/gh/tecnico-softeng/prototype-2019)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) |                     PRs id (with link)                         |        Date        |  
| ---------- | ----------------------- | ----------------------- | -------------------------------------------------------------- | ------------------ |
|    128     |      Pedro Carvalho     |      PedroMatias98      |  https://github.com/tecnico-softeng/es19al_20-project/pull/134 |                    |
|            |                         |                         |                                                                |                    |
|            |                         |                         |                                                                |                    |
|            |                         |                         |                                                                |                    |
|            |                         |                         |                                                                |                    |
|            |                         |                         |                                                                |                    |
|            |                         |                         |                                                                |                    |


### Infrastructure

This project includes the persistent layer, as offered by the FénixFramework.
This part of the project requires to create databases in mysql as defined in `resources/fenix-framework.properties` of each module.

See the lab about the FénixFramework for further details.

#### Docker (Alternative to installing Mysql in your machine)

To use a containerized version of mysql, follow these stesp:

```
docker-compose -f local.dev.yml up -d
docker exec -it mysql sh
```

Once logged into the container, enter the mysql interactive console

```
mysql --password
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context

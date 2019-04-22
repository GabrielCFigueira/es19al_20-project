# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_20-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_20-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_20-project/branch/develop/graph/badge.svg?token=79nNutGvkY)](https://codecov.io/gh/tecnico-softeng/es19al_20-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username)                 | PRs id (with link)                                            | Date               |
| ---------- | ----------------------- | --------------------------------------- | ------------------------------------------------------------- | ------------------ |
| 126        | 86426/87650             | GabrielCFigueira/DiogoEusebio           | https://github.com/tecnico-softeng/es19al_20-project/pull/132 | 15/04/2019         |
| 127        | 86410                   | DiogoFariaFernandes                     | https://github.com/tecnico-softeng/es19al_20-project/pull/133 | 15/04/2019         |
| 128        | 86499                   | PedroMatias98                           | https://github.com/tecnico-softeng/es19al_20-project/pull/134 | 15/04/2019         |
| 129        | 86461/86431/86451       | LivioCosta/HenriqueFSilva/JoaoPmargaco  | https://github.com/tecnico-softeng/es19al_20-project/pull/136 | 15/04/2019         |
| 130        | 86451                   | JoaoPmargaco                            | https://github.com/tecnico-softeng/es19al_20-project/pull/135 | 15/04/2019         |
| 131        | 86426                   | GabrielCFigueira                        | https://github.com/tecnico-softeng/es19al_20-project/pull/139 | 15/04/2019         |
| 141        | 86410/86499             | DiogoFariaFernandes/PedroMatias98       | https://github.com/tecnico-softeng/es19al_20-project/pull/142 | 20/04/2019         |
| 143        | 86461/86431/86451       | LivioCosta/HenriqueFSilva/JoaoPmargaco  | https://github.com/tecnico-softeng/es19al_20-project/pull/144 | 21/04/2019         |
| 145        | 86426                   | GabrielCFigueira                        | https://github.com/tecnico-softeng/es19al_20-project/pull/146 | 21/04/2019         |
| 147        | 86451/86431             | JoaoPmargaco/PedroMatias98              | https://github.com/tecnico-softeng/es19al_20-project/pull/148 | 21/04/2019         |
| 149        | 86426                   | GabrielCFigueira                        | https://github.com/tecnico-softeng/es19al_20-project/pull/150 | 22/04/2019         |


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

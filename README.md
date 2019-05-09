# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_20-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_20-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_20-project/branch/develop/graph/badge.svg?token=79nNutGvkY)](https://codecov.io/gh/tecnico-softeng/es19al_20-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)                                            | Date (dd/mm/yyyy)  |  
| ---------- | ----------------------- | ----------------------- | ------------------------------------------------------------- | ------------------ |
| 153        | ist187650               | DiogoEusebio            | https://github.com/tecnico-softeng/es19al_20-project/pull/162 | 03/05/2019         |
| 152        | ist186410               | DiogoFariaFernandes     | https://github.com/tecnico-softeng/es19al_20-project/pull/163 | 03/05/2019         |
| 160        | ist186431               | HenriqueFSilva          | https://github.com/tecnico-softeng/es19al_20-project/pull/165 | 04/05/2019         |
| 154        | ist186499               | PedroMatias98           | https://github.com/tecnico-softeng/es19al_20-project/pull/170 | 04/05/2019         |  
| 157        | ist186451               | JoaoPmargaco            | https://github.com/tecnico-softeng/es19al_20-project/pull/167 | 04/05/2019         |
| 159        | ist186410               | DiogoFariaFernandes     | https://github.com/tecnico-softeng/es19al_20-project/pull/169 | 04/05/2019         |
| 158        | ist186426               | GabrielCFigueira        | https://github.com/tecnico-softeng/es19al_20-project/pull/168 | 04/05/2019         |
| 156        | ist186461               | LivioCosta              | https://github.com/tecnico-softeng/es19al_20-project/pull/166 | 04/05/2019         |
| 155        | ist186499               | PedroMatias98           | https://github.com/tecnico-softeng/es19al_20-project/pull/184 | 05/05/2019         |
| 173        | ist186431               | HenriqueFSilva          | https://github.com/tecnico-softeng/es19al_20-project/pull/196 | 09/05/2019         |
| 157        | ist186451               | JoaoPmargaco            | https://github.com/tecnico-softeng/es19al_20-project/pull/185 | 09/05/2019         |
| 179        | ist186499               | PedroMatias98           | https://github.com/tecnico-softeng/es19al_20-project/pull/193 | 09/05/2019         |


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

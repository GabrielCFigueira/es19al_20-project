# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_20-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_20-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_20-project/branch/develop/graph/badge.svg?token=79nNutGvkY)](https://codecov.io/gh/tecnico-softeng/es19al_20-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


|   Number   |          Name           |                    Email                   |   GitHub Username  | Group |
| ---------- | ----------------------- | ---------------------------------------    | -------------------| ----- |
|   86461    |    Lívio Costa          |   livio.cos@gmail.com                      |    LivioCosta      |   20  |
|   86426    |    Gabriel Figueira     |   gabriel.figueira@tecnico.ulisboa.pt      |  GabrielCFigueira  |   20  |
|   86451    |    João Margaço         |   joao.margaco@tecnico.ulisboa.pt          |    JoaoPmargaco    |   20  |
|   87650    |    Diogo Eusébio        |   deusebio98@outlook.pt                    |    DiogoEusebio    |   20  |
|   86431    |    Henrique Silva       |   henrique.fs.1998@gmail.com               |   HenriqueFSilva   |   20  |
|   86499    |    Pedro Carvalho       |   pedro.matias.carvalho@tecnico.ulisboa.pt |    PedroMatias98   |   20  |
|   86410    |    Diogo Faria Fernandes|   diogo7ff@gmail.com                       | DiogoFariaFernandes|   20  |

- **Group 1:**
- **Group 2:**

**Sprint One**

| Name/Number/GitHub Username   |                         Files                                       |
| ----------------------------- | ------------------------------------------------------------------- |
|                               | tax.domain.BuyerConstructorTest 			                          |
|                               | tax.domain.SellerConstructorTest                                    |
|  Lívio Costa                  | activity.domain.ActivityMatchAgeMethodTest		                  |
|  86461                        | activity.domain.ActivityOfferMatchDateMethodTest	                  |
|  LivioCosta                   | broker.domain.BrokerConstructorMethodTest		                      |
|                               | bank.domain.AccountWithdrawMethodTest		                          |
|                               | bank.domain.ClientContructorMethodTest		                      |
|                               | tax.domain.RollbackTestAbstractClass                                |
|###############################|################################################|
|                               | hotel.domain.HotelGetPriceMethodTest 			                      |
|                               | hotel.domain.RoomReserveMethodTest                                  |
|  Gabriel Figueira             | tax.domain.IRSGetItemTypeByNameTest		                          |
|  86426                        | tax.domain.TaxPayerGetInvoiceByReferenceTest	                      |
|  GabrielCFigueira/Grabiel14   | activity.domain.ActivityConstructorMethodTest		                  |
|                               | activity.domain.ActivityProviderFindOfferMethodTest	              |
|                               | bank.domain.BankPersistentTest		                              |
|                               | bank.services.local.BankInterfaceCancelPaymentTest                  |
|###############################|################################################|
|                               | hotel.domain.BookingConstructorTest		                          |
|                               | hotel.domain.RoomConstructorMethodTest                              |
|  João Margaço                 | tax.domain.BuyerToReturnTest              		                  |
|  86451                        | tax.domain.SellerToPayTest                    	                  |
|  JoaoPmargaco                 | activity.domain.ActivityPersistenceTest		                      |
|                               | broker.domain.BrokerPersistenceTest		                          |
|                               | bank.domain.OperationRevertMethodTest 		                      |
|                               | hotel.domain.RollbackTestAbstractClass                              |
|###############################|################################################|
|                               | hotel.domain.HotelPersistenceTest 			                      |
|                               | hotel.services.local.HotelInterfaceReserveRoomMethodTest            |
|  Diogo Eusébio                | tax.domain.ItemTypeConstructorTest		                          |
|  87650                        | tax.domain.TaxPersistentTest                      	              |
|  DiogoEusebio                 | activity.domain.ActivityOfferConstructorMethodTest		          |
|                               | activity.domain.RollbackTestAbstractClass		                      |
|                               | bank.domain.BankConstructorTest		                              |
|                               | services.local.BankInterfaceProcessPaymentMethodTest                |
|###############################|################################################|
|                               | hotel.domain.HotelConstructorTest 			                      |
|                               | hotel.domain.HotelHasVacancyMethodTest                              |
|  Henrique Silva               | hotel.domain.RoomGetBookingMethodTest		                          |
|  86431                        | tax.domain.InvoiceConstructorTest                 	              |
|  HenriqueFSilva               | activity.domain.ActivityProviderConstructorMethodTest		          |
|                               | broker.domain.ClientConstructorMethodTest		                      |
|                               | bank.domain.BankGetAccountMethodTest                                |
|###############################|################################################|
|                               | hotel.domain.HotelSetPriceMethodTest		                          |
|                               | tax.domain.TaxInterfaceSubmitInvoiceTest                            |
|  Pedro Carvalho               | activity.domain.ActivityOfferGetBookingMethodTest		              |
|  86499                        | services.local.ActivityIntefaceGetActivityReservationDataMethodTest |
|  PedroMatias98                | broker.domain.AdventureConstructorMethodTest		                  |
|                               | bank.domain.AccountDepositMethodTest		                          |
|                               | bank.domain.OperationConstructorMethodTest		                  |
|                               | bank.domain.RollbackTestAbstractClass                               |
|###############################|################################################|
|                               | hotel.domain.BookingConflictMethodTest		                      |
|                               | services.local.HotelInterfaceGetRoomBookingDataMethodTest           |
|  Diogo Faria Fernandes        | tax.domain.IRSGetTaxPayerByNIFTest            		              |
|  86410                        | tax.domain.TaxPayerGetTaxesPerYearMethodsTest                       |
|  DiogoFariaFernandes          | tax.services.local.IRSCancelInvoiceMethodTest		                  |
|                               | activity.domain.BookingContructorMethodTest                         |
|                               | bank.domain.AccountConstructorMethodTest	    	                  |
|                               | bank.domain.BankInterfaceGetOperationDataMethodTest                 |


**Sprint Two**

| Name/Number/GitHub Username   |                         Files                                             |
| ----------------------------- | ------------------------------------------------------------------------- |
|  Lívio Costa                  | activity.domain.ActivityOfferHasVacancyMethodTest	                        |
|  86461                        | broker.domain.ReserveActivityStateProcessMethodTest                       |
|  LivioCosta                   | broker.domain.BulkRoomBookingProcessBookingMethodTest                     |
|###############################|#####################################################|
|  Gabriel Figueira             | hotel.services.local.HotelInterfaceCancelBookingMethodTest                |
|  86426                        | broker.domain.UndoStateProcessMethodTest                                  |
|  GabrielCFigueira             | broker.domain.RentVehicleStateMethodTest 	      	                        |
|###############################|#####################################################|
|  João Margaço                 | hotel.domain.ProcessorSubmitBookingMethodTest		                        |
|  86451                        | broker.domain.BookRoomStateMethodTest            	                        |
|  JoaoPmargaco                 | activity.service.ActivityInterfaceReserveActivityMethodTest               |
|###############################|#####################################################|
|  Diogo Eusébio                | hotel.service.HotelInterfaceBulkBookingMethodTest                         |
|  87650                        | broker.domain.BulkRoomBookingGetRoomBookingData4TypeMethodTest            |
|  DiogoEusebio                 | broker.domain.ProcessPaymentStateProcessMethodTest                        |
|###############################|#####################################################|
|  Henrique Silva               | activity.serice.ActivityInterfaceCancelReservationMethodTest              |
|  86431                        | broker.domain.ConfirmedStateProcessMethodTest        	                    |
|  HenriqueFSilva               |                                                                           |
|###############################|#####################################################|
|  Pedro Carvalho               | broker.domain.AdventureSequenceTest                                       |
|  86499                        | broker / TaxPaymentState                                                  |
|  PedroMatias98                |                                                                           |
|###############################|#####################################################|
|  Diogo Faria Fernandes        | activity.domain.InvoiceProcessorSubmitBookingMethodTest                   |
|  86410                        | broker.domain.CancelledStateProcessMethodTest                             |
|  DiogoFariaFernandes          |                                                                           |


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

package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*

class RentVehicleStateMotorcycleMethodSpockTest extends SpockRollbackTestAbstractClass {
    def carInterface
    def taxInterface

    def adventure
    def rentingData

    @Override
    def populate4Test() {
        carInterface = Mock(CarInterface)
        taxInterface = Mock(TaxInterface)

        def broker = new Broker("BR01", "eXtremeADVENTURE", NIF_AS_BUYER, BROKER_IBAN, new ActivityInterface(), new HotelInterface(), carInterface, new BankInterface(), taxInterface)
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        def vehicle = new VehicleType(adventure,CarInterface.Type.MOTORCYCLE)

        rentingData = new RestRentingData()
        rentingData.setReference(RENTING_CONFIRMATION)
        rentingData.setPrice(76.78)

        adventure.setState(Adventure.State.RENT_VEHICLE)
    }

    def 'successRentVehicle'() {
        given: 'mocking of renting a motorcycle succeeds and returns value rentingData'
        carInterface.rentCar(CarInterface.Type.MOTORCYCLE, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                        BEGIN, END, _) >> rentingData

        when: 'adventure is processed'
        adventure.process()

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    @Unroll('#label (rentCar): adventure in state #adventure_state ')
    def 'exceptions rentCar'() {
        given: 'rentCar fails #iterations time(s)'
        iterations * carInterface.rentCar(CarInterface.Type.MOTORCYCLE, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _) >> { throw mock_exception }

        when: 'adventure is processed'
        1.upto(iterations) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == adventure_state

        where:
        mock_exception              | adventure_state              | iterations                         | label
        new CarException()          | Adventure.State.UNDO         | 1                                  | 'car exception'
        new RemoteAccessException() | Adventure.State.RENT_VEHICLE | 1                                  | 'remote access exception'
        new RemoteAccessException() | Adventure.State.UNDO         | RentVehicleState.MAX_REMOTE_ERRORS | 'max remote access exception'
        new RemoteAccessException() | Adventure.State.RENT_VEHICLE | RentVehicleState.MAX_REMOTE_ERRORS - 1 | 'max minus one remote access exception'
    }

    def 'twoRemoteAccessExceptionOneSuccess'() {
        given: 'renting a motorcycle fails with two remote exceptions and then succeeds'
        carInterface.rentCar(CarInterface.Type.MOTORCYCLE, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _) >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                rentingData

        when: 'adventure is processed 3 times'
        1.upto(3) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
    }

    def 'oneRemoteAccessExceptionOneCarException'() {
        given: 'renting a motorcycle fails with a remote exception followed by a car exception'
        carInterface.rentCar(CarInterface.Type.MOTORCYCLE, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
                BEGIN, END, _) >>
                { throw new RemoteAccessException() } >>
                { throw new CarException() }

        when: 'adventure is processed twice'
        1.upto(2) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.UNDO
    }
}

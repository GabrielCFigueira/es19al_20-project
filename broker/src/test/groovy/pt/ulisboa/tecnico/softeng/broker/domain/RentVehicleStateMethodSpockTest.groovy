package pt.ulisboa.tecnico.softeng.broker.domain


import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import spock.lang.Unroll
import spock.lang.Shared


public class RentVehicleStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    
	@Shared def private rentingData
    def private carInterface
    
    
    @Override
	def populate4Test() {
        carInterface = Mock(CarInterface)
		
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
		adventure.setCarInterface(carInterface)

		rentingData = new RestRentingData()
		rentingData.setReference(RENTING_CONFIRMATION)
		rentingData.setPrice(76.78)

		adventure.setState(State.RENT_VEHICLE)
	}

	@Unroll('Test rentCar() with exceptions thrown')
    def success() {
		given:
		_t * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
		BEGIN, END, _ as String) >> {throw _return}

		when:
		for (def i = 0; i < _t; i++)
			adventure.process()

		then:
		_state == adventure.getState().getValue()

		where:
		_return                     | _t                                     | _state
		new CarException()          | 1                                      | State.UNDO
		new RemoteAccessException() | 1                                      | State.RENT_VEHICLE
		new RemoteAccessException() | RentVehicleState.MAX_REMOTE_ERRORS     | State.UNDO
		new RemoteAccessException() | RentVehicleState.MAX_REMOTE_ERRORS - 1 | State.RENT_VEHICLE

	}
	
    def 'success rent vehicle'() {  
        given:
        1 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
		BEGIN, END, _ as String) >> rentingData

        when:
		adventure.process()

        then:
		State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

    def 'two remote access exception one success'() {
        given:
        3 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
		BEGIN, END, _ as String) >> { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >> rentingData

        when:
		adventure.process()
		adventure.process()
		adventure.process()

        then:
		State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

    def 'one remote access exception one car exception'() {
		given:
		2 * carInterface.rentCar(CarInterface.Type.CAR, DRIVING_LICENSE, BROKER_NIF_AS_BUYER, BROKER_IBAN,
		BEGIN, END, _ as String) >> { throw new RemoteAccessException() } >> { throw new CarException() }

        when:
		adventure.process()
		adventure.process()

        then:
		State.UNDO == adventure.getState().getValue()
	}
}
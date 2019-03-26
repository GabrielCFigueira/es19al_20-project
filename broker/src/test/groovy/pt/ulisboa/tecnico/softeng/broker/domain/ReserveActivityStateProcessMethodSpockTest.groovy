package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import spock.lang.Unroll

class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
	def taxInterface = Mock(TaxInterface)
    def activityInterface = Mock(ActivityInterface)
	def bookingData = Mock(RestActivityBookingData)

    def broker
    def client
    def adventure

	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN, activityInterface)
		bookingData = new RestActivityBookingData()
		bookingData.setReference(ACTIVITY_CONFIRMATION)
		bookingData.setPrice(76.78)
        adventure.setState(State.RESERVE_ACTIVITY)
	}

    def 'successNoBookRoom'(){
        given:
        def sameDayAdventure = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, activityInterface)
		sameDayAdventure.setState(State.RESERVE_ACTIVITY)

        activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData

        when:
        sameDayAdventure.process()

        then:
        State.PROCESS_PAYMENT == sameDayAdventure.getState().getValue()
    }

	def 'successToRentVehicle'() {
		given:
		def adv = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, true, activityInterface)
		adv.setState(State.RESERVE_ACTIVITY)

		activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData
		
		when:
		adv.process()

		then:
		State.RENT_VEHICLE == adv.getState().getValue()
	}

	def 'successBookRoom'() {
		given:
		activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingData

		when:
		adventure.process()

		then:
		State.BOOK_ROOM == adventure.getState().getValue()
	}

	@Unroll('ReserveActivity: #_return | #_assert_value | #_iter')
	def 'activityException and singleRemoteAccessException and maxRemoteAccessException and maxMinusOneRemoteAccessException'(){
		given:
		activityInterface.reserveActivity(_ as RestActivityBookingData) >> {throw _return}

		when:
		for(def i=0; i < _iter; i++)
			adventure.process()

		then:
		_assert_value == adventure.getState().getValue()

		where:
			_return						| _assert_value				| _iter
			new ActivityException()		| State.UNDO				| 1
			new RemoteAccessException()	| State.RESERVE_ACTIVITY	| 1
			new RemoteAccessException()	| State.UNDO				| 5
			new RemoteAccessException()	| State.RESERVE_ACTIVITY	| 4
	}

	def 'twoRemoteAccessExceptionOneSuccess'() {
		given:
		activityInterface.reserveActivity(_ as RestActivityBookingData) >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> bookingData

		when:
		for(def i=0; i < 3; i++)
			adventure.process()
		
		then:
		State.BOOK_ROOM == adventure.getState().getValue()
	}
    
	def 'oneRemoteAccessExceptionOneActivityException'() {
		given:
		activityInterface.reserveActivity(_ as RestActivityBookingData) >> {throw new RemoteAccessException()} >> {throw new ActivityException()}

		when:
		for(def i=0; i < 2; i++)
			adventure.process()
		
		then:
		State.UNDO == adventure.getState().getValue()
	}

}
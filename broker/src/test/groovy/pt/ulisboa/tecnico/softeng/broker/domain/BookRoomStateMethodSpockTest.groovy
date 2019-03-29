package pt.ulisboa.tecnico.softeng.broker.domain  

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State  
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData  
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException  
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException  
import spock.lang.Unroll

import java.util.Arrays
import java.util.HashSet
import java.util.Set
import org.joda.time.LocalDate

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bookingData  
    def hotelInterface
	def activityInterface = new ActivityInterface()
	def taxInterface = new TaxInterface()
	def BankInterface = new BankInterface()
	def carInterface = new CarInterface()
	def restActivityBookingData = new RestActivityBookingData()
	def restRentingData = new RestRentingData()
	def restRoomBookingData = new RestRoomBookingData()
    def broker
    def client 
    def adventure 
	

	@Override
	def populate4Test() {
        hotelInterface = Mock(HotelInterface)
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,activityInterface,
		taxInterface,bankInterface,hotelInterface,carInterface,restActivityBookingData,restRentingData,restRoomBookingData)  
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)  
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

		bookingData = new RestRoomBookingData()  
		bookingData.setReference(ROOM_CONFIRMATION)  
		bookingData.setPrice(80.0)  

		adventure.setState(State.BOOK_ROOM)  
	}

	def 'successBookRoom'() {
        given:'mocking the remote invocation to succeed and return references'
            hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingData
        when:'processing an adventure'
		    adventure.process()  
        then:'reaching the right state'
		    State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

	def 'successBookRoomToRenting'() {
        given:'creating an adventure'
		    def adv = new Adventure(broker, BEGIN, END, client, MARGIN, true)
		    adv.setState(State.BOOK_ROOM)  
			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingData  
        when:'processing an adventure'
		    adv.process()  
        then:'reaching the right state'
		    State.RENT_VEHICLE == adv.getState().getValue()  
	}

	@Unroll('#exception,#argument')
	def 'exceptions'(){
		given:'mocking the remote invocation to succeed and return references'
			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> {throw exception}
		when:'processing the adventure'
			adventure.process()
		then:'reaching the right state'
			argument == adventure.getState().getValue()
		where:
			exception 						|	 argument
			new HotelException()		  	|	State.UNDO
			new RemoteAccessException() 	|	State.BOOK_ROOM
	}

	@Unroll('#iterations,#state')
	def 'maxExceptions'() {
		given:'mocking the remote invocation to succeed and return references'
			iterations * hotelInterface.reserveRoom(_ as RestRoomBookingData) >> {throw new RemoteAccessException()} 
		when:'processing the adventure multiple times'
			1.upto(iterations){
				adventure.process()
			}
		then:'reaching the right state'
			state == adventure.getState().getValue()
		where:
			iterations						    | state
			BookRoomState.MAX_REMOTE_ERRORS	    | State.UNDO 
			BookRoomState.MAX_REMOTE_ERRORS - 1	| State.BOOK_ROOM 
	}

	def 'bulkFound'(){
		/*
		@Shared def BEGIN = new LocalDate(2016, 12, 19)
		@Shared def END = new LocalDate(2016, 12, 21)*/
		given:
			//def mockedBroker = Mock(Broker)
			System.out.println("\n\n\n" + mockedBroker)
			def roomBooking = Mock(BulkRoomBooking)
			/*BulkRoomBooking roomBooking = new BulkRoomBooking(mockedBroker, NUMBER_OF_BULK, new LocalDate(2016,12,18), new LocalDate(2016,12,22), NIF_AS_BUYER,
                           IBAN_BUYER)*/
			broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,activityInterface,
		taxInterface,bankInterface,hotelInterface,carInterface,restActivityBookingData,restRentingData,restRoomBookingData)
			adventure = new Adventure(mockedBroker, BEGIN, END, client, MARGIN)
			roomBooking.getArrival() >> new LocalDate(2016,12,18)
			roomBooking.getDeparture() >> new LocalDate(2016,12,22)
			 HashSet<>(Arrays.asList(roomBooking) books = broker.getRoomBulkBookingSet();
		when: 
			adventure.process()
		then:
			0 * hotelInterface.reserveRoom(_ as RestRoomBookingData)
	}

	def 'fiveRemoteAccessExceptionOneSuccess'() {
        given:'mocking the remote invocation to succeed and return references'
            def times = 6
            times * hotelInterface.reserveRoom(_ as RestRoomBookingData) >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()}  >>> [bookingData]
        when:'processing the adventure multiple times'
            1.upto(6) {
                adventure.process()  
            }
        then:'reaching the right state'
		    State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

	def 'oneRemoteAccessExceptionOneHotelException'() {
		given:'mocking the remote invocation to succeed and return references'
			def times = 2
			times * hotelInterface.reserveRoom(_ as RestRoomBookingData) >> {throw new RemoteAccessException()} >> {throw new HotelException()}
		when:'processing the adventure multiple times'
			1.upto(2){
				adventure.process()  
			}
		then:'reaching the right state'
			State.UNDO == adventure.getState().getValue() 
	}

}
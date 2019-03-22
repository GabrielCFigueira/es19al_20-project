package pt.ulisboa.tecnico.softeng.broker.domain  

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State  
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface  
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData  
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException  
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException  
import spock.lang.Unroll

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bookingData  
    def hotelInterface
    def broker
    def client 
    def adventure 

	@Override
	def populate4Test() {
        hotelInterface = Mock(HotelInterface)
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)  
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)  
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
		adventure.setHotelInterface(hotelInterface)  

		bookingData = new RestRoomBookingData()  
		bookingData.setReference(ROOM_CONFIRMATION)  
		bookingData.setPrice(80.0)  

		adventure.setState(State.BOOK_ROOM)  
	}

	def 'successBookRoom'() {
        given:
            hotelInterface.reserveRoom(_) >> bookingData
        when:
		    adventure.process()  
        then:
		    State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

	def 'successBookRoomToRenting'() {
        given:
		    def adv = new Adventure(broker, BEGIN, END, client, MARGIN, true)
			adv.setHotelInterface(hotelInterface)  
		    adv.setState(State.BOOK_ROOM)  
			hotelInterface.reserveRoom(_) >> bookingData  
        when:
		    adv.process()  
        then:
		    State.RENT_VEHICLE == adv.getState().getValue()  
	}

	@Unroll
	def 'exceptions'(){
		given:
			hotelInterface.reserveRoom(_) >> {throw exception}
		when:
			adventure.process()
		then:
			argument == adventure.getState().getValue()
		where:
			exception 						|	 argument
			new HotelException()		  	|	State.UNDO
			new RemoteAccessException() 	|	State.BOOK_ROOM
	}

	@Unroll
	def 'maxExceptions'() {
		given:
			iterations * hotelInterface.reserveRoom(_) >> {throw new RemoteAccessException()} 
		when:
			for (def i=0; i< iterations; i++){
				adventure.process()
			}
		then:
			state == adventure.getState().getValue()
		where:
			iterations						    | state
			BookRoomState.MAX_REMOTE_ERRORS	    | State.UNDO 
			BookRoomState.MAX_REMOTE_ERRORS - 1	| State.BOOK_ROOM 
	}

	def 'fiveRemoteAccessExceptionOneSuccess'() {
        given:
            def times = 6
            times * hotelInterface.reserveRoom(_) >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()}  >>> [bookingData]
        when:
            for (def i = 0 ;  i < 6 ; i++) {
                adventure.process()  
            }
        then: 
		    State.PROCESS_PAYMENT == adventure.getState().getValue()
	}

	def 'oneRemoteAccessExceptionOneHotelException'() {
		given:
			def times = 2
			times * hotelInterface.reserveRoom(_) >> {throw new RemoteAccessException()} >> {throw new HotelException()}
		when:
			for (def i=0; i< 2; i++){
				adventure.process()  
			}
		then:
			State.UNDO == adventure.getState().getValue() 
	}

}
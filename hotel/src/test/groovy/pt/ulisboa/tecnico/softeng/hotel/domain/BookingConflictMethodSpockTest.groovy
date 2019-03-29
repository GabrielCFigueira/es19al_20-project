package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate

import spock.lang.Shared
import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface


class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def arrival = new LocalDate(2016, 12, 19)
	@Shared def departure = new LocalDate(2016, 12, 24)
	def booking
	def NIF_HOTEL = "123456700"
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"

	// JFF: unused variables
	def taxInterface
	
	def bankInterface

	@Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", NIF_HOTEL, "IBAN", 20.0, 30.0)
		def room = new Room(hotel, "01", Room.Type.SINGLE)

		booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)
	}

	

	//JFF: semicolons are unnecessary
	def 'noConflictBecauseItIsCancelled'() {
		when:'when cancelling booking'
			booking.cancel();
		then:'no conflict'
			false == booking.conflict(booking.getArrival(), booking.getDeparture());
	}

	
	def 'argumentsAreInconsistent'() {
		when:'when arguments are inconsistent'
			booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9));
		then:'throw HotelException'
			thrown(HotelException);
	}
	
	@Unroll('BookingConflictMethod:#arg1,#arg2,#bool')
	def 'asserts'(){
		expect:
			bool == booking.conflict(arg1,arg2);
		where:
			bool 	| arg1							| arg2
			false 	| new LocalDate(2016, 12, 9)	| new LocalDate(2016, 12, 15)
			false	| arrival.minusDays(10)			| arrival.minusDays(4)
			false	| arrival.minusDays(10)    		| arrival
			false 	| departure.plusDays(4)			| departure.plusDays(10)
			false 	| departure						| departure.plusDays(10)
			true 	| new LocalDate(2016, 12, 9)	| new LocalDate(2016, 12, 9)
			true 	| arrival.minusDays(4)       	| departure.plusDays(4)
			true 	| arrival						| departure.plusDays(4)
			true 	| arrival.minusDays(4) 			| departure
			true 	| arrival.minusDays(4)			| departure.minusDays(3)
			true 	| arrival.plusDays(3)			| departure.plusDays(6)	
			
	}
	

}

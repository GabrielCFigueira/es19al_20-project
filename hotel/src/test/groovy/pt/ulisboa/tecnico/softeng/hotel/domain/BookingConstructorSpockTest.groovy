package pt.ulisboa.tecnico.softeng.hotel.domain 

import org.joda.time.LocalDate 

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface 

public class BookingConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def ARRIVAL = new LocalDate(2016, 12, 19) 
	@Shared def DEPARTURE = new LocalDate(2016, 12, 21) 
	@Shared def ROOM_PRICE = 20.0 
	@Shared def NIF_BUYER = "123456789" 
	@Shared def IBAN_BUYER = "IBAN_BUYER" 
	@Shared def room 

	def taxInterface 
	def bankInterface 

	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", "NIF", "IBAN", 20.0, 30.0) 
		room = new Room(hotel, "01", Room.Type.SINGLE) 
	}

	def 'success'() {
        when: 'creating a new booking'
		    def booking = new Booking(room, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER) 
        then: 'should succeed'
            booking.getReference().startsWith(room.getHotel().getCode()) 
            booking.getReference().length() > Hotel.CODE_SIZE 
            ARRIVAL == booking.getArrival()
            DEPARTURE == booking.getDeparture() 
            ROOM_PRICE * 2 == booking.getPrice()
	}

	@Unroll('Booking: #_room, #_arrival, #_departure, #_nif, #_iban')
	def 'exceptions'(){
		when: 'creating a new booking with wrong parameters'
			new Booking(_room,_arrival,_departure,_nif,_iban)
		then: 'throws an exception'
			thrown(HotelException)
		where: 
			_room  | _arrival | _departure             | _nif        | _iban     
			null   | ARRIVAL  | DEPARTURE              | NIF_BUYER   | IBAN_BUYER 
			room   | null     | DEPARTURE              | NIF_BUYER   | IBAN_BUYER 
			room   | ARRIVAL  | null                   | NIF_BUYER   | IBAN_BUYER 
			room   | ARRIVAL  | ARRIVAL.minusDays(1)   | NIF_BUYER   | IBAN_BUYER 
	}

	def 'arrivalEqualDeparture'() {
        expect: 'the creation of a new booking'
		    new Booking(room, ARRIVAL, ARRIVAL, NIF_BUYER, IBAN_BUYER) 
	}

}

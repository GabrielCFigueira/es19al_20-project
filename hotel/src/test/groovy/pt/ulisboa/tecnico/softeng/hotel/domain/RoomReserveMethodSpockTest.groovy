package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import spock.lang.Unroll
import spock.lang.Shared


import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;


class RoomReserveMethodSpockTest extends SpockRollbackTestAbstractClass {

    @Shared def private final arrival = new LocalDate(2016, 12, 19)
	@Shared def private final departure = new LocalDate(2016, 12, 24)
	def private room
	def private final NIF_HOTEL = "123456700"
	def private final NIF_BUYER = "123456789"
	def private final IBAN_BUYER = "IBAN_BUYER"


    @Override
    def populate4Test() {
        def hotel = new Hotel("XPTO123", "Lisboa", this.NIF_HOTEL, "IBAN", 20.0, 30.0)
        this.room = new Room(hotel, "01", Type.SINGLE)
    }

    def success() {
        when:
        def booking = this.room.reserve(Type.SINGLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)
    
        then:
        1 == this.room.getBookingSet().size()
		booking.getReference().length() > 0
		this.arrival == booking.getArrival()
		this.departure == booking.getDeparture()
    }

    @Unroll('testing invalid reservations')
    def 'test reservation'() {
        when:
        this.room.reserve(_type, _arrival, _departure, this.NIF_BUYER, this.IBAN_BUYER)

        then:
        thrown(HotelException)

        where:
        _type       | _arrival | _departure
        Type.DOUBLE | arrival  | departure
        null        | arrival  | departure
        Type.SINGLE | null     | departure
        Type.SINGLE | arrival  | null

    }


    def allConflit() {
        when:
        this.room.reserve(Type.SINGLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)

        then:
        try {
			this.room.reserve(Type.SINGLE, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER)
			fail();
		} catch (HotelException he) {
			1 == this.room.getBookingSet().size()
		}
 
    }
}
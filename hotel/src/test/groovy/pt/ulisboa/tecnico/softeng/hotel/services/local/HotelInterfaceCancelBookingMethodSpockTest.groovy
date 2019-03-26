package pt.ulisboa.tecnico.softeng.hotel.services.local

import static org.junit.Assert.*

import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor

import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Unroll

class HotelInterfaceCancelBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"
	def ARRIVAL = new LocalDate(2016, 12, 19)
	def DEPARTURE = new LocalDate(2016, 12, 21)
	def hotel
	def room
	def booking

	def taxInterface = Mock(TaxInterface)

	@Override
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0)
		def processor = new Processor()
		processor.setTaxInterface(taxInterface)
		hotel.setProcessor(processor)
		room = new Room(hotel, "01", Type.DOUBLE)
		booking = room.reserve(Type.DOUBLE, ARRIVAL, DEPARTURE, NIF_BUYER, IBAN_BUYER)
	}

	def 'success'() {
		when: 'a booking is cancelled'
		def cancel = HotelInterface.cancelBooking(booking.getReference())

		then:
		booking.isCancelled()
		booking.getCancellation().equals(cancel)
	}

	@Unroll()
	def 'invalid arguments'() {
		when: 'a booking is cancelled'
		HotelInterface.cancelBooking(reference)

		then: 'throws an exception'
		thrown(HotelException)

		where:
		reference | label
		'XPTO'    | 'reference does not exist'
		null      | 'null reference'
		''        | 'empty reference'
		'   '     | 'bank reference'
	}

	def 'success integration'() {
		given:
		taxInterface.cancelInvoice(_ as String)

		when:
		def cancel = HotelInterface.cancelBooking(this.booking.getReference())

		then:
		booking.isCancelled()
		cancel == booking.getCancellation()

	}

	def 'does not exist integration'() {
		given:
		0 * taxInterface.cancelInvoice(_ as String)
	
		when:
		HotelInterface.cancelBooking("XPTO")
	
		then:
		thrown(HotelException)
	}
}

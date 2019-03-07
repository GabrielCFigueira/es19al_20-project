package pt.ulisboa.tecnico.softeng.hotel.domain


import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type


class RoomGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	def arrival = new LocalDate(2016, 12, 19)
	def departure = new LocalDate(2016, 12, 24)
	def hotel
	def room
	def booking
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"


	@Override
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
		room = new Room(hotel, "01", Type.SINGLE)
		booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
	}

	def 'success'() {
		expect: 'booking registered'
		booking == room.getBooking(booking.getReference())
	}

	def 'success canceled'() {
		when: 'cancelling the booking'
		booking.cancel()

		then: 'booking is canceled'
		booking == room.getBooking(booking.getCancellation())
	}

	def 'booking does not exists'() {
		expect: 'booking is null'
		null == room.getBooking("XPTO")
	}

}

package pt.ulisboa.tecnico.softeng.hotel.domain

import spock.lang.Shared
import spock.lang.Unroll


import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException


class HotelHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def arrival = new LocalDate(2016, 12, 19)
	@Shared def departure = new LocalDate(2016, 12, 21)
	def hotel
	def room
	@Shared def NIF_HOTEL = "123456700"
	@Shared def NIF_BUYER = "123456789"
	@Shared def IBAN_BUYER = "IBAN_BUYER"


	@Override
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0)
		room = new Room(hotel, "01", Type.DOUBLE)
	}

	def 'room has vacancy'() {
		when: 'getting a room by hotel vacancy'
		def room = hotel.hasVacancy(Type.DOUBLE, arrival, departure)

		then: 'exists vacant room'
		room != null
		"01" == room.getNumber()
	}

	def 'making reservation'() {
		when: 'reserving a room'
		room.reserve(Type.DOUBLE, arrival, departure, NIF_BUYER, IBAN_BUYER)

		then: 'room is no longer vacant'
		null == hotel.hasVacancy(Type.DOUBLE, arrival, departure)
	}

	def 'checking vacancy'() {
		when: 'creating a hotel with no rooms'
		def otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)

		then: 'there are no vacant rooms'
		null == otherHotel.hasVacancy(Type.DOUBLE, arrival, departure)
	}

	@Unroll('hasVacancy: #type, #arrive, #depart')
	def 'exceptions'() {
		when: 'checking vacancy with wrong parameters'
		hotel.hasVacancy(type, arrive, depart)

		then: 'throws an exception'
		thrown(HotelException)

		where:
		type        | arrive       | depart
		null        | arrival      | departure
		Type.DOUBLE | null         | departure
		Type.DOUBLE | arrival      | null
	}

}

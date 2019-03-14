package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

class HotelPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
    def logger = LoggerFactory.getLogger(HotelPersistenceSpockTest.class)

	def HOTEL_NIF = "123456789"
	def HOTEL_IBAN = "IBAN"
	def HOTEL_NAME = "Berlin Plaza"
	def HOTEL_CODE = "H123456"
	def ROOM_NUMBER = "01"
	def CLIENT_NIF = "123458789"
	def CLIENT_IBAN = "IBANC"
	def arrival = new LocalDate(2017, 12, 15)
	def departure = new LocalDate(2017, 12, 19)

	@Override
    def whenCreateInDatabase() {
		FenixFramework.getDomainRoot().getHotelSet().stream().each {h -> h.delete()}
		def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, 10.0, 20.0)
		new Room(hotel, ROOM_NUMBER, Type.DOUBLE)
		hotel.reserveRoom(Type.DOUBLE, arrival, departure, CLIENT_NIF, CLIENT_IBAN, "adventureId")
	}

	@Override
	def thenAssert() {
		assert FenixFramework.getDomainRoot().getHotelSet().size() == 1

		def hotels = FenixFramework.getDomainRoot().getHotelSet()
		def hotel = hotels[0]

		assert HOTEL_NAME == hotel.getName()
		assert HOTEL_CODE == hotel.getCode()
		assert HOTEL_IBAN == hotel.getIban()
		assert HOTEL_NIF == hotel.getNif()
		assert 10.0 == hotel.getPriceSingle()
		assert 20.0 == hotel.getPriceDouble()
		assert 1 == hotel.getRoomSet().size()
		def processor = hotel.getProcessor()
		assert null != processor
		assert 1 == processor.getBookingSet().size()

		def rooms = hotel.getRoomSet()
		def room = rooms[0]
		
		assert ROOM_NUMBER == room.getNumber()
		assert Type.DOUBLE == room.getType()
		assert 1 ==  room.getBookingSet().size()

		def bookings = room.getBookingSet()
		def booking = bookings[0]

		assert null != booking.getReference()
		assert arrival == booking.getArrival()
		assert departure == booking.getDeparture()
		assert CLIENT_IBAN == booking.getBuyerIban()
		assert CLIENT_NIF == booking.getBuyerNif()
		assert HOTEL_NIF == booking.getProviderNif()
		assert 80.0 == booking.getPrice()
		assert room == booking.getRoom()
		assert null != booking.getTime()
		assert null != booking.getProcessor()
	}

	@Override
	def deleteFromDatabase() {
		FenixFramework.getDomainRoot().getHotelSet().stream().each {h -> h.delete()}
	}
}
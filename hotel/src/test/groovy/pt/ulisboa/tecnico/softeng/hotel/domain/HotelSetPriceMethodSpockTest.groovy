package pt.ulisboa.tecnico.softeng.hotel.domain

import spock.lang.Shared
import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelSetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {
	def hotel
	@Shared def price = 25.0


	def populate4Test() {
		hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", price + 5.0, price + 10.0)
	}


	@Unroll('setPrice: #roomType, #price')
	def 'success'() {
		when:	'setting a price to an hotel room'
		    hotel.setPrice(roomType, _price)
		then:	'should succeed'
			price == hotel.getPrice(roomType)
		where:
			roomType         | _price
			Room.Type.SINGLE | price
			Room.Type.DOUBLE | price
	}


	@Unroll('setPrice: #roomType, #price')
	def 'exceptions'() {
		when:	'setting invalid prices'
		    hotel.setPrice(roomType, price)
		then:	'throws an exception'
			thrown(HotelException)
		where:
			roomType         | price
			Room.Type.SINGLE | -1.0
			Room.Type.DOUBLE | -1.0
	}

}
package pt.ulisboa.tecnico.softeng.hotel.domain


import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import spock.lang.Shared
import spock.lang.Unroll


class HotelConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def IBAN = "IBAN"
	@Shared def NIF = "NIF"

	@Shared def HOTEL_NAME = "Londres"
	@Shared def HOTEL_CODE = "XPTO123"

	@Shared def PRICE_SINGLE = 20.0
	@Shared def PRICE_DOUBLE = 30.0

	@Override
	def populate4Test() {
	}

	def 'success'() {
		when: 'creating a new hotel'
		def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: 'should succeed'
		HOTEL_NAME == hotel.getName()
		hotel.getCode().length() == Hotel.CODE_SIZE
		0 == hotel.getRoomSet().size()
		1 == FenixFramework.getDomainRoot().getHotelSet().size()
		PRICE_SINGLE == hotel.getPrice(Room.Type.SINGLE)
		PRICE_DOUBLE == hotel.getPrice(Room.Type.DOUBLE)
	}


	@Unroll('Hotel: #code, #name, #nif, #iban, #priceSingle, #priceDouble')
	def 'exceptions'() {
		when: 'creating a hotel with wrong parameters'
		new Hotel(code, name, nif, iban, priceSingle, priceDouble)

		then: 'throws an exception'
		thrown(HotelException)

		where:
		code        | name       | nif | iban | priceSingle  | priceDouble
		null        | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"      "    | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		""          | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE  | null       | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE  | "  "       | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE  | ""         | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"123456"    | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		"12345678"  | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | PRICE_DOUBLE
		HOTEL_CODE  | HOTEL_NAME | NIF | IBAN | -1.0         | PRICE_DOUBLE
		HOTEL_CODE  | HOTEL_NAME | NIF | IBAN | PRICE_SINGLE | -1.0
	}

	def 'duplicated code'() {
		given: 'a hotel'
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		when: 'creating another hotel with the same code'
		new Hotel(HOTEL_CODE, HOTEL_NAME + " City", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: 'throws an exception'
		thrown(HotelException)
	}

	def 'duplicated nif'() {
		given: 'a hotel'
		new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		when: 'creating another hotel with the same nif'
		new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)

		then: 'throws an exception'
		thrown(HotelException)
	}

}

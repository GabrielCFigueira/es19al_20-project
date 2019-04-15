package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

class HotelSetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared
    def PRICE = 25000

    def hotel

    @Override
    def populate4Test() {
        hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', PRICE + 5000, PRICE + 10000, new Processor(new BankInterface(), new TaxInterface()))
    }

    def 'success single'() {
        when:
        hotel.setPrice(Room.Type.SINGLE, PRICE);

        then:
        hotel.getPrice(Room.Type.SINGLE) == PRICE
        hotel.getPrice(Room.Type.DOUBLE) == PRICE + 10000
    }

    def 'success double'() {
        when:
        hotel.setPrice(Room.Type.DOUBLE, PRICE);

        then:
        hotel.getPrice(Room.Type.DOUBLE) == PRICE
        hotel.getPrice(Room.Type.SINGLE) == PRICE + 5000
    }

    @Unroll('one of the following argument is invalid: #type | #price')
    def 'incorret arguments'() {
        when:
        hotel.setPrice(type, price);

        then:
        def error = thrown(HotelException)

        where:
        type             | price
        Room.Type.SINGLE | -1000
        Room.Type.DOUBLE | -1000
    }
}

package pt.ulisboa.tecnico.softeng.hotel.domain


import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException


class HotelGetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {

    def private hotel
    def private final double priceSingle = 20.0
    def private final double priceDouble = 30.0


    @Override
    def populate4Test() {
        this.hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", this.priceSingle, this.priceDouble)
    }

    def successSingle() {
        expect:
        this.priceSingle == this.hotel.getPrice(Room.Type.SINGLE)
    }

    def successDouble() {
        expect:
        this.priceDouble == this.hotel.getPrice(Room.Type.DOUBLE)
    }

    def nullType() {
        when:
        this.hotel.getPrice(null)

        then:
        thrown(HotelException)
    }
}
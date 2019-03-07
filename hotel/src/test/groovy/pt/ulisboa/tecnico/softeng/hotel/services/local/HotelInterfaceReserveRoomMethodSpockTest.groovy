package pt.ulisboa.tecnico.softeng.hotel.services.local;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData;

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class HotelInterfaceReserveRoomMethodTest extends SpockRollbackTestAbstractClass {
    @Shared LocalDate arrival = new LocalDate(2016, 12, 19);
	@Shared LocalDate departure = new LocalDate(2016, 12, 24);
	private Hotel hotel;
	@Shared String NIF_HOTEL = "123456789";
	@Shared String NIF_BUYER = "123456700";
	@Shared String IBAN_BUYER = "IBAN_CUSTOMER";
	@Shared String IBAN_HOTEL = "IBAN_HOTEL";
	@Shared String ADVENTURE_ID = "ADVENTURE_ID";

    @Override
    def populate4Test() {
		this.hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, IBAN_HOTEL, 20.0, 30.0);
		new Room(this.hotel, "01", Room.Type.SINGLE);
	}

    def 'success'() {
        when: 'Testing InterfaceReserve'
        def bookingData = new RestRoomBookingData("SINGLE", this.arrival, this.departure, NIF_BUYER, 
            IBAN_BUYER, ADVENTURE_ID)
        
        bookingData = HotelInterface.reserveRoom(bookingData)

        then: 'should succeed'
        assertNotNull bookingData.getReference()
        assertTrue bookingData.getReference().startsWith("XPTO123")
    }

    def 'noHotels'() {
        when: 'Testing in InterfaceReserve'

        FenixFramework.getDomainRoot().getHotelSet().stream().each {h -> h.delete()} //deletes every hotel
		def bookingData = new RestRoomBookingData("SINGLE", this.arrival, this.departure, NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID)

        HotelInterface.reserveRoom(bookingData)

        then: 'there are no hotels'
        thrown(HotelException)
    }

    def 'noVacancy'() {
        when: 'Testing in InterfaceReserve'
        def bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)
        HotelInterface.reserveRoom(bookingData)

		bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25), NIF_BUYER,
				IBAN_BUYER, ADVENTURE_ID + "1")
        HotelInterface.reserveRoom(bookingData)

        then: 'there is no Vacancy'
        thrown(HotelException)

    }

    def 'noRooms'(){
        when: 'Testing in InterfaceReserve'
        this.hotel.getRoomSet().stream().each {r -> r.delete()}
        
        def bookingData = new RestRoomBookingData("SINGLE", this.arrival, new LocalDate(2016, 12, 25),
				NIF_BUYER, IBAN_BUYER, ADVENTURE_ID)

		HotelInterface.reserveRoom(bookingData)

        then: 'there is no Vacancy'
        thrown(HotelException)

    }
}
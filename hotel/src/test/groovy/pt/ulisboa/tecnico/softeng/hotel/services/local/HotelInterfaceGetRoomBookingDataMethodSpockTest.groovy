package pt.ulisboa.tecnico.softeng.hotel.services.local;


import org.joda.time.LocalDate;



import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData;


class HotelInterfaceGetRoomBookingDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	def private final arrival = new LocalDate(2016, 12, 19)
	def private final departure = new LocalDate(2016, 12, 24)
	def private hotel
	def private room
	def private booking
	def private final NIF_HOTEL = "123456700"
	def private final NIF_BUYER = "123456789"
	def private final IBAN_BUYER = "IBAN_BUYER"


	def taxInterface

	def bankInterface

	@Override
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0);
		room = new Room(hotel, "01", Type.SINGLE);
		booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER);
	}

	
	def 'success'() {
		when:
		def data = HotelInterface.getRoomBookingData(booking.getReference());
		then:
		booking.getReference() == data.getReference();
		null == data.getCancellation();
		null == data.getCancellationDate();
		hotel.getName() == data.getHotelName();
		hotel.getCode() == data.getHotelCode();
		room.getNumber() == data.getRoomNumber();
		room.getType().name() == data.getRoomType();
		booking.getArrival() == data.getArrival();
		booking.getDeparture() == data.getDeparture();
		booking.getPrice() == data.getPrice();

	}

	
	def 'successCancellation'() {
		when:
		booking.cancel();
		def data = HotelInterface.getRoomBookingData(booking.getCancellation());
		then:
		booking.getReference() == data.getReference();
		booking.getCancellation() == data.getCancellation();
		booking.getCancellationDate() == data.getCancellationDate();
		hotel.getName() == data.getHotelName();
		hotel.getCode() == data.getHotelCode();
		room.getNumber() == data.getRoomNumber();
		room.getType().name() == data.getRoomType();
		booking.getArrival() == data.getArrival();
		booking.getDeparture() == data.getDeparture();
		booking.getPrice() == data.getPrice();
	}

	
	def 'nullReference'() {
		when:
		HotelInterface.getRoomBookingData(null);
		then:
		thrown(HotelException);
	}

	
	def 'emptyReference'() {
		when:
		HotelInterface.getRoomBookingData("");
		then:
		thrown(HotelException);
	}

	
	def 'referenceDoesNotExist'() {
		when:
		HotelInterface.getRoomBookingData("XPTO");
		then:
		thrown(HotelException);
	}

}

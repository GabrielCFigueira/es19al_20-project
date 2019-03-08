package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;



import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;


class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
	def private final arrival = new LocalDate(2016, 12, 19);
	def private final departure = new LocalDate(2016, 12, 24);
	def private booking;
	def private final NIF_HOTEL = "123456700";
	def private final NIF_BUYER = "123456789";
	def private final IBAN_BUYER = "IBAN_BUYER";

	
	def private taxInterface;
	
	def private bankInterface;

	@Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", NIF_HOTEL, "IBAN", 20.0, 30.0);
		def room = new Room(hotel, "01", Room.Type.SINGLE);

		booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER);
	}

	
	def 'argumentsAreConsistent'() {
		expect:
		false == this.booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 15));
	}

	def 'noConflictBecauseItIsCancelled'() {
		when:
		booking.cancel();
		then:
		false == booking.conflict(booking.getArrival(), booking.getDeparture());
	}

	
	def 'argumentsAreInconsistent'() {
		when:
		booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9));
		then:
		thrown(HotelException);
	}


	def 'argumentsSameDay'() {
		expect:
		true == booking.conflict(new LocalDate(2016, 12, 9), new LocalDate(2016, 12, 9));
	}

	
	def 'arrivalAndDepartureAreBeforeBooked'() {
		expect:
		false == booking.conflict(arrival.minusDays(10), arrival.minusDays(4));
	}

	def 'arrivalAndDepartureAreBeforeBookedButDepartureIsEqualToBookedArrival'() {
		expect:
		false == booking.conflict(arrival.minusDays(10), arrival);
	}

	
	def 'arrivalAndDepartureAreAfterBooked'() {
		expect:
		false == booking.conflict(departure.plusDays(4), departure.plusDays(10));
	}

	
	def 'arrivalAndDepartureAreAfterBookedButArrivalIsEqualToBookedDeparture'() {
		expect:
		false == booking.conflict(departure, departure.plusDays(10));
	}


	def 'arrivalIsBeforeBookedArrivalAndDepartureIsAfterBookedDeparture'() {
		expect:
		true == booking.conflict(arrival.minusDays(4), departure.plusDays(4));
	}

	def 'arrivalIsEqualBookedArrivalAndDepartureIsAfterBookedDeparture'() {
		expect:
		true == booking.conflict(arrival, departure.plusDays(4));
	}

	def 'arrivalIsBeforeBookedArrivalAndDepartureIsEqualBookedDeparture'() {
		expect:
		true == booking.conflict(arrival.minusDays(4), departure);
	}

	def 'arrivalIsBeforeBookedArrivalAndDepartureIsBetweenBooked'() {
		expect:
		true == booking.conflict(arrival.minusDays(4), departure.minusDays(3));
	}

	def 'arrivalIsBetweenBookedAndDepartureIsAfterBookedDeparture'() {
		expect:
		true == booking.conflict(arrival.plusDays(3), departure.plusDays(6));
	}

}

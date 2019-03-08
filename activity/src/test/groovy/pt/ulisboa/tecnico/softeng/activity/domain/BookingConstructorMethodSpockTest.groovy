package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;

import spock.lang.Shared;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

import mockit.FullVerifications;


class BookingContructorMethodTest extends SpockRollbackTestAbstractClass {
	def private provider;
	def private offer;
	@Shared def private final AMOUNT = 30;
	@Shared def private final IBAN = "IBAN";
	@Shared def private final NIF = "123456789";

	@Override
	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3);

		def begin = new LocalDate(2016, 12, 19);
		def end = new LocalDate(2016, 12, 21);
		offer = new ActivityOffer(activity, begin, end, AMOUNT);
	}


	def 'success'() {
		when:
		def booking = new Booking(this.provider, this.offer, NIF, IBAN);
		then:
		true == booking.getReference().startsWith(provider.getCode());
		true == booking.getReference().length() > ActivityProvider.CODE_SIZE;
		1 == offer.getNumberActiveOfBookings();
		
		NIF == booking.getBuyerNif();
		IBAN == booking.getIban();
		AMOUNT == booking.getAmount();
	}

	def 'nullProvider'() {
		when:
		new Booking(null, offer, NIF, IBAN);

		new FullVerifications() {
		};
		then:
		thrown(ActivityException);
	}

	def 'nullOffer'() {
		when:
		new Booking(provider, null, NIF, IBAN);

		new FullVerifications() {
		};
		then:
		thrown(ActivityException);
	}

	def 'nullNIF'() {
		when:
		new Booking(null, offer, null, IBAN);

		new FullVerifications() {
		};
		then:
		thrown(ActivityException);
	}

	def 'emptyIBAN'() {
		when:
		new Booking(provider, null, NIF, "     ");
		then:
		thrown(ActivityException);
	}

	def 'nullIBAN'() {
		when:
		new Booking(null, offer, NIF, null);

		new FullVerifications() {
		};
		then:
		thrown(ActivityException);
	}

	def 'emptyNIF'() {
		when:
		new Booking(provider, null, "     ", IBAN);
		then:
		thrown(ActivityException);
	}

	def 'bookingEqualCapacity'() {
		given:
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);
		when:
		new Booking(provider, offer, NIF, IBAN);
		then:
		thrown(ActivityException); 
		and:
		3 == offer.getNumberActiveOfBookings();
		
	}


	def 'bookingEqualCapacityButHasCancelled'() {
		given:
		new Booking(provider, offer, NIF, IBAN);
		new Booking(provider, offer, NIF, IBAN);
		def booking = new Booking(provider, offer, NIF, IBAN);
		when:
		booking.cancel();		
		new Booking(provider, offer, NIF, IBAN);
		then:
		3 == offer.getNumberActiveOfBookings();
	}

}

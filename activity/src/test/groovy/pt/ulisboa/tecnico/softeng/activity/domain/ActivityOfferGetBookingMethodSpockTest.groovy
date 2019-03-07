package pt.ulisboa.tecnico.softeng.activity.domain


import org.joda.time.LocalDate


class ActivityOfferGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final String IBAN = "IBAN"
	private static final String NIF = "123456789"
	private ActivityProvider provider
	private ActivityOffer offer

	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		Activity activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		LocalDate begin = new LocalDate(2016, 12, 19)
		LocalDate end = new LocalDate(2016, 12, 21)

		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def 'success'() {
        when:	'creating a new booking'
		    Booking booking = new Booking(provider, offer, NIF, IBAN)
        then:	'should succeed'
		    booking == offer.getBooking(booking.getReference())
	}

	def 'successCancelled'() {
        when:	'creating and cancelling a booking'
		    Booking booking = new Booking(provider, offer, NIF, IBAN)
		    booking.cancel()
        then:	'should succeed'
		    booking == offer.getBooking(booking.getCancel())
	}

    def 'doesNotExist'() {
        when:	'creating a new booking'
		    new Booking(provider, offer, NIF, IBAN)

		then:	'should succeed'
            offer.getBooking("XPTO") == null
	}

}

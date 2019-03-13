package pt.ulisboa.tecnico.softeng.activity.domain


import org.joda.time.LocalDate


class ActivityOfferGetBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer

	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)

		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def 'success'() {
        when:	'creating a new booking'
		    def booking = new Booking(provider, offer, NIF, IBAN)
        then:	'should succeed'
		    booking == offer.getBooking(booking.getReference())
	}

	def 'success cancelled'() {
        when:	'creating and cancelling a booking'
		    def booking = new Booking(provider, offer, NIF, IBAN)
		    booking.cancel()
        then:	'should succeed'
		    booking == offer.getBooking(booking.getCancel())
	}

    def 'does not exist'() {
        when:	'creating a new booking'
		    new Booking(provider, offer, NIF, IBAN)

		then:	'should succeed'
            offer.getBooking("XPTO") == null
	}

}

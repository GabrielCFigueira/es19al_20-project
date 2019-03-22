package pt.ulisboa.tecnico.softeng.activity.services.local


import org.joda.time.LocalDate


import pt.ulisboa.tecnico.softeng.activity.domain.*
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface


class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer

	def taxInterface = Mock(TaxInterface)
	def bankInterface = Mock(BankInterface)

	@Override
	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)
		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def success() {
		bankInterface.processPayment(_) >> ""
		taxInterface.submitInvoice(_) >> ""

		def booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		def cancel = ActivityInterface.cancelReservation(booking.getReference())

		true == booking.isCancelled()
		cancel == booking.getCancel()
	}


	def 'activity does not exist'() {
		given: 'this processes'
			bankInterface.processPayment(_) >> ""
			taxInterface.submitInvoice(_) >> ""

		when: 'submitting a booking and cancelling a reservation'
			provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
			ActivityInterface.cancelReservation("XPTO")

		then: 'an exception occurs'
			thrown(ActivityException)
	}

}

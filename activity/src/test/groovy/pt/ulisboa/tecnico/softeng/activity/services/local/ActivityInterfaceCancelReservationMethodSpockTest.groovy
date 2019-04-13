package pt.ulisboa.tecnico.softeng.activity.services.local


import org.joda.time.LocalDate


import pt.ulisboa.tecnico.softeng.activity.domain.*
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData


class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer

	def taxInterface = Mock(TaxInterface)
	def bankInterface = Mock(BankInterface)

	def processor

	// JFF: tests different from what was expected (see solutions)
	@Override
	def populate4Test() {
		processor = new Processor(bankInterface, taxInterface)
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)
		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def 'success'() {
		given: 'this processes'
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null

		when: 'creating a new booking'
			def booking = new Booking(provider, offer, NIF, IBAN)
			provider.getProcessor().submitBooking(booking)
			def cancel = ActivityInterface.cancelReservation(booking.getReference())

		then: 'booking is cancelled'
			true == booking.isCancelled()
			cancel == booking.getCancel()
	}


	def 'activity does not exist'() {
		given: 'this processes'
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			taxInterface.submitInvoice(_ as RestInvoiceData	) >> null

		when: 'submitting a booking and cancelling a reservation'
			provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
			ActivityInterface.cancelReservation("XPTO")

		then: 'an exception occurs'
			thrown(ActivityException)
	}

}

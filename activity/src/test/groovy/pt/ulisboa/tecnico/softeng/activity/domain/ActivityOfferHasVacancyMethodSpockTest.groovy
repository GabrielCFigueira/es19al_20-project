package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData
import spock.lang.Unroll

class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN"
	def NIF = "123456789"
	def provider
	def offer

    def bankInterface = Mock(BankInterface)
    def taxInterface = Mock(TaxInterface)

	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)

		offer = new ActivityOffer(activity, begin, end, 30)
	}

	@Unroll('Booking: #_iter | #_assert')
	def 'success and bookingIsFull and bookingIsFullMinusOne'() {
        when:
		for(def i=0; i<_iter; i++)
			new Booking(provider, offer, NIF, IBAN)

        then:
		_assert == offer.hasVacancy()

		where:
			_iter	| _assert
			1		| true
			3		| false
			2		| true
	}

	def 'hasCancelledBookings'() {
		given:
        bankInterface.processPayment(_ as RestBankOperationData) >> null
        taxInterface.submitInvoice(_ as RestInvoiceData) >> null

        when:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

		def booking = new Booking(provider, offer, NIF, IBAN)

        then:
		provider.getProcessor().submitBooking(booking)

		booking.cancel()

		true == offer.hasVacancy()
	}

	def 'hasCancelledBookingsButFull'() {
        given:
		bankInterface.processPayment(_ as RestBankOperationData) >> null
        taxInterface.submitInvoice(_ as RestInvoiceData) >> null

		when:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
        
		provider.getProcessor().submitBooking(booking)
		booking.cancel()
		booking = new Booking(provider, offer, NIF, IBAN)

		provider.getProcessor().submitBooking(booking)

		then:
		false == offer.hasVacancy()
	}

}

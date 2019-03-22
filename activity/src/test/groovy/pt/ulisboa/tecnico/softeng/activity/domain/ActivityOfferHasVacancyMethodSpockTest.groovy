package pt.ulisboa.tecnico.softeng.activity.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

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

	def 'success'() {
        when:
		new Booking(provider, offer, NIF, IBAN)

        then:
		true == offer.hasVacancy()
	}

	def 'bookingIsFull'() {
		when:
        new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)

        then:
		false == offer.hasVacancy()
	}

	def 'bookingIsFullMinusOne'() {
        when:
		new Booking(provider, offer, NIF, IBAN)
		new Booking(provider, offer, NIF, IBAN)

		then:
        true == offer.hasVacancy()
	}

	def 'hasCancelledBookings'() {
		given:
        bankInterface.processPayment(_) >> ""
        taxInterface.submitInvoice(_) >> ""

        when:
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))

		def booking = new Booking(provider, offer, NIF, IBAN)

        then:
		provider.getProcessor().submitBooking(booking)

		booking.cancel()

		true == offer.hasVacancy()
	}

	def hasCancelledBookingsButFull() {
        bankInterface.processPayment(_) >> ""
        taxInterface.submitInvoice(_) >> ""

		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		def booking = new Booking(provider, offer, NIF, IBAN)
        
		provider.getProcessor().submitBooking(booking)
		booking.cancel()
		booking = new Booking(provider, offer, NIF, IBAN)

		provider.getProcessor().submitBooking(booking)

		false == offer.hasVacancy()
	}

}

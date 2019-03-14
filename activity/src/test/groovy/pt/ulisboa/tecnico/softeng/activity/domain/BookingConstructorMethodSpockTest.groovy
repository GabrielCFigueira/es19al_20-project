package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

import mockit.FullVerifications


class BookingContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def provider
	@Shared def offer
	def AMOUNT = 30
	@Shared def IBAN = "IBAN"
	@Shared def NIF = "123456789"

	@Override
	def populate4Test() {
		provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)
		def begin = new LocalDate(2016, 12, 19)
		def end = new LocalDate(2016, 12, 21)
		offer = new ActivityOffer(activity, begin, end, AMOUNT)
	}


	def 'success'() {
		when:'when creating a new Booking'
			def booking = new Booking(provider, offer, NIF, IBAN)
		then: 'should succeed'
			true == booking.getReference().startsWith(provider.getCode())
			true == booking.getReference().length() > ActivityProvider.CODE_SIZE
			1 == offer.getNumberActiveOfBookings()
			
			NIF == booking.getBuyerNif()
			IBAN == booking.getIban()
			AMOUNT == booking.getAmount()
	}
	@Unroll('BookingConstructorMethod:#provider_tab, #offer_tab, #NIF_tab, #IBAN_tab')
	def 'nullFullVerifications'(){
		when:'when creating Booking'
			new Booking(provider_tab, offer_tab, NIF_tab, IBAN_tab)
			new FullVerifications() {}
		then:'throws ActivityException'
			thrown(ActivityException)
		where:
			provider_tab	| offer_tab 	| NIF_tab	| IBAN_tab
			null			| offer			| NIF		| IBAN
			provider		| null 			| NIF 		| IBAN
			null			| offer 		| null 		| IBAN
			null			| offer 		| NIF		| null
				
	}
	
	@Unroll('BookingConstructorMethod:#provider_tab, #offer_tab, #NIF_tab, #IBAN_tab')
	def 'empty'(){
		when:'when creating Booking'
			new Booking(provider_tab, offer_tab, NIF_tab, IBAN_tab)
		then:'throws ActivityException'
			thrown(ActivityException)
		where:
			provider_tab	| offer_tab 	| NIF_tab	| IBAN_tab
			provider		| null 			| NIF 		| "     "
			provider		| null 			| "     " 	| IBAN
	}


	def 'bookingEqualCapacity'() {
		given:'given three bookings'
			new Booking(provider, offer, NIF, IBAN)
			new Booking(provider, offer, NIF, IBAN)
			new Booking(provider, offer, NIF, IBAN)
		when:'when creating a forth'
			new Booking(provider, offer, NIF, IBAN)
		then:'throw ActivtyException'
			thrown(ActivityException)
		and:'assert the expected number(3)'
			3 == offer.getNumberActiveOfBookings()
		
	}


	def 'bookingEqualCapacityButHasCancelled'() {
		given:'given three bookings'
			new Booking(provider, offer, NIF, IBAN)
			new Booking(provider, offer, NIF, IBAN)
			def booking = new Booking(provider, offer, NIF, IBAN)
		when:'when cancelling'
			booking.cancel()
			new Booking(provider, offer, NIF, IBAN)
		then:'assert the expected number(3)'
			3 == offer.getNumberActiveOfBookings()
	}

}

package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import spock.lang.Shared

import pt.ist.fenixframework.FenixFramework


class ActivityPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	def ADVENTURE_ID = "AdventureId" 
	def ACTIVITY_NAME = "Activity_Name" 
	def PROVIDER_NAME = "Wicket" 
	def PROVIDER_CODE = "A12345" 
	def IBAN = "IBAN" 
	def NIF = "NIF" 
	def BUYER_IBAN = "IBAN2" 
	def BUYER_NIF = "NIF2" 
	def CAPACITY = 25 
	def AMOUNT = 30.0 
	def begin = LocalDate.parse("2017-04-01") 
	def end = LocalDate.parse("2017-04-15") 

	@Override
	def whenCreateInDatabase() {
		def activityProvider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN) 
		def activity = new Activity(activityProvider, ACTIVITY_NAME, 18, 65, CAPACITY) 
		def offer = new ActivityOffer(activity, begin, end, AMOUNT) 
		offer.book(activityProvider, offer, 54, BUYER_NIF, BUYER_IBAN, ADVENTURE_ID) 
	}

	@Override
	def thenAssert() {
		assert 1 == FenixFramework.getDomainRoot().getActivityProviderSet().size() 

		def providers = new ArrayList<>(FenixFramework.getDomainRoot().getActivityProviderSet()) 
		def provider = providers.get(0) 

		assert PROVIDER_CODE == provider.getCode()
		assert PROVIDER_NAME == provider.getName()
		assert 1 == provider.getActivitySet().size() 
		assert NIF == provider.getNif()
		assert IBAN == provider.getIban() 
		def processor = provider.getProcessor() 
		assert processor != null
		assert 1 == processor.getBookingSet().size()

		def activities = new ArrayList<>(provider.getActivitySet()) 
		def activity = activities.get(0) 

		assert ACTIVITY_NAME == activity.getName()
		assert activity.getCode().startsWith(PROVIDER_CODE) 
		assert 18 == activity.getMinAge()
		assert 65 == activity.getMaxAge()
		assert CAPACITY == activity.getCapacity()
		assert 1 == activity.getActivityOfferSet().size()

		def offers = new ArrayList<>(activity.getActivityOfferSet()) 
		def offer = offers.get(0) 

		assert begin == offer.getBegin() 
		assert end == offer.getEnd()
		assert CAPACITY == offer.getCapacity() 
		assert 1 == offer.getBookingSet().size() 
		assert AMOUNT == offer.getPrice() 

		def bookings = new ArrayList<>(offer.getBookingSet()) 
		def booking = bookings.get(0) 

		assert booking.getReference()!= null
		assert booking.getCancel() == null
		assert booking.getCancellationDate() == null 
		assert booking.getPaymentReference() == null 
		assert booking.getInvoiceReference() == null
		assert booking.getCancelledInvoice() != true
		assert booking.getCancelledPaymentReference() == null
		assert "SPORT" == booking.getType() 
		assert BUYER_NIF == booking.getBuyerNif() 
		assert BUYER_IBAN == booking.getIban()
		assert NIF == booking.getProviderNif()
		assert AMOUNT == booking.getAmount()
		assert ADVENTURE_ID == booking.getAdventureId() 
		assert begin == booking.getDate()
		assert booking.getTime() != null 
		assert booking.getProcessor() != null 
	}

	@Override
	def deleteFromDatabase() {
		for (def activityProvider : FenixFramework.getDomainRoot().getActivityProviderSet()) {
			activityProvider.delete() 
		}
	}

}
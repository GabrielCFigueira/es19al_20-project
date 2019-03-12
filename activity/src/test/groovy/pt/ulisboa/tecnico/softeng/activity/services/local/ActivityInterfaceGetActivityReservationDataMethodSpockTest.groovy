package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData

class ActivityInterfaceGetActivityReservationDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	def NAME = "ExtremeAdventure"
	def CODE = "XtremX"
	def begin = new LocalDate(2016, 12, 19)
	def end = new LocalDate(2016, 12, 21)
	def provider
	def offer
	def booking

	def populate4Test() {
		provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN")
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def 'success'() {
		when:	'creating a new booking'
			booking = new Booking(provider, offer, "123456789", "IBAN")

			def data = ActivityInterface.getActivityReservationData(booking.getReference())
		then:	'should succeed'
			booking.getReference() == data.getReference()
			data.getCancellation() == null
			NAME == data.getName()
			CODE == data.getCode()
			begin == data.getBegin()
			end == data.getEnd()
			data.getCancellationDate() == null
	}

	def 'success cancelled'() {
		when:	'creating and cancel a bookign'
			booking = new Booking(provider, offer, "123456789", "IBAN")
			provider.getProcessor().submitBooking(booking)
			booking.cancel();
			def data = ActivityInterface.getActivityReservationData(booking.getCancel())
		then:	'should succeed'
			booking.getReference() == data.getReference()
			booking.getCancel() == data.getCancellation()
			NAME == data.getName()
			CODE == data.getCode()
			begin == data.getBegin()
			end == data.getEnd()
			data.getCancellationDate() != null
	}

	@Unroll("getActivityReservationData: #reference")
	def 'exceptions'() {
		when:	'getting invalid references'
			ActivityInterface.getActivityReservationData(reference)
		then:	'throws an exception'
			thrown(ActivityException)
		where:
			reference | _
			null      | _
			""        | _
			"XPTO"    | _
	}

}

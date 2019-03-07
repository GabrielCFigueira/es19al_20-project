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
	private static final String NAME = "ExtremeAdventure"
	private static final String CODE = "XtremX"
	private final LocalDate begin = new LocalDate(2016, 12, 19)
	private final LocalDate end = new LocalDate(2016, 12, 21)
	private ActivityProvider provider
	private ActivityOffer offer
	private Booking booking

	def populate4Test() {
		provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN")
		Activity activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		offer = new ActivityOffer(activity, begin, end, 30)
	}

	def 'success'() {
		when:	'creating a new booking'
			booking = new Booking(provider, offer, "123456789", "IBAN")

			RestActivityBookingData data = ActivityInterface.getActivityReservationData(booking.getReference())
		then:	'should succeed'
			booking.getReference() == data.getReference()
			data.getCancellation() == null
			NAME == data.getName()
			CODE == data.getCode()
			begin == data.getBegin()
			end == data.getEnd()
			data.getCancellationDate() == null
	}

	def 'successCancelled'() {
		when:	'creating and cancel a bookign'
			booking = new Booking(provider, offer, "123456789", "IBAN")
			provider.getProcessor().submitBooking(booking)
			booking.cancel();
			RestActivityBookingData data = ActivityInterface.getActivityReservationData(booking.getCancel())
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

package pt.ulisboa.tecnico.softeng.activity.domain

import spock.lang.Shared
import spock.lang.Unroll

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityOfferMatchDateMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def begin = new LocalDate(2016, 12, 19)
	@Shared def end = new LocalDate(2016, 12, 23)
	
	def offer
	
	def populate4Test() {
		def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
		def activity = new Activity(provider, "Bush Walking", 18, 80, 3)

		offer = new ActivityOffer(activity, this.begin, this.end, 30)
	}
	
	def 'success'() {
		expect:
		true == offer.matchDate(begin, end)
	}
	
	@Unroll('#begin_date, #end_date')
	def 'exceptions'() {
		when:
		offer.matchDate(begin_date, end_date)
		
		then: 'throws an exception'
		thrown(ActivityException)
		
		where:
			begin_date	| end_date
			null		| end
			begin		| null
	}
	
	def 'beginPlusOne'() {
		expect:
		false == offer.matchDate(begin.plusDays(1), end)
	}
	
	def 'beginMinusOne'() {
		expect:
		false == offer.matchDate(begin.minusDays(1), end)
	}
	
	def 'endPlusOne'() {
		expect:
		false == offer.matchDate(begin, end.plusDays(1))
	}
	
	def 'endMinusOne'() {
		expect:
		false == offer.matchDate(begin, end.minusDays(1))
	}

}

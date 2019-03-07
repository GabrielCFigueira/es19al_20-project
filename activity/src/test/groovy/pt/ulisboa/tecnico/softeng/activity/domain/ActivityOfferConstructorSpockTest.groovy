package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import org.joda.time.LocalDate;
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ActivityOfferConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def CAPACITY = 25;
    @Shared def MAX_AGE = 50;
	@Shared def MIN_AGE = 25;
	static def LocalDate BEGIN = new LocalDate(2016, 12, 19);
	static def LocalDate END = new LocalDate(2016, 12, 21);
    static def int AMOUNT = 30;
	@Shared def Activity ACTIVITY;

    static def LocalDate endDateImmediatelyBeforeBeginDate = BEGIN.minusDays(1)
    static def int amountIs0 = 0

    @Override
	def populate4Test() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
		ACTIVITY = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY);
	}

    def 'success' () {
        when: 'creating a new Activity Offer'
        def offer = new ActivityOffer(this.ACTIVITY, BEGIN, END, AMOUNT)

        then: 'should succeed'
        offer.getBegin() == BEGIN
        offer.getEnd() == END
        ACTIVITY.getActivityOfferSet().size() == 1
        offer.getNumberActiveOfBookings() == 0
        offer.getPrice() == AMOUNT
    }
    @Unroll('ActivityOffer:#activity, #begin, #end, #amount')
    def 'exceptions'() {
        when: 'creating an ActivityOffer with invalid arguments'
        new ActivityOffer(activity, begin, end, amount)

        then:
        thrown(ActivityException)

        where: 'cases where some arguments are null'
        activity | begin | end                               | amount
        null     | BEGIN | END                               | AMOUNT
        ACTIVITY | null  | END                               | AMOUNT
        ACTIVITY | BEGIN | null                              | AMOUNT
        ACTIVITY | BEGIN | END                               | amountIs0
        ACTIVITY | BEGIN | endDateImmediatelyBeforeBeginDate | AMOUNT
    }

    def 'successBeginDateEqualEndDate'(){
        when: 'creating an ActivityOffer'
        def offer = new ActivityOffer(this.ACTIVITY, BEGIN, BEGIN, 30)

        then: 'that ends and starts on the same day'
        offer.getBegin() == BEGIN
        offer.getEnd() == BEGIN
        ACTIVITY.getActivityOfferSet().size() == 1
        offer.getNumberActiveOfBookings() == 0
    }
}

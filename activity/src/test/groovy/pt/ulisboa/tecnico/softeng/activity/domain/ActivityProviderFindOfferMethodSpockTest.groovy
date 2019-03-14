package pt.ulisboa.tecnico.softeng.activity.domain


import org.joda.time.LocalDate
import spock.lang.Unroll
import spock.lang.Shared

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException


class ActivityProviderFindOfferMethodSpockTest extends SpockRollbackTestAbstractClass {

    def private static final MIN_AGE = 25
	def private static final MAX_AGE = 80
	def private static final CAPACITY = 25
	def private static final AGE = 40
	@Shared def private final begin = new LocalDate(2016, 12, 19)
    @Shared def private final end = new LocalDate(2016, 12, 21)

	def private provider
	def private activity
	def private offer


    @Override
    def populate4Test() {
        this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
		this.activity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
		this.offer = new ActivityOffer(this.activity, this.begin, this.end, 30)
    }

    @Unroll('testing correct ages')
    def success() {
        given:
        def offers = this.provider.findOffer(this.begin, this.end, _age)

        expect:
        1 == offers.size()
		offers.contains(this.offer)

        where:
        _age    | _
        AGE     | _
        MIN_AGE | _
        MAX_AGE | _
    }

    @Unroll('testing combinations of null dates')
    def 'null date'() {
        when:
        this.provider.findOffer(_begin, _end, AGE)

        then:
        thrown(ActivityException)

        where:
        _begin     | _end
        this.begin | null
        null       | this.end
    }

    @Unroll('testing incorrect ages')
    def insuccess() {
        when:
        def offers = this.provider.findOffer(this.begin, this.end, _age)

        then:
		offers.isEmpty()

        where:
        _age        | _
        MIN_AGE - 1 | _
        MAX_AGE + 1 | _
    }

    def 'empty activity set'() {
        given:
		def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")
		
        when:
        def offers = otherProvider.findOffer(this.begin, this.end, AGE)

        then:
		offers.isEmpty()
	}

	def 'empty activity offer set'() {
        given:
		def otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")
		new Activity(otherProvider, "Bush Walking", 18, 80, 25)

        when:
		def offers = otherProvider.findOffer(this.begin, this.end, AGE)

        then:
		offers.isEmpty()
	}

    @Unroll('testing offers\' size') 
	def 'match activity offers'() {
        given:
		new ActivityOffer(this.activity, this.begin, _end, 30)

        when:
		def offers = this.provider.findOffer(this.begin, _end, AGE)

        then:
		_number == offers.size()

        where:
        _end                 | _number
        this.end             | 2
        this.end.plusDays(1) | 1
	}

	def 'one match activity offer and other no capacity'() {
        given:
		def otherActivity = new Activity(this.provider, "Bush Walking", MIN_AGE, MAX_AGE, 1)
		def otherActivityOffer = new ActivityOffer(otherActivity, this.begin, this.end, 30)
		new Booking(this.provider, otherActivityOffer, "123456789", "IBAN")

        when:
		def offers = this.provider.findOffer(this.begin, this.end, AGE);

        then:
		1 == offers.size()
	}

}
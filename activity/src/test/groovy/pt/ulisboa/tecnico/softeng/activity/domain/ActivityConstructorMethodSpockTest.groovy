package pt.ulisboa.tecnico.softeng.activity.domain


import spock.lang.Unroll
import spock.lang.Shared


import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class ActivityConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    def private static final IBAN = "IBAN"
	def private static final NIF = "NIF"
	def private static final PROVIDER_NAME = "Bush Walking"
	def private static final MIN_AGE = 25
	def private static final MAX_AGE = 50
	def private static final CAPACITY = 30
	@Shared def private provider


    @Override
    def populate4Test() {
		this.provider = new ActivityProvider("XtremX", "ExtremeAdventure", NIF, IBAN)
	}

    @Unroll('testing argument combinations which should succeede')
    def success() {
        given:
        def activity = new Activity(this.provider, PROVIDER_NAME, _min, _max, _capacity)

        expect:
        activity.getCode().startsWith(this.provider.getCode())
		activity.getCode().length() > ActivityProvider.CODE_SIZE
		"Bush Walking" == activity.getName()
		_min == activity.getMinAge()
		_max == activity.getMaxAge()
		_capacity == activity.getCapacity()
		0 == activity.getActivityOfferSet().size()
		1 == this.provider.getActivitySet().size()

        where:
        _min    | _max    | _capacity
        MIN_AGE | MAX_AGE | CAPACITY
        18      | MAX_AGE | CAPACITY
        MIN_AGE | 99      | CAPACITY
        MIN_AGE | MIN_AGE | CAPACITY
        MIN_AGE | MAX_AGE | 1
    }

    @Unroll('testing argument combinations which shouldnt succeede')
    def insuccess() {
        when:
        new Activity(_provider, _name, _min, _max, _capacity);

        then:
        thrown(ActivityException)

        where:
        _provider | _name         | _min         | _max    | _capacity
        null      | PROVIDER_NAME | MIN_AGE      | MAX_AGE | CAPACITY
        provider  | null          | MIN_AGE      | MAX_AGE | CAPACITY
        provider  | "    "        | MIN_AGE      | MAX_AGE | CAPACITY
        provider  | PROVIDER_NAME | 17           | MAX_AGE | CAPACITY
        provider  | PROVIDER_NAME | MIN_AGE      | 100     | CAPACITY
        provider  | PROVIDER_NAME | MAX_AGE + 10 | MAX_AGE | CAPACITY
        provider  | PROVIDER_NAME | MAX_AGE + 1  | MAX_AGE | CAPACITY
        provider  | PROVIDER_NAME | MIN_AGE      | MAX_AGE | 0
    }

}

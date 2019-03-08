package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import spock.lang.Shared
import spock.lang.Unroll

class AdventureConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {

    def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
	}

	def 'success'() {
        when:	'creating a new adventure'
		    adventure = new Adventure(broker, begin, end, client, MARGIN)

        then:	'should succeed'
		    broker == adventure.getBroker()
		    begin == adventure.getBegin()
		    end == adventure.getEnd()
		    client == adventure.getClient()
		    MARGIN == adventure.getMargin()
		    broker.getAdventureSet().contains(adventure) == true

		    adventure.getPaymentConfirmation() == null
		    adventure.getActivityConfirmation() == null
		    adventure.getRoomConfirmation() == null
	}


	@Unroll('Adventure: #broker, #begin, #end, #client, #margin')
	def 'exceptions'() {
		when:	'creating an adventure with invalid arguments'
		    new Adventure(_broker, _begin, _end, _client, _margin)
		then:	'throws exceptions'
			thrown(BrokerException)
		where:
			_broker | _begin | _end | _client | _margin
			null | begin | end | client | MARGIN
			broker | null | end | client | MARGIN
			broker | begin | null | client | MARGIN
			broker | begin | begin.minusDays(1) | client | MARGIN
	}

	def 'successEqual18'() {
        when:	'creating a new adventure with the minimum permitted age'
		    Adventure adventure = new Adventure(broker, begin, end,
				new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 18), MARGIN)

        then:	'should succeed'
		    broker == adventure.getBroker()
		    begin == adventure.getBegin()
		    end == adventure.getEnd()
		    18 == adventure.getAge()
		    CLIENT_IBAN == adventure.getIban()
		    MARGIN == adventure.getMargin()
		    broker.getAdventureSet().contains(adventure) == true

		    adventure.getPaymentConfirmation() == null
		    adventure.getActivityConfirmation() == null
		    adventure.getRoomConfirmation() == null
	}

	def 'negativeAge'() {
        when:	'creating a new adventure with less than the minimum permitted age'
		    Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 17)
		    new Adventure(broker, begin, end, c, MARGIN)
        then:	'throws exceptions'
            thrown(BrokerException)
	}

	def 'successEqual100'() {
        when:	'creating a new adventure with the maximum permitted age'
		    Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 100)
		    Adventure adventure = new Adventure(broker, begin, end, c, MARGIN)
        then:	'should succeed'
		    broker == adventure.getBroker()
		    begin == adventure.getBegin()
		    end == adventure.getEnd()
		    100 == adventure.getAge()
		    CLIENT_IBAN == adventure.getIban()
		    MARGIN == adventure.getMargin()
		    broker.getAdventureSet().contains(adventure) == true

		    adventure.getPaymentConfirmation() == null
		    adventure.getActivityConfirmation() == null
		    adventure.getRoomConfirmation() == null
	}

	def 'over100'() {
        when:	'creating a new adventure with more than the maximum permitted age'
		    Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 101)
		    new Adventure(broker, begin, end, c, MARGIN)
        then:	'throws exceptions'
            thrown(BrokerException)
	}

	def 'negativeAmount'() {
        when:	'creating a new adventure with invalid amount'
		    new Adventure(broker, begin, end, client, -100)
        then:	'throws exceptions'
            thrown(BrokerException)
	}

	def 'success1Amount'() {
        when:	'creating an adventure with 1 as margin'
		    Adventure adventure = new Adventure(broker, begin, end, client, 1)
        then:	'should succeed'
		    broker == adventure.getBroker()
		    begin == adventure.getBegin()
		    end == adventure.getEnd()
		    20 == adventure.getAge()
		    CLIENT_IBAN == adventure.getIban()
		    1 == adventure.getMargin()
		    broker.getAdventureSet().contains(adventure) == true

		    adventure.getPaymentConfirmation() == null
		    adventure.getActivityConfirmation() == null
		    adventure.getRoomConfirmation() == null
	}

	def 'zeroAmount'() {
        when:	'creating an adventure with 0 as margin'
		    new Adventure(broker, begin, end, client, 0)
        then:	'throws exceptions'
            thrown(BrokerException)
	}

	def 'successEqualDates'() {
        when:	'creating an adventure with equal dates'
		    Adventure adventure = new Adventure(broker, begin, begin, client, MARGIN)

        then:	'should succeed'
		    broker == adventure.getBroker()
		    begin == adventure.getBegin()
		    begin == adventure.getEnd()
		    20 == adventure.getAge()
		    CLIENT_IBAN == adventure.getIban()
		    MARGIN == adventure.getMargin()
		    broker.getAdventureSet().contains(adventure) == true

		    adventure.getPaymentConfirmation() == null
		    adventure.getActivityConfirmation() == null
		    adventure.getRoomConfirmation() == null
	}

}
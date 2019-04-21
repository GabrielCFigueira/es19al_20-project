package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import spock.lang.Shared
import spock.lang.Unroll

class AdventureConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def broker
    def client

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface())
    }

    @Unroll('success #label: #begin, #end, #age, #margin')
    def 'success'() {
        given: 'a client'
        client = getClientWithAge(age)

        when: 'an adventure is created'
        def adventure = new Adventure(broker, begin, end, client, margin, CarInterface.Type.CAR, HotelInterface.Type.SINGLE)

        then: 'all its attributes are correctly set'
        adventure.getBroker() == broker
        with(adventure) {
            getBroker() == broker
            getBegin() == begin
            getEnd() == end
            getClient() == client
            getMargin() == margin
            getAge() == age
            getIban().equals(iban)

            getPaymentConfirmation() == null
            getActivityConfirmation() == null
            getRoomConfirmation() == null
        }
        broker.getAdventureSet().contains(adventure)

        where:
        begin | end   | margin | age | label
        BEGIN | END   | MARGIN | AGE | 'normal'
        BEGIN | END   | MARGIN | 18  | '18 years old'
        BEGIN | END   | MARGIN | 100 | '100 years old'
        BEGIN | END   | 1000   | AGE | 'margin 1000'
        BEGIN | BEGIN | MARGIN | AGE | 'begin begin'
    }

    @Unroll('#label')
    def 'invalid arguments'() {
        given: 'a client'
        client = getClientWithAge(age)

        when: 'an adventure is created with invalid arguments'
        new Adventure(brok, begin, end, client, margin, CarInterface.Type.CAR, HotelInterface.Type.SINGLE)

        then: 'an exception is thrown'
        thrown(BrokerException)

        where:
        brok   | begin | end                | age | margin | label
        null   | BEGIN | END                | 20  | MARGIN | 'broker is null'
        broker | null  | END                | 20  | MARGIN | 'begin date is null'
        broker | BEGIN | null               | 20  | MARGIN | 'end date is null'
        broker | BEGIN | BEGIN.minusDays(1) | 20  | MARGIN | 'end date before begin date'
        broker | BEGIN | END                | 17  | MARGIN | 'client is 17 years old'
        broker | BEGIN | END                | 20  | 0      | 'margin is zero'
        broker | BEGIN | END                | 20  | -100000| 'margin is negative'
        broker | BEGIN | END                | -1  | MARGIN | 'client is null'
    }

    def getClientWithAge(def age) {
        if (age != -1)
            return new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, age)
        else
            return null
    }
}

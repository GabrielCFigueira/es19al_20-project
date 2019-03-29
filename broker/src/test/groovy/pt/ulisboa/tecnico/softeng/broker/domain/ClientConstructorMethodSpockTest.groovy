package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class ClientConstructorMethodSpockTest extends SpockRollbackTestAbstractClass implements SpockBaseTest {

	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
	}


	def 'success'() {
		when: 'creating a new client'
		Client client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

		then: 'should succeed'
		CLIENT_IBAN == client.getIban()
		CLIENT_NIF == client.getNif()
		AGE == client.getAge()
	}

	@Unroll('Client: #_broker, #_iban, #_nif, #_drivingLicense, #_age')
	def 'exceptions'() {
		when: 'creating a client with wrong parameters'
		new Client(_broker, _iban, _nif, _drivingLicense, _age)

		then: 'throws an exception'
		thrown(BrokerException)

		where:
		_broker     | _iban       | _nif       | _drivingLicense | _age
		null        | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | AGE
		broker      | null        | CLIENT_NIF | DRIVING_LICENSE | AGE
		broker      | "   "       | CLIENT_NIF | DRIVING_LICENSE | AGE
		broker      | CLIENT_IBAN | null       | DRIVING_LICENSE | AGE
		broker      | CLIENT_IBAN | "    "     | DRIVING_LICENSE | AGE
		broker      | CLIENT_IBAN | CLIENT_NIF | DRIVING_LICENSE | -1
		broker      | CLIENT_IBAN | CLIENT_NIF | "      "        | AGE

	}

	// JFF: this test seems to be incomplete/incorrect: what is it testing?
	def 'client exists with same IBAN'() {
		expect: 'that it doesnt work'
		new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		new Client(broker, CLIENT_IBAN, "OTHER_NIF", DRIVING_LICENSE + "1", AGE)
	}

	def 'client exists with same NIF'(){
		given: 'a new client'
		def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		when: 'creating another client with same NIF'
		new Client(broker, "OTHER_IBAN", CLIENT_NIF, DRIVING_LICENSE + "1", AGE)
		then: 'throws exception'
		thrown(BrokerException)
		client == broker.getClientByNIF(CLIENT_NIF)
	}

	def 'client with null driving license'(){
		when: 'creating a client with a null driving license'
		def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, null, AGE)
		then: 'succeeds'
		CLIENT_IBAN == client.getIban()
		CLIENT_NIF == client.getNif()
		AGE == client.getAge()
		null == client.getDrivingLicense()
	}

}

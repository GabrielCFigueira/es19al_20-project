package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class BrokerConstructorMethodSpockTest extends SpockRollbackTestAbstractClass implements SpockBaseTest {
	
	def populate4Test() {}
	
	def 'success'() {
		when: 'creating a Broker'
		def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		
		then:
		BROKER_CODE == broker.getCode()
		BROKER_NAME == broker.getName()
		0 == broker.getAdventureSet().size()
		true == FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
	}
	
	@Unroll('Broker: #code | #name | #nif_seller | #nif_buyer | #iban')
	def 'exceptions'(){
		when: 'creating a Broker with invalid arguments'
		new Broker(code, name, nif_seller, nif_buyer, iban)
		
		then: 'throws an exception'
		thrown(BrokerException)
		
		and:
		0 == FenixFramework.getDomainRoot().getBrokerSet().size()
		
		where:
			code		| name			| nif_seller			| nif_buyer		        | iban
			null		| BROKER_NAME	| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			""			| BROKER_NAME	| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			"  "		| BROKER_NAME	| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| null			| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| ""			| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| "    "		| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| BROKER_NAME	| null					| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| BROKER_NAME	| "    "				| NIF_AS_BUYER	        | BROKER_IBAN
			BROKER_CODE	| BROKER_NAME	| BROKER_NIF_AS_SELLER	| null			        | BROKER_IBAN
			BROKER_CODE	| BROKER_NAME	| BROKER_NIF_AS_SELLER	| "   "			        | BROKER_IBAN
			BROKER_CODE	| BROKER_NAME	| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | null
			BROKER_CODE	| BROKER_NAME	| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER	        | "    "
			BROKER_CODE	| BROKER_NAME	| BROKER_NIF_AS_SELLER	| BROKER_NIF_AS_SELLER	| BROKER_IBAN
	}
	
	def 'uniqueCode'() {
		given: 'creating a normal Broker'
		def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		
		when: 'creating a Broker with invalid BROKER_NAME'
		new Broker(BROKER_CODE, "WeExploreX", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		
		then: 'throws an exception'
		thrown(BrokerException)
		
		and:
		1 == FenixFramework.getDomainRoot().getBrokerSet().size()
		true == FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
	}
	
	@Unroll('Broker: #nif_as_buyer1 | #broker_nif_as_seller | #nif_as_buyer2')
	def 'exceptions and assert'(){
		given: 'creating a Broker '
		new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, nif_as_buyer1, BROKER_IBAN)

		when: 'creating another Broker'
		new Broker(BROKER_CODE, BROKER_NAME, broker_nif_as_seller, nif_as_buyer2, BROKER_IBAN)
		
		then: 'throws an exception'
		thrown(BrokerException)
		
		and:
		1 == FenixFramework.getDomainRoot().getBrokerSet().size()
		
		where:
			nif_as_buyer1	| broker_nif_as_seller	| nif_as_buyer2
			"123456789"		| BROKER_NIF_AS_SELLER	| NIF_AS_BUYER
			NIF_AS_BUYER	| "123456789"			| NIF_AS_BUYER
			NIF_AS_BUYER	| NIF_AS_BUYER			| "123456789"
			
	}
}
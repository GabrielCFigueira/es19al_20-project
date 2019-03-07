package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

public class ClientContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def CLIENT_NAME = "António"

	@Shared private Bank bank

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
	}
	
	def 'success'() {
		when:
		def client = new Client(bank, CLIENT_NAME)
		
		then:
		assert CLIENT_NAME == client.getName()
		assert client.getID().length() >= 1
		assert true == bank.getClientSet().contains(client)
	}
	
	@Unroll('Client: #cbank | #cname')
	def 'exceptions'(){
		when: 'creating a Client with invalid arguments'
		new Client(cbank, cname)
		
		then: 'throws an exception'
		thrown(BankException)
		
		where:
			cbank	| cname
			null	| CLIENT_NAME
			bank	| null
			bank	| "    "
			bank	| ""
	}
}

package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class AccountDepositMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account


	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
	}

	def 'success'() {
        when:	'creating a reference'
		    def reference = this.account.deposit(50).getReference()
        
        then:	'should succeed'
            50 == account.getBalance()
		    def operation = this.bank.getOperation(reference)
		    operation != null
		    Operation.Type.DEPOSIT == operation.getType()
		    account == operation.getAccount()
		    50 == operation.getValue()
	}

	@Unroll('deposit: #money')
	def 'exceptions'() {
		when:	'depositing with wrong parameters'
			account.deposit(money)
		then:	'throws an exception'
			thrown(BankException)
		where:
			money | _
			0 	  | _
			-100  | _
	}

	def 'one amount'() {
		expect: "that the deposit is successful"
			account.deposit(1);
	}

}

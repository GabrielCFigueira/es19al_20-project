package pt.ulisboa.tecnico.softeng.bank.services.local


import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class BankInterfaceCancelPaymentSpockTest extends SpockRollbackTestAbstractClass {
    def private bank
	def private account
	def private reference


	@Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01")
		def client = new Client(this.bank, "António")
		this.account = new Account(this.bank, client)
		this.reference = this.account.deposit(100).getReference()
	}

	def success() {
        when:
		def newReference = BankInterface.cancelPayment(this.reference)
		
        then:
        null != this.bank.getOperation(newReference)
	}

    @Unroll('testing incorrect references')
	def 'reference'() {
        when:
		BankInterface.cancelPayment(_reference)

        then:
        thrown(BankException)

        where:
        _reference | _
        null       | _
        ""         | _
        "XPTO"     | _
	}

}
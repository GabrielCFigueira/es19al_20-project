package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Shared
import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	@Shared def account

	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
	}

	def 'success'() {
        when:	'creating a new operation'
		    def operation = new Operation(Type.DEPOSIT, this.account, 1000);

        then:	'should succeed'
		    operation.getReference().startsWith(this.bank.getCode()) == true
		    operation.getReference().length() > Bank.CODE_SIZE
		    Type.DEPOSIT == operation.getType()
		    account == operation.getAccount()
		    1000 == operation.getValue()
		    operation.getTime() != null
		    operation == bank.getOperation(operation.getReference())
	}

	@Unroll('Operation: #type, #account, #money')
	def 'exceptions'() {
		when: 'creating a new operation with invalid arguments'
		    new Operation(type, _account, money)
		then:	'throws an exception'
			thrown(BankException)
		where:
			type 		  | _account | money
			null          |  account | 1000
			Type.WITHDRAW | null     | 1000
			Type.DEPOSIT  | account  | 0
			Type.WITHDRAW | account  | -1000
	}

	def 'one amount'() {
        when:	'creating a new operation'
		    def operation = new Operation(Type.DEPOSIT, account, 1)
        then:	'should succeed'
		    operation == bank.getOperation(operation.getReference())
	}

}

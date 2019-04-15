package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		account = new Account(bank, client)
	}

	def 'success'() {
		when: 'when creating an operation'
		def operation = new OperationDeposit(account, 1000)

		then: 'the object should hold the proper values'
		with(operation) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getType() == "DEPOSIT"
			getAccount() == account
			getValue() == 1000
			getTime() != null
			bank.getOperation(getReference()) == operation
		}
	}


	@Unroll('operation: #type, #acc, #value')
	def 'withdrawExceptions'() {
		when: 'when creating an invalid operation'
		new OperationWithdraw(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc     | value
		null    | 1000
		account | 0
		account | -1000
	}

	@Unroll('operation: #type, #acc, #value')
	def 'depositExceptions'() {
		when: 'when creating an invalid operation'
		new OperationDeposit(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc     | value
		account | 0
		account | -1000
	}

	def 'one amount'() {
		when:
		def operation = new OperationDeposit(account, 1)

		then:
		bank.getOperation(operation.getReference()) == operation
	}
}

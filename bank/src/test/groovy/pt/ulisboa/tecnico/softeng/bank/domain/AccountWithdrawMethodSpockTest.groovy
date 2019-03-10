package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

import spock.lang.Unroll

class AccountWithdrawMethodSpockTest extends SpockRollbackTestAbstractClass {
	private Bank bank
	private Account account

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
		account.deposit(100)
	}
	
	def 'success'() {
		when:
		def reference = account.withdraw(40).getReference()
		def operation = bank.getOperation(reference)
		
		then:
		60 == account.getBalance()
		
		null != operation
		Operation.Type.WITHDRAW == operation.getType()
		account == operation.getAccount()
		40 == operation.getValue()
	}
	
	@Unroll('#value')
	def 'exceptions'(){
		when:
		account.withdraw(value)
		
		then: 'throws an exception'
		thrown(BankException)
		
		where:
			value	| _
			-20		| _
			0		| _
			101		| _
			150		| _
	}
	
	@Unroll('#value, #assert_value')
	def 'asserts'(){
		when:
		account.withdraw(value)
		
		then:
		assert_value == account.getBalance()
		
		where:
			value	| assert_value
			1		| 99
			100		| 0
	}

}

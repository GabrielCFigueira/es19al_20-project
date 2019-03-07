package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

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
		assert 60 == account.getBalance()
		
		assert null != operation
		assert Operation.Type.WITHDRAW == operation.getType()
		assert account == operation.getAccount()
		assert 40 == operation.getValue()
	}
	
	def 'negativeAmount'() {
		when:
		account.withdraw(-20)
		
		then: 'throws an exception'
		thrown(BankException)
	}
	
	def 'zeroAmount'() {
		when:
		account.withdraw(0)
		
		then: 'throws an exception'
		thrown(BankException)
	}
	
	def 'oneAmount'() {
		when:
		account.withdraw(1)
		
		then:
		assert 99 == this.account.getBalance()
	}
	
	def 'equalToBalance'() {
		when:
		account.withdraw(100)
		
		then:
		assert 0 == this.account.getBalance()
	}
	
	def 'equalToBalancePlusOne'() {
		when:
		account.withdraw(101)
		
		then: 'throws an exception'
		thrown(BankException)
	}
	
	def 'moreThanBalance'() {
		when:
		account.withdraw(150)
		
		then: 'throws an exception'
		thrown(BankException)
	}

}

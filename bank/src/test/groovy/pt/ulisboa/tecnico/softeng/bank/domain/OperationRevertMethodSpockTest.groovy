package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account
	def client

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		client = new Client(bank,'António')
		account = new Account(bank,client)
	}

	def 'revert deposit'() {
		given: 'a deposit operation'
		def reference = account.deposit(100).getReference()
		def operation = bank.getOperation(reference)

		when: 'when reverting the deposit'
		def newReference = operation.revert()

		then: 'account should have have balance as before'
		account.getBalance() == 0

		and: 'a new operation is added'
		bank.getOperation(newReference) != null

		and: 'the initial operation is not removed'
		bank.getOperation(reference) != null
	}

	def 'revert withdraw'() {
		given: 'given a deposit operation'
		account.deposit(1000)
		def reference = this.account.withdraw(100).getReference()
		def operation = this.bank.getOperation(reference)

		when: 'when reverting the operation'
		def newReference = operation.revert()

		then: 'account should have the balance as before'
		1000 == this.account.getBalance()

		and: 'a new operation is added'
		this.bank.getOperation(newReference) != null

		and: 'the initial operation is not removed'
		this.bank.getOperation(reference) != null
	}

	def 'revert transfer'() {
		given: 'given a deposit operation'
		account.deposit(1000)

		def accountToTransfer = new Account(bank, client)
		def reference = this.account.transfer(100, accountToTransfer).getReference()
		def operation = this.bank.getOperation(reference)

		when: 'when reverting the operation'
		def newReference = operation.revert()

		then: 'accounts should have the balances as before'
		1000 == this.account.getBalance()
		0 == accountToTransfer.getBalance()

		and: 'a new operation is added'
		this.bank.getOperation(newReference) != null

		and: 'the initial operation is not removed'
		this.bank.getOperation(reference) != null
	}

	def 'cannot revert transfer again'() {
		given: 'given a deposit operation'
		account.deposit(1000)

		def accountToTransfer = new Account(bank, client)
		def reference = this.account.transfer(100, accountToTransfer).getReference()
		def operation = this.bank.getOperation(reference)

		when: 'when reverting the operation twice'
		def newReference = operation.revert()
		def newerReference = operation.revert()

		then: 'it should throw an exception'
		thrown(BankException)

	}

}

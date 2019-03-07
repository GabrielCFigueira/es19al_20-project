package pt.ulisboa.tecnico.softeng.bank.domain

import spock.lang.Unroll


import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def client

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		client = new Client(bank, "António")
	}


	def 'success'() {
		when: 'creating a new account and using IBAN to get it'
		Account account = new Account(bank, client)
		Account result = bank.getAccount(account.getIBAN())

		then: 'accounts are the same'
		account == result
	}

	@Unroll('getAccount: #iban')
	def 'exceptions'() {
		when: 'getting an account with wrong IBAN'
		bank.getAccount(iban)

		then: 'throws an exception'
		thrown(BankException)

		where:
		iban   | _
		null   | _
		""     | _
		"    " | _
	}

	def 'checking for a non existent account'(){
		expect: 'that account doesnt exist'
		null == bank.getAccount("XPTO")
	}



	def 'checking if several account do not match'() {
		when: 'creating two accounts with same parameters'
		new Account(bank, client)
		new Account(bank, client)

		then: 'account still doesnt match'
		null == bank.getAccount("XPTO")

	}

}

package pt.ulisboa.tecnico.softeng.bank.domain


import spock.lang.Unroll
import spock.lang.Shared
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

// JFF: class name different from file name
class AccountContructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def client

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		client = new Client(bank, "António")
	}


	def 'success'() {
		when:'when creating account'
			def account = new Account(bank, client)
		then:'should succeed'
			bank == account.getBank()
			client == account.getClient()
			true == account.getIBAN().startsWith(bank.getCode())
			0 == account.getBalance()
			1 == bank.getAccountSet().size()
			true == bank.getClientSet().contains(client)
	}
	
	@Unroll('Account:#bank_arg,#client_arg')
	def 'exceptions'(){
		when:'when creating new account'
			new Account(bank_arg,client_arg)
		then:'throws BankException'
			thrown(BankException)
		where:
			bank_arg	| client_arg
			null		| client
			bank		| null		
			
	}


	def 'clientDoesNotBelongToBank'() {
		given:'given an allien'
			def allien = new Client(new Bank("MoneyPlus", "BK02"), "António")
		when:'when creating a new Account'
			new Account(bank, allien)
		then:'throws BankException'
			thrown(BankException)
	}	

}

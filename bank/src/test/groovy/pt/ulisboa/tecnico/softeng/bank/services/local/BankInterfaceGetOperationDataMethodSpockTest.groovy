package pt.ulisboa.tecnico.softeng.bank.services.local


import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData

class BankInterfaceGetOperationDataMethodSpockTest extends SpockRollbackTestAbstractClass {
	def AMOUNT = 100
	def bank
	def account
	def reference

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
		reference = account.deposit(AMOUNT).getReference()
	}

	def 'success'() {
		when:'testing Get Operation Data Interface'
			def data = BankInterface.getOperationData(reference)
		
		then:'should succeed'
			reference == data.getReference()
			account.getIBAN() == data.getIban()
			Type.DEPOSIT.name() == data.getType()
			AMOUNT == data.getValue()
			null != data.getTime()
	}
	
	@Unroll('BankInterfaceGetOperationData:#op')
	def 'exceptions'(){
		when: 'getting Operation data with wrong argument'
			BankInterface.getOperationData(op)
		
		then: 'throws BankException'
			thrown(BankException)
		
		where:
			op		| _
			null	| _
			""		| _
			"XPTO"	| _
		
		
	}
			
}





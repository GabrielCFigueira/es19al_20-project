package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Unroll

class AccountTransferMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank
	def account
    def accountTarget

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
        def clientTarget = new Client(bank,'José')

		account = new Account(bank, client)
        accountTarget = new Account(bank, clientTarget)
	}

	@Unroll('success Transfer, #label: #amnt, #balance')
	def 'success'() {
		when: 'when transfer an amount to an account'
        account.deposit(100)
		String reference = account.transfer(amnt,accountTarget).getReference()

		then: 'the account is updated appropriately'
		firstBalance == account.getBalance()
        secondBalance == accountTarget.getBalance()
		Operation operation = bank.getOperation(reference)
		operation != null
		operation.getType() == "TRANSFER"
		operation.getAccount() == account

		where:  
		label              | amnt | firstBalance     |     secondBalance       
		'fifty'            | 50   |    50            |       50                
		'ten amount'       | 10   |    90            |       10                
	}

	@Unroll('Transfer: #label')
	def 'throwing exception'() {
		when: 'when transfer an invalid amount'
		account.transfer(amnt,accountTarget)

		then: 'throw an exception'
		thrown(BankException)

		where:
		amnt | label
		0    | 'zero amount'
		-100 | 'negative amount'
        101  | 'equal to balance plus one'
		150  | 'more than balance'
	}
}

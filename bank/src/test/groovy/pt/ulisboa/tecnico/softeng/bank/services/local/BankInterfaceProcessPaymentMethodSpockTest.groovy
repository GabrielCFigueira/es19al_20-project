package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodSpockTest extends SpockRollbackTestAbstractClass {
	def TRANSACTION_SOURCE='ADVENTURE'
	def TRANSACTION_REFERENCE='REFERENCE'
	def bank
	def account
	def accountCompany
	@Shared def iban
	@Shared def ibanCompany

	@Override
	def populate4Test() {
		bank = new Bank('Money','BK01')
		def client = new Client(bank,'António')
		def company = new Client(bank, 'Empresa')
		account = new Account(bank, client)
		accountCompany = new Account(bank, company)
		account.deposit(500000)
		iban = account.getIBAN()
		ibanCompany = accountCompany.getIBAN()
	}

	def 'success'() {
		when: 'a payment is processed for this account'
		def newReference = BankInterface.processPayment(new BankOperationData(iban, ibanCompany,  100.0, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation occurs and a reference is generated'
		newReference != null
		newReference.startsWith('BK01')
		bank.getOperation(newReference) != null
		bank.getOperation(newReference).getType() == "TRANSFER"
		bank.getOperation(newReference).getValue() == 100000
		account.getBalance() == 400000
		accountCompany.getBalance() == 100000
	}

	def 'success two banks'() {
		given:
		def otherBank = new Bank('Money','BK02')
		def otherClient = new Client(otherBank,'Manuel')
		def otherAccount = new Account(otherBank,otherClient)
		def otherIban = otherAccount.getIBAN()
		otherAccount.deposit(1000000)

		when:
		String reference = BankInterface.processPayment(new BankOperationData(otherIban, ibanCompany, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		
		then:
		otherBank.getOperation(reference) != null
		bank.getOperation(reference) == null
		otherAccount.getBalance() == 900000
		accountCompany.getBalance() == 100000

		when:
		BankInterface.processPayment(new BankOperationData(iban, ibanCompany, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + 'PLUS'))

		then:
		account.getBalance() == 400000
		accountCompany.getBalance() == 200000
	}

	def 'redo an already payed'() {
		given: 'a payment to the account'
		def firstReference = BankInterface.processPayment(new BankOperationData(iban, ibanCompany, 100.0, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		when: 'when there is a second payment for the same reference'
		def secondReference = BankInterface.processPayment(new BankOperationData(iban, ibanCompany, 100.0, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'the operation is idempotent'
		secondReference == firstReference
		and: 'does not withdraw twice'
		account.getBalance() == 400000
		accountCompany.getBalance() == 100000
	}

	def 'one amount'() {
		when: 'a payment of 1'
		BankInterface.processPayment(new BankOperationData(this.iban, ibanCompany, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then:
		account.getBalance() == 499000
		accountCompany.getBalance() == 1000

	}


	@Unroll('bank operation data, process payment: #ibn, #ibnCompany, #val')
	def 'problem process payment'() {
		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(ibn, ibnCompany, val, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'throw exception'
		thrown(BankException)

		where: 'for incorrect arguments'
		ibn     | ibnCompany  | val 	| label
		null    | ibanCompany | 100000  | 'null iban'
		'  '    | ibanCompany |  100000 | 'blank iban'
		''      | ibanCompany |  100000 | 'empty iban'
		iban    | null        | 100000  | 'null iban of company'
		iban    | '  '        |  100000 | 'blank iban of company'
		iban    | ''          |  100000 | 'empty iban of company'
		iban    | ibanCompany |  0   	| '0 amount'
		'other' | ibanCompany |  0   	| 'account does not exist for other iban'
		iban    | 'other'     |  0   	| 'account does not exist for other iban of company'
	}

	def 'no banks'() {
		given: 'remove all banks'
		bank.delete()

		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(iban, "", 100000, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'an exception is thrown'
		thrown(BankException)
	}

	def 'successfulTransfer'() {
		given: 'creating two different accounts'
			def targetClient = new Client(bank,'Pestana')
			def targetAccount = new Account(bank,targetClient)
			def otherIban = targetAccount.getIBAN()

		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(iban, otherIban, 400, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'values check up'
			targetAccount.getBalance() == 400000
			account.getBalance() == 100000
	}

	def 'transferWithRepeatedAccount'() {
		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(iban, iban, 400, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'throws exception'
			thrown(BankException)
	}

	def 'transferWithoutEnoughBalance'() {
		given: 'creating two different accounts'
			def targetClient = new Client(bank,'Pestana')
			def targetAccount = new Account(bank,targetClient)
			def otherIban = targetAccount.getIBAN()

		when: 'process payment'
		BankInterface.processPayment(
				new BankOperationData(iban, otherIban, 600, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'throws exception'
			thrown(BankException)
	}
}

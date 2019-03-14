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

public class BankInterfaceProcessPaymentMethodTest extends SpockRollbackTestAbstractClass {
    @Shared def TRANSACTION_SOURCE = "ADVENTURE"
	@Shared def TRANSACTION_REFERENCE = "REFERENCE"

	def bank
    def account
	@Shared def iban
    
    @Override
	def populate4Test() {
		bank = new Bank("Money", "BK01")
		def client = new Client(bank, "António")
		account = new Account(bank, client)
		iban = account.getIBAN()
		account.deposit(500)
	}

	def 'success'() {
		when: 'testing Process Payment Interface'
		account.getIBAN()
		def newReference = BankInterface
				.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		
		then: 'Should succeed'
		null != newReference
		true == newReference.startsWith("BK01")

		null != bank.getOperation(newReference)
		bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW

	}
	def 'successTwoBanks'() {
		when: 'Testing 2 banks process 2 payments'
		def otherBank = new Bank("Money", "BK02")
		def otherClient = new Client(otherBank, "Manuel")
		def otherAccount = new Account(otherBank, otherClient)
		def otherIban = otherAccount.getIBAN()
		otherAccount.deposit(1000)

		BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		BankInterface.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))	
		
		then: 'should succeed'
		900 == otherAccount.getBalance()
		400 == account.getBalance()
	}

	def 'redoAnAlreadyPayed'() {
		when: 'a payment is made twice'
		account.getIBAN()
		def firstReference = BankInterface
				.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		def secondReference = BankInterface
				.processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		
		then: 'should only be executed once if the reference is the same, thus only the first execution is considered'
		firstReference == secondReference
		400 == account.getBalance()
	}
	
	def 'oneAmount'() {
		when: '(edge case) one unit is paid'
			BankInterface.processPayment(new BankOperationData(iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'only one unit is removed'
			499 == account.getBalance()
	}

	@Unroll('InterfaceProcessPayment:#_iban, #_amount, #_transaction_source, #_transaction_reference' )
	def 'exceptions'() {
		when:
		BankInterface.processPayment(new BankOperationData(_iban, _amount, _transaction_source, _transaction_reference))

		then:
		thrown(BankException)

		where:
		_iban      | _amount | _transaction_source | _transaction_reference
		null       | 100     | TRANSACTION_SOURCE  | TRANSACTION_REFERENCE
		"    "     | 100     | TRANSACTION_SOURCE  | TRANSACTION_REFERENCE
		iban       | 0       | TRANSACTION_SOURCE  | TRANSACTION_REFERENCE
		"other"    | 100     | TRANSACTION_SOURCE  | TRANSACTION_REFERENCE
	}
}
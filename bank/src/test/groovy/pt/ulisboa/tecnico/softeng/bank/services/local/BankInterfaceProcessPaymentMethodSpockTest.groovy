package pt.ulisboa.tecnico.softeng.bank.services.local;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import pt.ulisboa.tecnico.softeng.bank.domain.Account;
import pt.ulisboa.tecnico.softeng.bank.domain.Bank;
import pt.ulisboa.tecnico.softeng.bank.domain.Client;
import pt.ulisboa.tecnico.softeng.bank.domain.Operation;
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData;

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

public class BankInterfaceProcessPaymentMethodTest extends SpockRollbackTestAbstractClass {
    @Shared String TRANSACTION_SOURCE = "ADVENTURE";
	@Shared String TRANSACTION_REFERENCE = "REFERENCE";

	@Shared Bank bank;
    @Shared Account account;
	@Shared String iban;
    
    @Override
	def populate4Test() {
		this.bank = new Bank("Money", "BK01");
		def client = new Client(this.bank, "António");
		this.account = new Account(this.bank, client);
		this.iban = this.account.getIBAN();
		this.account.deposit(500);
	}

	def 'success'() {
		when: 'testing Porcess Payment Interface'
		this.account.getIBAN();
		def newReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		
		then: 'Should succeed'
		assertNotNull newReference
		assertTrue newReference.startsWith("BK01")

		assertNotNull(this.bank.getOperation(newReference))
		this.bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW

	}
	def 'successTwoBanks'() {
		when: 'Testing 2 banks process 2 payments'
		def otherBank = new Bank("Money", "BK02")
		def otherClient = new Client(otherBank, "Manuel")
		def otherAccount = new Account(otherBank, otherClient);
		def otherIban = otherAccount.getIBAN();
		otherAccount.deposit(1000);

		BankInterface.processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		BankInterface.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))	
		
		then: 'should succeed'
		assertEquals(900, otherAccount.getBalance(), 0.0d)
		assertEquals(400, this.account.getBalance(), 0.0d)
	}

	def 'redoAnAlreadyPayed'() {
		when: 'a payment is made twice'
		this.account.getIBAN()
		def firstReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		def secondReference = BankInterface
				.processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
		
		then: 'should only be executed once if the reference is the same, thus only the first execution is considered'
		assertEquals(firstReference, secondReference)
		assertEquals(400, this.account.getBalance(), 0.0d)
	}
	
	def 'oneAmount'() {
		when: '(edge case) one unit is paid'
			BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

		then: 'only one unit is removed'
			assertEquals(499, this.account.getBalance(), 0.0d);
	}

	@Unroll('InterfaceProcessPayment:#iban, #amount, #transaction_source, #transaction_reference' )
	def 'exceptions'() {
		when:
		BankInterface.processPayment(new BankOperationData(iban, amount, transaction_source, transaction_reference))

		then:
		thrown(BankException)

		where:
		iban      | amount | transaction_source | transaction_reference
		null      | 100    | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
		"    "    | 100    | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
		this.iban | 0      | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
		"other"   | 100    | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
	}
}
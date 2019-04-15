package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework

class BankPersistenceSpockTest extends SpockPersistenceTestAbstractClass {
	def BANK_NAME = 'Money'
	def BANK_CODE = 'BK01'
	def CLIENT_NAME = 'João dos Anzóis'

	@Override
	def whenCreateInDatabase() {
		def bank = new Bank(BANK_NAME,BANK_CODE)
		def client = new Client(bank,CLIENT_NAME)
		def account = new Account(bank,client)
		account.deposit(100)
	}

	@Override
	def thenAssert() {
		assert 1 == FenixFramework.getDomainRoot().getBankSet().size()

		def banks = new ArrayList<>(FenixFramework.getDomainRoot().getBankSet())
		def bank = banks.get(0)

		assert BANK_NAME == bank.getName()
		assert BANK_CODE == bank.getCode()
		assert 1 == bank.getClientSet().size()
		assert 1 == bank.getAccountSet().size()
		assert 1 == bank.getOperationSet().size()

		def clients = new ArrayList<>(bank.getClientSet())
		def client = clients.get(0)

		assert CLIENT_NAME == client.getName()

		def accounts = new ArrayList<>(bank.getAccountSet())
		def account = accounts.get(0)

		assert client == account.getClient()
		assert null != account.getIBAN()
		assert 100 == account.getBalance()

		def operations=new ArrayList<>(bank.getOperationSet())
		def operation=operations.get(0)

		assert "DEPOSIT" == operation.getType()
		assert 100 == operation.getValue()
		assert null != operation.getReference()
		assert null != operation.getTime()
	}

	@Override
	def deleteFromDatabase() {
		for (def bank : FenixFramework.getDomainRoot().getBankSet()) {
			bank.delete()
		}
	}
}

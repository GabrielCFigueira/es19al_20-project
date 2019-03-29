package pt.ulisboa.tecnico.softeng.bank.domain 

// JFF: class name different from file name
class OperationRevertMethodSpockTest extends SpockRollbackTestAbstractClass {
	def bank 
	def account 

	@Override
	def populate4Test() {
		bank = new Bank("Money", "BK01") 
		def client = new Client(bank, "António") 
		account = new Account(bank, client) 
	}

	def 'revertDeposit'() {
        when: 'making a deposit and reverting the operation'
            def reference = account.deposit(100).getReference() 
            def operation = bank.getOperation(reference) 
            def newReference = operation.revert() 
        then: 'checking the operation result'
		    0 == account.getBalance()
		    bank.getOperation(newReference)!= null 
		    bank.getOperation(reference)!= null
	}

	def 'revertWithdraw'() {
        when: 'withdrawing a deposit and reverting the operation'
            account.deposit(1000) 
            def reference = account.withdraw(100).getReference() 
            def operation = bank.getOperation(reference) 
            def newReference = operation.revert() 
        then: 'checking the operation result'
            1000 == account.getBalance()
            bank.getOperation(newReference) != null 
            bank.getOperation(reference) != null
	}

}

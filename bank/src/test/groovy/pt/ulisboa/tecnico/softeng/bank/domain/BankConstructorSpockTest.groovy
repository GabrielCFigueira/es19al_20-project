package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class BankConstructorTest extends SpockRollbackTestAbstractClass {
    @Shared String BANK_CODE = "BK01";
	@Shared String BANK_NAME = "Money";

	@Override
	def populate4Test() { }

    def 'success'() {
        when: 'creating a new bank'
        def bank = new Bank(BANK_NAME, BANK_CODE);

        then:'should succeed'
        bank.getName() == BANK_NAME
        bank.getCode() == BANK_CODE
        FenixFramework.getDomainRoot().getBankSet().size() == 1
        bank.getAccountSet().size() == 0
        bank.getClientSet().size() == 0
    }
    def 'notUniqueCode'() {
        new Bank(BANK_NAME, BANK_CODE)
        shouldFail BankException, {
            new Bank(BANK_NAME, BANK_CODE)
        }
        assert FenixFramework.getDomainRoot().getBankSet().size() == 1
    }

    @Unroll('Bank: #bank_name, #bank_code')
    def 'exceptions'() {
        when: 'creating a Bank with invalid arguments'
        new Bank(bank_name, bank_code)

        then:
        thrown(BankException)

        where: 'cases where some arguments are null or invalid'
        bank_name | bank_code
        null      | BANK_CODE
        "    "    | BANK_CODE
        BANK_NAME | null
        BANK_NAME | "    "
        BANK_NAME | "BK0"
        BANK_NAME | "BK011"
    }
}
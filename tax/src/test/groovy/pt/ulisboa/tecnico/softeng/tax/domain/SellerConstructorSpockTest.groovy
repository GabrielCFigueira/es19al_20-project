package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def ADDRESS = 'Somewhere'
	@Shared def NAME = 'José Vendido'
	@Shared def NIF = '123456789'
	IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}

	def 'success'() {
		when:
		def seller = new TaxPayer(irs,NIF,NAME,ADDRESS)

		then:
		seller.getNif() == NIF
		seller.getName() == NAME
		seller.getAddress() == ADDRESS
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}

	def 'unique nif'() {
		given: "a seller"
		def seller=new TaxPayer(this.irs,NIF,NAME,ADDRESS)

		when: "another buyer with the same info"
		new TaxPayer(this.irs,NIF,NAME,ADDRESS)

		then: "an exception is thrown"
		def error = thrown(TaxException)
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}


	@Unroll('testing exceptions: #nif, #name, #address')
	def 'testing exceptions'() {
		when:
		new TaxPayer(this.irs,nif, name, address)

		then:
		thrown(TaxException)

		where:
		nif        | name | address
		null       | NAME | ADDRESS
		''         | NAME | ADDRESS
		'12345678' | NAME | ADDRESS
		NIF        | null | ADDRESS
		NIF        | ''   | ADDRESS
		NIF        | NAME | null
		NIF        | NAME | ''

	}

}

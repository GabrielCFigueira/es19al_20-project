package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def ADDRESS = "Somewhere"
	@Shared def NAME = "José Vendido"
	@Shared def NIF = "123456789"

	def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance();
	}
	
	def 'success'() {
		when:
		def buyer = new Buyer(irs, NIF, NAME, ADDRESS);
		
		then:
		assert NIF == buyer.getNif()
		assert NAME == buyer.getName()
		assert ADDRESS == buyer.getAddress()
		
		assert buyer == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
	}
	
	def 'testing a unique NIF'() {
		given: 'creating a Buyer'
		def seller = new Buyer(irs, NIF, NAME, ADDRESS)
		
		when: 'creating the same Buyer'
		new Buyer(irs, NIF, NAME, ADDRESS)
		
		then: 'throws an exception'
		thrown(TaxException)
		
		and:
		assert seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
	}
	
	@Unroll('Buyer: irs | #bnif | #bname | #baddress')
	def 'exceptions'(){
		when: 'creating a Buyer with invalid arguments'
		new Buyer(irs, bnif, bname, baddress)
		
		then: 'throws an exception'
		thrown(TaxException)
		
		where:
			bnif		| bname		| baddress
			null		| NAME		| ADDRESS
			""			| NAME		| ADDRESS
			"12345678"	| NAME		| ADDRESS
			NIF			| null		| ADDRESS
			NIF			| ""		| ADDRESS
			NIF			| NAME		| null
			NIF			| NAME		| ""
			
	}

}

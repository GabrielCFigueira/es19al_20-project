package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerConstructorSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def ADDRESS = "Somewhere"
	@Shared def NAME = "José Vendido"
	@Shared def NIF = "123456789"

	def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}
	
	def 'success'() {
		when:
		def seller = new Seller(irs, NIF, NAME, ADDRESS);
		
		then:
		assert NIF == seller.getNif()
		assert NAME == seller.getName()
		assert ADDRESS == seller.getAddress()

		assert seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
	}
	
	def 'testing a unique NIF'() {
		given: 'creating a Seller'
		def seller = new Seller(irs, NIF, NAME, ADDRESS)

		when: 'creating the same Seller'
		new Seller(irs, NIF, NAME, ADDRESS)
		
		then: 'throws an exception'
		thrown(TaxException)
		
		and:
		assert seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF)
	}
	
	@Unroll('Seller: irs | #snif | #sname | #saddress')
	def 'exceptions'(){
		when: 'creating a Seller with invalid arguments'
		new Seller(irs, snif, sname, saddress)
		
		then: 'throws an exception'
		thrown(TaxException)
		
		where:
			snif		| sname		| saddress
			null		| NAME		| ADDRESS
			""			| NAME		| ADDRESS
			NIF			| null		| ADDRESS
			NIF			| ""		| ADDRESS
			NIF			| NAME		| null
			NIF			| NAME		| ""
	}

}

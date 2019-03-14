package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll
import spock.lang.Shared
import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer

class IRSGetTaxPayerByNIFSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def SELLER_NIF = "123456789"
	@Shared def BUYER_NIF = "987654321"

	def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
		new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
	}
	
	@Unroll('IRSGetTaxPayerByNIF:#NIF')
	def 'success'(){
		given:'given a Tax Payer By NIF '
			def taxPayer = irs.getTaxPayerByNIF(NIF)
		expect:'expect a Tax Payer assert'	
			null!= taxPayer
			NIF == taxPayer.getNif()
		where:
			NIF 		| _
			SELLER_NIF	| _
			BUYER_NIF	| _			
	}
	
	@Unroll('IRSGetTaxPayerByNIF:#NIF')
	def 'nullTaxPayer'(){
		given:'given a Tax Payer by NIF'
			def taxPayer = irs.getTaxPayerByNIF(NIF)
		expect:'expect a null'
			null == taxPayer
		where:
			NIF			| _
			null		| _
			""			| _
			"122456789"	| _
					
	}

}

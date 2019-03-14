package pt.ulisboa.tecnico.softeng.tax.services.local

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll
import pt.ulisboa.tecnico.softeng.tax.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface


class IRSCancelInvoiceMethodSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = "123456789"
	def BUYER_NIF = "987654321"
	def FOOD = "FOOD"
	def  VALUE = 16
	def  date = new LocalDate(2018, 02, 13)

	def irs
	def reference
	def invoice

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
		def seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		def buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
		def itemType = new ItemType(irs, FOOD, VALUE)
		invoice = new Invoice(30.0, date, itemType, seller, buyer)
		reference = invoice.getReference()
	}

	def 'success'() {
		when:'when cancelling the Invoice'
			TaxInterface.cancelInvoice(reference)
		then:'should succeed'
			true == invoice.isCancelled()
	}
	
	@Unroll('IRSCancelInvoice:#arg')
	def 'exceptions'(){
		when:'when invoice is cancelled'
			TaxInterface.cancelInvoice(arg)
		then:'TaxException is thrown'
			thrown(TaxException)
		where:
			arg			| _
			null		| _
			"   "		| _
			"XXXXXXXX"	| _
			
	}

}

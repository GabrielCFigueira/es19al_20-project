package pt.ulisboa.tecnico.softeng.tax.domain



import org.joda.time.LocalDate


import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller

public class TaxPayerGetTaxesPerYearMethodsSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = "123456788"
	def BUYER_NIF = "987654311"
	def FOOD = "FOOD"
	def TAX = 10
	def date = new LocalDate(2018, 02, 13)

	def seller
	def buyer
	def itemType

	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance()
		seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
		itemType = new ItemType(irs, FOOD, TAX)
	}

	
	def 'success'() {		
		when:'when Invoices are created and seller and buyer getters are called'
			new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer)
			new Invoice(100, date, itemType, seller, buyer)
			new Invoice(100, date, itemType, seller, buyer)
			new Invoice(50, date, itemType, seller, buyer)
			def toPay = seller.getToPayPerYear()
			def  taxReturn = buyer.getTaxReturnPerYear()
		then:'assert values'
			2 == toPay.keySet().size()
			10.0d == toPay.get(2017)
			25.0d == toPay.get(2018)
			2 == taxReturn.keySet().size()
			0.5d == taxReturn.get(2017)
			1.25d == taxReturn.get(2018)
		}
		
	def 'successEmpty'() {
		when:'getters are called'
			def toPay = seller.getToPayPerYear()
			def taxReturn = buyer.getTaxReturnPerYear()
		then:'assert 0'
			0 == toPay.keySet().size()
			0 == taxReturn.keySet().size()
	}


}

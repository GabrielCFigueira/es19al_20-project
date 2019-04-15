package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

class TaxPayerGetTaxesPerYearMethodSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = '123456788'
	def BUYER_NIF = '987654311'
	def FOOD = 'FOOD'
	def TAX = 10
	def date = new LocalDate(2018, 02, 13)
	def seller
	def buyer
	def itemType

	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance()

		seller = new Seller(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
		buyer = new Buyer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
		itemType = new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
		given:
		new Invoice(100000, new LocalDate(2017, 12, 12), itemType, seller, buyer)
		new Invoice(100000, date, itemType, seller, buyer)
		new Invoice(100000, date, itemType, seller, buyer)
		new Invoice(50000, date, itemType, seller, buyer)

		when:
		def toPay = seller.getToPayPerYear()

		then:
		toPay.keySet().size() == 2
		10000 == toPay.get(2017)
		25000 == toPay.get(2018)
		Map<Integer, Double> taxReturn=buyer.getTaxReturnPerYear()

		taxReturn.keySet().size() == 2
		500 == taxReturn.get(2017)
		1250 == taxReturn.get(2018)
	}

	def 'success empty'() {
		when:
		def toPay=seller.getToPayPerYear()

		then:
		toPay.keySet().size() == 0
		Map<Integer, Long> taxReturn=buyer.getTaxReturnPerYear()
		taxReturn.keySet().size() == 0
	}

}

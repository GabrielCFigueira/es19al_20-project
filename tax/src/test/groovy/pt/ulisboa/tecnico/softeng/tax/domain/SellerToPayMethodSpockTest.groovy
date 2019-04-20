package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerToPayMethodSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = '123456789'
	def BUYER_NIF = '987654321'
	def FOOD = 'FOOD'
	def TAX = 10
	def date = new LocalDate(2018, 02, 13)
	def seller
	def buyer
	def itemType

	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance()

		seller = new TaxPayer(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
		buyer = new TaxPayer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
		itemType = new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
		given:
		new Invoice(100000, date, itemType, seller, buyer)
		new Invoice(100000, date, itemType, seller, buyer)
		new Invoice(50000, date, itemType, seller, buyer)

		when:
		def value = seller.toPay(year)

		then:
		toPay  ==  value

		where:
		year | toPay
		2018 | 25000
		2015 | 0

	}

	def 'no invoices'() {
		expect:
		def value  =  seller.toPay(2018)
		0  ==  value
	}

	def 'before 1970'() {
		when:
		new Invoice(100000, new LocalDate(1969, 02, 13), itemType, seller, buyer)
		new Invoice(50000, new LocalDate(1969, 02, 13), itemType, seller, buyer)

		seller.toPay(1969)

		then:
		thrown(TaxException)
	}

	def 'equal 1970'() {
		when:
		new Invoice(100000, new LocalDate(1970, 02, 13), itemType, seller, buyer)
		new Invoice(50000, new LocalDate(1970, 02, 13), itemType, seller, buyer)

		def value = seller.toPay(1970)

		then:
		15000  ==  value
	}

	def 'ignore cancelled'() {
		when:
		new Invoice(100000, date, itemType, seller, buyer)
		def invoice = new Invoice(100, date, itemType, seller, buyer)

		new Invoice(50000, date, itemType, seller, buyer)
		invoice.cancel()

		def value = seller.toPay(2018)

		then:
		15000  ==  value
	}

}

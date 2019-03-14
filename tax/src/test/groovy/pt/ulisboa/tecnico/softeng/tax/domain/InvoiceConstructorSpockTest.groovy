package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll
import spock.lang.Shared


import org.joda.time.LocalDate


import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = "123456789"
	def BUYER_NIF = "987654321"
	def FOOD = "FOOD"
	def TAX = 23
	@Shared def VALUE = 16
	@Shared def date = new LocalDate(2018, 02, 13)

	@Shared def seller
	@Shared def buyer
	@Shared def itemType

	@Override
	def populate4Test() {
		IRS irs = IRS.getIRSInstance()
		seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
		itemType = new ItemType(irs, FOOD, TAX)
	}


	def 'success'() {
		when: 'creating a new invoice'
		Invoice invoice = new Invoice(VALUE, date, itemType, seller, buyer)

		then: 'should succeed'
		null != invoice.getReference()
		VALUE == invoice.getValue()
		date == invoice.getDate()
		itemType == invoice.getItemType()
		seller == invoice.getSeller()
		buyer == invoice.getBuyer()
		VALUE * TAX / 100.0 == invoice.getIva()
		false == invoice.isCancelled()
		seller.getInvoiceByReference(invoice.getReference()) == invoice
		buyer.getInvoiceByReference(invoice.getReference()) == invoice

	}

	@Unroll('Invoice: #_value, #_date, #_itemType, #_seller, #_buyer')
	def 'exceptions'() {
		when: 'creating an invoice with wrong parameters'
		new Invoice(_value, _date, _itemType, _seller, _buyer)

		then: 'throws an exception'
		thrown(TaxException)

		where:
		_value | _date                       | _itemType  | _seller  | _buyer
		VALUE  | date                        | itemType   | null     | buyer
		VALUE  | date                        | itemType   | seller   | null
		VALUE  | date                        | null       | seller   | buyer
		0      | date                        | itemType   | seller   | buyer
		-23.6f | date                        | itemType   | seller   | buyer
		VALUE  | null                        | itemType   | seller   | buyer
		VALUE  | new LocalDate(1969, 12, 31) | itemType   | seller   | buyer
	}

	def equal1970() {
		new Invoice(VALUE, new LocalDate(1970, 01, 01), itemType, seller, buyer)
	}
}

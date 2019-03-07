package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerToReturnSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def SELLER_NIF = "123456789" 
	@Shared def BUYER_NIF = "987654321" 
	@Shared def FOOD = "FOOD" 
	@Shared def TAX = 10 
	def date = LocalDate.parse("2018-02-13") 

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

	@Unroll('Invoices: #value1, #value2, #value3, #year, #taxValue')
	def 'invoices'() {
        when: 'creating invoices'
            new Invoice(value1, date, itemType, seller, buyer) 
            new Invoice(value2, date, itemType, seller, buyer) 
            new Invoice(value3, date, itemType, seller, buyer) 
            def value = buyer.taxReturn(year) 
        then: 'checking value of tax' 
		    value == taxValue 
		where:
			value1 | value2 | value3 | year | taxValue
			   100 |  100   |   50   | 2018 |  1.25
			   100 |  100   |   50   | 2017 |  0.0
	}

	def 'noInvoices'() {
		when: 'finding default value of tax'
			def value = buyer.taxReturn(2018) 
		then: 'checking default value of tax'
			0.0 == value 
	}

	def 'before1970'() {
		when: 'creating invoice and checking value of tax'
			new Invoice(100,LocalDate.parse("1969-02-13"), itemType, seller, buyer)
			0.0 == buyer.taxReturn(1969)
		then: 'throws exception'
			thrown(TaxException)
	}

	def 'equal1970'() {
		when: 'creating invoice'
			new Invoice(100,LocalDate.parse("1970-02-13"), itemType, seller, buyer) 
			def value = buyer.taxReturn(1970) 
		then: 'checking value of tax'
			0.5 == value
	}

	def 'ignoreCancelled'() {
		when: 'creating multiple invoices and cancelling one'
			new Invoice(100, date, itemType, seller, buyer) 
			def invoice = new Invoice(100, date, itemType, seller, buyer) 
			new Invoice(50, date, itemType, seller, buyer) 
			invoice.cancel() 
			def value = buyer.taxReturn(2018) 
		then: 'checking value of tax'
			0.75 == value 
	}
}

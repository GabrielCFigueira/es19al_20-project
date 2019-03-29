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

// JFF: public modifier unnecessary
public class SellerToPaySpockTest extends SpockRollbackTestAbstractClass {
	def SELLER_NIF = "123456789" 
	def BUYER_NIF = "987654321" 
	def FOOD = "FOOD" 
	def TAX = 10 
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

	@Unroll('#Value,#year')
	def 'success'() {
		when: 'creating multiple invoices'
			new Invoice(100, date, itemType, seller, buyer) 
            new Invoice(100, date, itemType, seller, buyer) 
            new Invoice(50, date, itemType, seller, buyer)
		then: 'checking values'
			value == seller.toPay(year)
		where:
			value  | year	
			 25.0  | 2018
			 0.0   | 2015
	}

	def 'noInvoices'() {
        when: 'using default value toPay'
		    def value =  seller.toPay(2018) 
        then: 'checking value toPay'
		    0.0 == value 
	}

	def 'before1970'() {
        when: 'creating invoices before 1970'
            new Invoice(100, new LocalDate(1969, 02, 13),  itemType,  seller,  buyer) 
            new Invoice(50, new LocalDate(1969, 02, 13),  itemType,  seller,  buyer) 
            def value =  seller.toPay(1969) 
            0.0 == value
        then: 'throwing exception'
            thrown(TaxException)
		   
	}

	def 'equal1970'() {
        when: 'creating invoices in 1970'
            new Invoice(100, new LocalDate(1970, 02, 13),  itemType,  seller,  buyer) 
            new Invoice(50, new LocalDate(1970, 02, 13),  itemType,  seller,  buyer) 
            def value =  seller.toPay(1970) 
        then: "checking value toPay"
		    15.0 == value
	}

	def 'ignoreCancelled'() {
        when: 'creating multiple invoices and cancelling one'
		    new Invoice(100,  date,  itemType,  seller,  buyer) 
            def invoice = new Invoice(100,  date,  itemType,  seller,  buyer) 
            new Invoice(50,  date,  itemType,  seller,  buyer) 
            invoice.cancel() 
		    def value =  seller.toPay(2018) 
        then: 'checking value toPay'
		    15.0 == value
	}
}

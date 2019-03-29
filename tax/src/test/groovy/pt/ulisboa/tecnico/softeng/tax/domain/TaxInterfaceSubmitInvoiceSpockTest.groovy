package pt.ulisboa.tecnico.softeng.tax.domain


import org.joda.time.DateTime
import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData

class TaxInterfaceSubmitInvoiceSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def REFERENCE = "123456789"
	@Shared def SELLER_NIF = "123456789"
	@Shared def BUYER_NIF = "987654321"
	@Shared def FOOD = "FOOD"
	@Shared def VALUE = 160
	@Shared def TAX = 16
	@Shared def date = new LocalDate(2018, 02, 13)
	@Shared def time = new DateTime(2018, 02, 13, 10, 10)

	def irs


	def populate4Test() {
		irs = IRS.getIRSInstance()
		new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
		new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
        when: 'creating and submitting an invoice'
		    	def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date,
					time);
		    	def invoiceReference = TaxInterface.submitInvoice(invoiceData)

		    	def invoice = irs.getTaxPayerByNIF(SELLER_NIF).getInvoiceByReference(invoiceReference)

        then:	'checking the parameters'
		    	invoiceReference == invoice.getReference()
		    	SELLER_NIF == invoice.getSeller().getNif()
		    	BUYER_NIF == invoice.getBuyer().getNif()
		    	FOOD == invoice.getItemType().getName()
		    	VALUE == invoice.getValue()
		    	date == invoice.getDate()
	}

	def 'submitTwice'() {
        when:	'creating and submitting two references with the same invoiceData'
		    	def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date,
					time)
		   		def invoiceReference = TaxInterface.submitInvoice(invoiceData)

		    	def secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

        then:	'should succeed'
		    	invoiceReference == secondInvoiceReference
	}

	@Unroll('Invoice: #reference, #seller_nif, #buyer_nif, #food, #value ,#date ,#time')
	def 'exceptions'() {
		when:	'submitting multiple invoices with invalid data'
		    def invoiceData = new RestInvoiceData(reference, seller_nif, buyer_nif, food, value, _date, _time)
            TaxInterface.submitInvoice(invoiceData)
		then:	'throws an exception'
			thrown(TaxException)
		where:
			reference | seller_nif | buyer_nif | food | value  | _date | _time
			REFERENCE | null       | BUYER_NIF | FOOD | VALUE  | date  | time
			REFERENCE | ""         | BUYER_NIF | FOOD | VALUE  | date  | time
			REFERENCE | SELLER_NIF | null      | FOOD | VALUE  | date  | time
			REFERENCE | SELLER_NIF | ""        | FOOD | VALUE  | date  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | null | VALUE  | date  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | ""   | VALUE  | date  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | 0.0d   | date  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | -23.7d | date  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | null  | time
			REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | date  | null
			REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1969, 12, 31) | new DateTime(1969, 12, 31, 10, 10)
			null      | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1970, 01, 01) | new DateTime(1970, 01, 01, 10, 10)


	}

	// JFF: what is this testing?
	def 'equals to 1970'() {
        expect: 'that the date is equal to 1970'
		    def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE,
			new LocalDate(1970, 01, 01), new DateTime(1970, 01, 01, 10, 10))
		    TaxInterface.submitInvoice(invoiceData)
	}

}

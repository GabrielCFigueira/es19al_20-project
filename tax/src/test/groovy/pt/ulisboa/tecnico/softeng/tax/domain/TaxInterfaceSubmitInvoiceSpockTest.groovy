package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.DateTime
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData
import spock.lang.Shared
import spock.lang.Unroll

class TaxInterfaceSubmitInvoiceSpockTest extends SpockRollbackTestAbstractClass {
	private static final String REFERENCE = '123456789'
	private static final String SELLER_NIF = '123456789'
	private static final String BUYER_NIF = '987654321'
	private static final String FOOD = 'FOOD'
	private static final double VALUE = 160
	private static final int TAX = 16
	@Shared private final LocalDate date = new LocalDate(2018, 02, 13)
	@Shared private final DateTime time = new DateTime(2018, 02, 13, 10, 10)
	@Shared private IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()

		new Seller(irs, SELLER_NIF, 'José Vendido', 'Somewhere')

		new Buyer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')

		new ItemType(irs, FOOD, TAX)
	}

	def 'success'() {
		given:
		RestInvoiceData invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)
		String invoiceReference = TaxInterface.submitInvoice(invoiceData)

		when:
		Invoice invoice = irs.getTaxPayerByNIF(SELLER_NIF).getInvoiceByReference(invoiceReference)

		then:
		invoice.getReference() == invoiceReference
		invoice.getSeller().getNif()  ==  SELLER_NIF
		invoice.getBuyer().getNif()  ==  BUYER_NIF
		invoice.getItemType().getName()  ==  FOOD
		160.0  == invoice.getValue()
		invoice.getDate() == date
	}

	def 'submit twice'() {
		given:
		RestInvoiceData invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)

		when:
		String invoiceReference = TaxInterface.submitInvoice(invoiceData)
		String secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

		then:
		secondInvoiceReference  ==  invoiceReference
	}

	def 'equal 1970'() {
		given:
		RestInvoiceData invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, new LocalDate(1970, 01, 01), new DateTime(1970, 01, 01, 10, 10))

		expect:
		TaxInterface.submitInvoice(invoiceData)

	}

	@Unroll('#reference,  #sel,  #buy,  #food,  #value,  #dt,  #tm')
	def 'exceptions'() {
		given:
		RestInvoiceData invoiceData = new RestInvoiceData(reference,  sel,  buy,  food,  value,  dt,  tm)

		when:
		TaxInterface.submitInvoice(invoiceData)

		then:
		thrown(TaxException)

		where:
		reference | sel        | buy       | food | value  | dt | tm
		REFERENCE | null       | BUYER_NIF | FOOD | VALUE  | date | time
		REFERENCE | ''         | BUYER_NIF | FOOD | VALUE  | date | time
		REFERENCE | SELLER_NIF | null      | FOOD | VALUE  | date | time
		REFERENCE | SELLER_NIF | ''        | FOOD | VALUE  | date | time
		REFERENCE | SELLER_NIF | BUYER_NIF | null | VALUE  | date | time
		REFERENCE | SELLER_NIF | BUYER_NIF | ''   | VALUE  | date | time
		REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | 0.0d   | date | time
		REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | -23.7d | date | time
		REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | date | null
		REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1969, 12, 31) | new DateTime(1969, 12, 31, 10, 10)
	}

	def 'null ref'() {
		when:
		new RestInvoiceData(null,  SELLER_NIF, BUYER_NIF,
				FOOD, VALUE, new LocalDate(1970, 01, 01),
				new DateTime(1970, 01, 01, 10, 10))

		then:
		thrown(TaxException)
	}
}
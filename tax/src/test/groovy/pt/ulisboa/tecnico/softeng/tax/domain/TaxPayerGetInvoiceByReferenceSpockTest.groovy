package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll
import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;


class TaxPayerGetInvoiceByReferenceSpockTest extends SpockRollbackTestAbstractClass {

    def private static final SELLER_NIF = "123456789"
	def private static final BUYER_NIF = "987654321"
	def private static final FOOD = "FOOD"
	def private static final VALUE = 16
	def private static final TAX = 23
	def private final date = new LocalDate(2018, 02, 13)

	def private seller
	def private buyer
	def private itemType
	def private invoice


	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance();
		this.seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
		this.buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
		this.itemType = new ItemType(irs, FOOD, TAX)
		this.invoice = new Invoice(VALUE, this.date, this.itemType, this.seller, this.buyer)
	}

    def success() {
        expect:
        this.invoice == this.seller.getInvoiceByReference(this.invoice.getReference())
    }

    def nullReference() {
        when:
        this.seller.getInvoiceByReference(null)

        then:
        thrown(TaxException)
    }

    def emptyReference() {
        when:
        this.seller.getInvoiceByReference("")

        then:
        thrown(TaxException)
    }

    def 'reference does not exist'() {
        expect:
        null == this.seller.getInvoiceByReference(BUYER_NIF)
    }
}

package pt.ulisboa.tecnico.softeng.tax.domain;

import java.util.Map;

import org.joda.time.LocalDate;


import pt.ulisboa.tecnico.softeng.tax.domain.Buyer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType;
import pt.ulisboa.tecnico.softeng.tax.domain.Seller;

public class TaxPayerGetTaxesPerYearMethodsSpockTest extends SpockRollbackTestAbstractClass {
	def private static final SELLER_NIF = "123456788";
	def private static final BUYER_NIF = "987654311";
	def private static final FOOD = "FOOD";
	def private static final TAX = 10;
	def private final date = new LocalDate(2018, 02, 13);

	def private seller;
	def private buyer;
	def private itemType;

	@Override
	def populate4Test() {
		def irs = IRS.getIRSInstance();
		seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere");
		buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
		itemType = new ItemType(irs, FOOD, TAX);
	}

	
	def 'success'() {
		when:
		new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer);
		new Invoice(100, date, itemType, seller, buyer);
		new Invoice(100, date, itemType, seller, buyer);
		new Invoice(50, date, itemType, seller, buyer);
	
		def Map<Integer, Double> toPay = seller.getToPayPerYear();
		def Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear();
		then:
		2 == toPay.keySet().size();
		10.0d == toPay.get(2017);
		25.0d == toPay.get(2018);
		2 == taxReturn.keySet().size();
		0.5d == taxReturn.get(2017);
		1.25d == taxReturn.get(2018);
		}
		
		def 'successEmpty'() {
			when:
			def Map<Integer, Double> toPay = seller.getToPayPerYear();
			def Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear();
			then:
			0 == toPay.keySet().size();
	

			
			0 == taxReturn.keySet().size();
		}

	/*def 'success'() {
		new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer);
		new Invoice(100, date, itemType, seller, buyer);
		new Invoice(100, date, itemType, seller, buyer);
		new Invoice(50, date, itemType, seller, buyer);

		def Map<Integer, Double> toPay = seller.getToPayPerYear();
		
		2 == toPay.keySet().size();
		10.0d == toPay.get(2017);
		25.0d == toPay.get(2018);

		def Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear();
		
		2 == taxReturn.keySet().size();
		0.5d == taxReturn.get(2017);
		1.25d == taxReturn.get(2018);
	}

	def 'successEmpty'() {
		def Map<Integer, Double> toPay = seller.getToPayPerYear();
		
		0 == toPay.keySet().size();

		def Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear();
		
		0 == taxReturn.keySet().size();
	}*/

}

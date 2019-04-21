package pt.ulisboa.tecnico.softeng.tax.domain;

import java.util.Map;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class TaxPayer extends TaxPayer_Base {
	private final static int PERCENTAGE = 5;
	protected TaxPayer() {
		super();
	}

	public TaxPayer(IRS irs, String NIF, String name, String address) {
		checkArguments(irs, NIF, name, address);

		setNif(NIF);
		setName(name);
		setAddress(address);

		irs.addTaxPayer(this);
	}

	public void delete() {
		setIrs(null);
		for (Invoice invoice : getInvoiceSellerSet()) {
			invoice.delete();
		}

		for (Invoice invoice : getInvoiceBuyerSet()) {
			invoice.delete();
		}

		deleteDomainObject();
	}

	public long toPay(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		long result = 0;
		for (Invoice invoice : getInvoiceSellerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva();
			}
		}
		return result;
	}

	public long taxReturn(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

	  long result = 0;
	  long individualInvoice = 0;
		for (Invoice invoice : getInvoiceBuyerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				individualInvoice = invoice.getIva() * PERCENTAGE;
				if (individualInvoice % 100 >= 50)
					individualInvoice /= 100 + 1;
				else
					individualInvoice /= 100;
				result = result + individualInvoice;
			}
		}
		return result;
	}

	protected void checkArguments(IRS irs, String NIF, String name, String address) {
		if (NIF == null || NIF.length() != 9) {
			throw new TaxException();
		}

		if (name == null || name.length() == 0) {
			throw new TaxException();
		}

		if (address == null || address.length() == 0) {
			throw new TaxException();
		}

		if (irs.getTaxPayerByNIF(NIF) != null) {
			throw new TaxException();
		}

	}

	public Invoice getInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getInvoiceSellerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}

		for (Invoice invoice : getInvoiceBuyerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}

		return null;
	}

	public Map<Integer, Long> getToPayPerYear() {
		return getInvoiceSellerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> toPay(y)));
	}

	public Map<Integer, Long> getTaxReturnPerYear() {
		return getInvoiceBuyerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> taxReturn(y)));
	}

}

package pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class RestInvoiceData {
	private String reference;
	private String sellerNif;
	private String buyerNif;
	private String itemType;
	private Double value;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;
	private Double iva;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private DateTime time;

	public RestInvoiceData() {
	}

	public RestInvoiceData(String reference, String sellerNif, String buyerNif, String itemType, Double value,
			LocalDate date, DateTime time) {
		if (reference == null) {
			throw new TaxException();
		}
		this.reference = reference;
		this.sellerNif = sellerNif;
		this.buyerNif = buyerNif;
		this.itemType = itemType;
		this.value = value;
		this.date = date;
		this.time = time;
	}

	public RestInvoiceData(Invoice invoice) {
		this.reference = invoice.getReference();
		this.sellerNif = invoice.getSeller().getNif();
		this.buyerNif = invoice.getBuyer().getNif();
		this.itemType = invoice.getItemType().getName();
		this.value = (double) invoice.getValue() / 1000;
		this.date = invoice.getDate();
		this.iva = (double) invoice.getIva() / 1000;
		this.time = invoice.getTime();
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getSellerNif() {
		return this.sellerNif;
	}

	public void setSellerNif(String sellerNif) {
		this.sellerNif = sellerNif;
	}

	public String getBuyerNif() {
		return this.buyerNif;
	}

	public void setBuyerNif(String buyerNif) {
		this.buyerNif = buyerNif;
	}

	public String getItemType() {
		return this.itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Double getIva() {
		return this.iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public DateTime getTime() {
		return this.time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

}

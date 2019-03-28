package pt.ulisboa.tecnico.softeng.hotel.domain;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException;

public class Processor extends Processor_Base {
	private static Logger logger = LoggerFactory.getLogger(Processor.class);

	private static final String TRANSACTION_SOURCE = "HOTEL";
	private BankInterface bankInterface;
	private TaxInterface taxInterface;

	/*Setters*/
	public void setBankInterface(BankInterface bankInterface){
		this.bankInterface = bankInterface;
	}

	public void setTaxInterface(TaxInterface taxInterface){
		this.taxInterface = taxInterface;
	}

	public void delete() {
		setHotel(null);

		for (Booking booking : getBookingSet()) {
			booking.delete();
		}

		deleteDomainObject();
	}

	public Processor() {
		taxInterface = new TaxInterface();
		bankInterface = new BankInterface();
	}

	public void submitBooking(Booking booking) {
		addBooking(booking);
		processInvoices();
	}

	private void processInvoices() {
		Set<Booking> failedToProcess = new HashSet<>();
		for (Booking booking : getBookingSet()) {
			if (!booking.isCancelled()) {
				if (booking.getPaymentReference() == null) {
					try {
						booking.setPaymentReference(
								this.bankInterface.processPayment(new RestBankOperationData(booking.getBuyerIban(),
										booking.getPrice(), TRANSACTION_SOURCE, booking.getReference())));
					} catch (BankException | RemoteAccessException ex) {
						failedToProcess.add(booking);
						continue;
					}
				}
				RestInvoiceData invoiceData = new RestInvoiceData(booking.getProviderNif(), booking.getBuyerNif(),
						Booking.getType(), booking.getPrice(), booking.getArrival(), booking.getTime());
				try {
					booking.setInvoiceReference(this.taxInterface.submitInvoice(invoiceData));
				} catch (TaxException | RemoteAccessException ex) {
					failedToProcess.add(booking);
				}
			} else {
				try {
					if (booking.getCancelledPaymentReference() == null) {
						booking.setCancelledPaymentReference(
								this.bankInterface.cancelPayment(booking.getPaymentReference()));
					}
					if (!booking.getCancelledInvoice()) {
						this.taxInterface.cancelInvoice(booking.getInvoiceReference());
						booking.setCancelledInvoice(true);
					}
				} catch (BankException | TaxException | RemoteAccessException ex) {
					failedToProcess.add(booking);
				}

			}
		}

		for (Booking booking : getBookingSet()) {
			removeBooking(booking);
		}

		for (Booking booking : failedToProcess) {
			addBooking(booking);
		}
	}

}
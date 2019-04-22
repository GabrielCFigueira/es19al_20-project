package pt.ulisboa.tecnico.softeng.car.domain;

import java.util.HashSet;
import java.util.Set;

import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestBankOperationData;
import pt.ulisboa.tecnico.softeng.car.services.remote.dataobjects.RestInvoiceData;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.BankException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.car.services.remote.exceptions.TaxException;

public class Processor extends Processor_Base {
	private static final String TRANSACTION_SOURCE = "CAR";

	private BankInterface bankInterface;
	private TaxInterface taxInterface;

	public Processor(BankInterface bankInterface, TaxInterface taxInterface) {
		this.bankInterface = bankInterface;
		this.taxInterface = taxInterface;
	}

	private BankInterface getBankInterface() {
		if (this.bankInterface == null)
			this.bankInterface = new BankInterface();
		return this.bankInterface;
	}

	private TaxInterface getTaxInterface() {
		if (this.taxInterface == null)
			this.taxInterface = new TaxInterface();
		return this.taxInterface;
	}

	public void delete() {
		setRentACar(null);

		for (Renting renting : getRentingSet()) {
			renting.delete();
		}

		deleteDomainObject();
	}

	public void submitRenting(Renting renting) {
		addRenting(renting);
		processInvoices();
	}

	private void processInvoices() {
		Set<Renting> failedToProcess = new HashSet<>();
		for (Renting renting : getRentingSet()) {
			if (!renting.isCancelled()) {
				if (renting.getPaymentReference() == null) {
					try {
						renting.setPaymentReference(
								getBankInterface().processPayment(new RestBankOperationData(renting.getClientIban(), getRentACar().getIban(),
										(double) renting.getPrice() / 1000, TRANSACTION_SOURCE, renting.getReference())));
					} catch (BankException | RemoteAccessException ex) {
						failedToProcess.add(renting);
						continue;
					}
				}

				RestInvoiceData invoiceData = new RestInvoiceData(renting.getVehicle().getRentACar().getNif(),
						renting.getClientNif(), renting.getType(), (double) renting.getPrice() / 1000, renting.getBegin(),
						renting.getTime());
				try {
					renting.setInvoiceReference(getTaxInterface().submitInvoice(invoiceData));
				} catch (TaxException | RemoteAccessException ex) {
					failedToProcess.add(renting);
				}
			} else {
				try {
					if (renting.getCancelledPaymentReference() == null) {
						renting.setCancelledPaymentReference(
								getBankInterface().cancelPayment(renting.getPaymentReference()));
					}
					getTaxInterface().cancelInvoice(renting.getInvoiceReference());
					renting.setCancelledInvoice(true);
				} catch (BankException | TaxException | RemoteAccessException ex) {
					failedToProcess.add(renting);
				}

			}
		}

		for (Renting renting : getRentingSet()) {
			removeRenting(renting);
		}

		for (Renting renting : failedToProcess) {
			addRenting(renting);
		}
	}

}

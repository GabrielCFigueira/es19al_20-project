package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException;

public class UndoState extends UndoState_Base {

	@Override
	public State getValue() {
		return State.UNDO;
	}

	@Override
	public void process() {
		if (getAdventure().shouldCancelPayment()) {
			try {
				getAdventure()
						.setPaymentCancellation(getAdventure().getBroker().getBankInterface().cancelPayment(getAdventure().getPaymentConfirmation()));
			} catch (BankException | RemoteAccessException ex) {
				// does not change state
			}
		}

		if (getAdventure().shouldCancelActivity()) {
			try {
				getAdventure().setActivityCancellation(
						getAdventure().getBroker().getActivityInterface().cancelReservation(getAdventure().getActivityConfirmation()));
			} catch (ActivityException | RemoteAccessException ex) {
				// does not change state
			}
		}

		if (getAdventure().shouldCancelRoom()) {
			try {
				getAdventure().setRoomCancellation(getAdventure().getBroker().getHotelInterface().cancelBooking(getAdventure().getRoomConfirmation()));
			} catch (HotelException | RemoteAccessException ex) {
				// does not change state
			}
		}

		if (getAdventure().shouldCancelVehicleRenting()) {
			try {
				getAdventure()
						.setRentingCancellation(getAdventure().getBroker().getCarInterface().cancelRenting(getAdventure().getRentingConfirmation()));
			} catch (CarException | RemoteAccessException ex) {
				// does not change state
			}
		}

		if (getAdventure().shouldCancelInvoice()) {
			try {
				getAdventure().getBroker().getTaxInterface().cancelInvoice(getAdventure().getInvoiceReference());
				getAdventure().setInvoiceCancelled(true);
			} catch (TaxException | RemoteAccessException ex) {
				// does not change state
			}
		}

		if (getAdventure().roomIsCancelled() && getAdventure().activityIsCancelled()
				&& getAdventure().paymentIsCancelled() && getAdventure().rentingIsCancelled()
				&& getAdventure().invoiceIsCancelled()) {
			getAdventure().setState(State.CANCELLED);
		}
	}

}

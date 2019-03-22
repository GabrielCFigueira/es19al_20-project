package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;

public class Adventure extends Adventure_Base {
	private static Logger logger = LoggerFactory.getLogger(Adventure.class);
	private BankInterface bankInterface;
	private CarInterface carInterface;
	private HotelInterface hotelInterface;
	private ActivityInterface activityInterface;

	public enum State {
		PROCESS_PAYMENT, RESERVE_ACTIVITY, BOOK_ROOM, RENT_VEHICLE, UNDO, CONFIRMED, CANCELLED, TAX_PAYMENT
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin) {
		this(broker, begin, end, client, margin, false);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, ActivityInterface activityInterface) {
		this(broker, begin, end, client, margin, false);
		setActivityInterface(activityInterface);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, HotelInterface hotelInterface ) {
		this(broker, begin, end, client, margin, false);
		setHotelInterface(hotelInterface);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, CarInterface carInterface) {
		this(broker, begin, end, client, margin, false);
		setCarInterface(carInterface);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, BankInterface bankInterface){
		this(broker, begin, end, client, margin, false);
		setBankInterface(bankInterface);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, boolean rentVehicle) {
		checkArguments(broker, begin, end, client, margin);
		
		setID(broker.getCode() + Integer.toString(broker.getCounter()));

		setBegin(begin);
		setEnd(end);
		setMargin(margin);
		setRentVehicle(rentVehicle);
		setClient(client);

		broker.addAdventure(this);
		setBroker(broker);

		setCurrentAmount(0.0);
		setTime(DateTime.now());

		setState(State.RESERVE_ACTIVITY);

		setActivityInterface(new ActivityInterface());
		setHotelInterface(new HotelInterface());
		setCarInterface(new CarInterface());
		setBankInterface(new BankInterface());
	}
	
	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, boolean rentVehicle, ActivityInterface activityInterface) {
		this(broker, begin, end, client, margin, rentVehicle);
		setActivityInterface(activityInterface);
	}

	public Adventure(Broker broker, LocalDate begin, LocalDate end, Client client, double margin, boolean rentVehicle, HotelInterface hotelInterface) {
		this(broker,begin,end,client,margin, rentVehicle);
		setHotelInterface(hotelInterface);
	}

	public void delete() {
		setBroker(null);
		setClient(null);

		getState().delete();

		deleteDomainObject();
	}

	private void checkArguments(Broker broker, LocalDate begin, LocalDate end, Client client, double margin) {
		if (client == null || broker == null || begin == null || end == null) {
			throw new BrokerException();
		}

		if (end.isBefore(begin)) {
			throw new BrokerException();
		}

		if (client.getAge() < 18 || client.getAge() > 100) {
			throw new BrokerException();
		}

		if (margin <= 0 || margin > 1) {
			throw new BrokerException();
		}
	}

	public int getAge() {
		return getClient().getAge();
	}

	public String getIban() {
		return getClient().getIban();
	}

	public void incAmountToPay(double toPay) {
		setCurrentAmount(getCurrentAmount() + toPay);
	}

	public double getAmount() {
		return getCurrentAmount() * (1 + getMargin());
	}

	public boolean shouldRentVehicle() {
		return getRentVehicle();
	}

	/* #################### INTERFACE - NEW GETTERS #################### */

	public ActivityInterface getActivityInterface(){
		return this.activityInterface;
	}

	public HotelInterface getHotelInterface(){
		return this.hotelInterface;
	}

	public CarInterface getCarInterface(){
		return this.carInterface;
	}

	public BankInterface getBankInterface(){
		return this.bankInterface;
	}

	/* #################### INTERFACE - NEW SETTERS #################### */

	public void setActivityInterface(ActivityInterface activityInterface){
		this.activityInterface = activityInterface;
	}

	public void setHotelInterface(HotelInterface hotelInterface){
		this.hotelInterface = hotelInterface;
	}

	public void setCarInterface(CarInterface carInterface){
		this.carInterface = carInterface;
	}

	public void setBankInterface(BankInterface bankInterface){
		this.bankInterface = bankInterface;
	}

	/* ############################################################# */

	public void setState(State state) {
		if (getState() != null) {
			getState().delete();
		}

		switch (state) {
		case RESERVE_ACTIVITY:
			setState(new ReserveActivityState());
			break;
		case BOOK_ROOM:
			setState(new BookRoomState());
			break;
		case RENT_VEHICLE:
			setState(new RentVehicleState());
			break;
		case PROCESS_PAYMENT:
			setState(new ProcessPaymentState());
			break;
		case TAX_PAYMENT:
			setState(new TaxPaymentState());
			break;
		case UNDO:
			setState(new UndoState());
			break;
		case CONFIRMED:
			setState(new ConfirmedState());
			break;
		case CANCELLED:
			setState(new CancelledState());
			break;
		default:
			new BrokerException();
			break;
		}
	}

	public void process() {
		// logger.debug("process ID:{}, state:{} ", this.ID, getState().name());
		getState().process();
	}

	public boolean shouldCancelRoom() {
		return getRoomConfirmation() != null && getRoomCancellation() == null;
	}

	public boolean roomIsCancelled() {
		return !shouldCancelRoom();
	}

	public boolean shouldCancelActivity() {
		return getActivityConfirmation() != null && getActivityCancellation() == null;
	}

	public boolean activityIsCancelled() {
		return !shouldCancelActivity();
	}

	public boolean shouldCancelPayment() {
		return getPaymentConfirmation() != null && getPaymentCancellation() == null;
	}

	public boolean paymentIsCancelled() {
		return !shouldCancelPayment();
	}

	public boolean shouldCancelVehicleRenting() {
		return getRentingConfirmation() != null && getRentingCancellation() == null;
	}

	public boolean rentingIsCancelled() {
		return !shouldCancelVehicleRenting();
	}

	public boolean shouldCancelInvoice() {
		return getInvoiceReference() != null && !getInvoiceCancelled();
	}

	public boolean invoiceIsCancelled() {
		return !shouldCancelInvoice();
	}

}

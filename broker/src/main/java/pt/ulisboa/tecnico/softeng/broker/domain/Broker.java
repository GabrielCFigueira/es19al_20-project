package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;

public class Broker extends Broker_Base {
	private static Logger logger = LoggerFactory.getLogger(Broker.class);

	private BankInterface _bankInterface;
	private HotelInterface _hotelInterface;
	private ActivityInterface _activityInterface;
	private TaxInterface _taxInterface;
	private CarInterface _carInterface;

	private RestActivityBookingData _activityBookingData;
	private RestRentingData _rentingData;
	private RestRoomBookingData _roomBookingData;


	public Broker(String code, String name, String nifAsSeller, String nifAsBuyer, String iban) {
		checkArguments(code, name, nifAsSeller, nifAsBuyer, iban);

		setCode(code);
		setName(name);
		setNifAsSeller(nifAsSeller);
		setNifAsBuyer(nifAsBuyer);
		setIban(iban);

		FenixFramework.getDomainRoot().addBroker(this);
	}

	/*------------Constructor for interface testing---------------*/
	public Broker(String code, String name, String nifAsSeller, String nifAsBuyer, String iban,
				  ActivityInterface activityInterface, TaxInterface taxInterface, BankInterface bankInterface,
				  HotelInterface hotelInterface, CarInterface carInterface, RestActivityBookingData activityBookingData,
				  RestRentingData restRentingData, RestRoomBookingData roomBookingData) {

		checkArguments(code, name, nifAsSeller, nifAsBuyer, iban);

		setCode(code);
		setName(name);
		setNifAsSeller(nifAsSeller);
		setNifAsBuyer(nifAsBuyer);
		setIban(iban);

		setActivityInterface(activityInterface);
		setTaxInterface(taxInterface);
		setBankInterface(bankInterface);
		setHotelInterface(hotelInterface);
		setCarInterface(carInterface);
		setActivityBookingData(activityBookingData);
		setRentingData(restRentingData);
		setRoomBookingData(roomBookingData);

		FenixFramework.getDomainRoot().addBroker(this);
	}
	/*----------------------------------------------------------------*/

	public void delete() {
		setRoot(null);

		for (Adventure adventure : getAdventureSet()) {
			adventure.delete();
		}

		for (BulkRoomBooking bulkRoomBooking : getRoomBulkBookingSet()) {
			bulkRoomBooking.delete();
		}

		for (Client client : getClientSet()) {
			client.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(String code, String name, String nifAsSeller, String nifAsBuyer, String iban) {
		if (code == null || code.trim().length() == 0 || name == null || name.trim().length() == 0
				|| nifAsSeller == null || nifAsSeller.trim().length() == 0 || nifAsBuyer == null
				|| nifAsBuyer.trim().length() == 0 || iban == null || iban.trim().length() == 0) {
			throw new BrokerException();
		}

		if (nifAsSeller.equals(nifAsBuyer)) {
			throw new BrokerException();
		}

		for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			if (broker.getCode().equals(code)) {
				throw new BrokerException();
			}
		}

		for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			if (broker.getNifAsSeller().equals(nifAsSeller) || broker.getNifAsSeller().equals(nifAsBuyer)
					|| broker.getNifAsBuyer().equals(nifAsSeller) || broker.getNifAsBuyer().equals(nifAsBuyer)) {
				throw new BrokerException();
			}
		}

	}

	public Client getClientByNIF(String NIF) {
		for (Client client : getClientSet()) {
			if (client.getNif().equals(NIF)) {
				return client;
			}
		}
		return null;
	}

	public boolean drivingLicenseIsRegistered(String drivingLicense) {
		return getClientSet().stream().anyMatch(client -> client.getDrivingLicense().equals(drivingLicense));
	}

	public void bulkBooking(int number, LocalDate arrival, LocalDate departure) {
		BulkRoomBooking bulkBooking = new BulkRoomBooking(this, number, arrival, departure, getNifAsBuyer(), getIban());
		bulkBooking.processBooking();
	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	/* #################### INTERFACE - NEW GETTERS #################### */

	public ActivityInterface getActivityInterface(){
		return this._activityInterface;
	}

	public HotelInterface getHotelInterface(){
		return this._hotelInterface;
	}

	public CarInterface getCarInterface(){
		return this._carInterface;
	}

	public BankInterface getBankInterface(){
		return this._bankInterface;
	}

	public TaxInterface getTaxInterface(){
		return this._taxInterface;
	}

	public RestActivityBookingData getActivityBookingData() { return this._activityBookingData; }

	public RestRentingData getRentingData() { return this._rentingData; }

	public RestRoomBookingData getRoomBookingData() { return this._roomBookingData; }

	/* #################### INTERFACE - NEW SETTERS #################### */

	public void setActivityInterface(ActivityInterface activityInterface){
		this._activityInterface = activityInterface;
	}

	public void setHotelInterface(HotelInterface hotelInterface){
		this._hotelInterface = hotelInterface;
	}

	public void setCarInterface(CarInterface carInterface){
		this._carInterface = carInterface;
	}

	public void setBankInterface(BankInterface bankInterface){
		this._bankInterface = bankInterface;
	}

	public void setTaxInterface(TaxInterface taxInterface){
		this._taxInterface = taxInterface;
	}

	public void setActivityBookingData(RestActivityBookingData activityBookingData) { this._activityBookingData = activityBookingData; }

	public void setRentingData(RestRentingData rentingData) { this._rentingData = rentingData; }

	public void setRoomBookingData(RestRoomBookingData roomBookingData) { this._roomBookingData = roomBookingData; }

	/* ############################################################# */
}

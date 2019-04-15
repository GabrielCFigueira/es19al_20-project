package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.*;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;

import java.util.Objects;

public class Broker extends Broker_Base {
    private ActivityInterface activityInterface = new ActivityInterface();
    private HotelInterface hotelInterface = new HotelInterface();
    private CarInterface carInterface = new CarInterface();
    private BankInterface bankInterface = new BankInterface();
    private TaxInterface taxInterface = new TaxInterface();

    public Broker(String code, String name, String nif, String iban,
                  ActivityInterface activityInterface, HotelInterface hotelInterface, CarInterface carInterface,
                  BankInterface bankInterface, TaxInterface taxInterface) {
        checkArguments(code, name, nif, iban);

        setCode(code);
        setName(name);
        setNif(nif);
        setIban(iban);

        this.activityInterface = activityInterface;
        this.hotelInterface = hotelInterface;
        this.carInterface = carInterface;
        this.bankInterface = bankInterface;
        this.taxInterface = taxInterface;

        FenixFramework.getDomainRoot().addBroker(this);
    }

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

    private void checkArguments(String code, String name, String nif, String iban) {
        if (code == null || code.trim().length() == 0 || name == null || name.trim().length() == 0
                || nif == null
                || nif.trim().length() == 0 || iban == null || iban.trim().length() == 0) {
            throw new BrokerException();
        }

        /*if (nifAsSeller.equals(nifAsBuyer)) {
            throw new BrokerException();
        }*/

        for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
            if (broker.getCode().equals(code)) {
                throw new BrokerException();
            }
        }

        for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
            if (broker.getNif().equals(nif)) {
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
        BulkRoomBooking bulkBooking = new BulkRoomBooking(this, number, arrival, departure, getNif(), getIban());
        bulkBooking.processBooking();
    }

    @Override
    public int getCounter() {
        int counter = super.getCounter() + 1;
        setCounter(counter);
        return counter;
    }

    public RestRoomBookingData getRoomBookingFromBulkBookings(String type, LocalDate arrival, LocalDate departure) {
        return getRoomBulkBookingSet().stream().map(bulkRoomBooking -> bulkRoomBooking.getRoomBookingData4Type(type, arrival, departure)).
                filter(Objects::nonNull).findAny().orElse(null);
    }


    public ActivityInterface getActivityInterface() {
        return this.activityInterface;
    }

    public HotelInterface getHotelInterface() {
        return this.hotelInterface;
    }

    public CarInterface getCarInterface() {
        return this.carInterface;
    }

    public BankInterface getBankInterface() {
        return this.bankInterface;
    }


    public TaxInterface getTaxInterface() {
        return this.taxInterface;
    }

}

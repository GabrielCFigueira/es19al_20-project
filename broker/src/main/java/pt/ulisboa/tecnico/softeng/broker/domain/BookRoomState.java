package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import org.joda.time.Days;

public class BookRoomState extends BookRoomState_Base {
    public static final int MAX_REMOTE_ERRORS = 10;

    @Override
    public State getValue() {
        return State.BOOK_ROOM;
    }

    @Override
    public void process() {
        RestRoomBookingData bookingData = getAdventure().getBroker().getRoomBookingFromBulkBookings(getAdventure().getRoomType().getType().toString(), getAdventure().getBegin(), getAdventure().getEnd());

        if (getAdventure().getReserveRoom() != false || Days.daysBetween(getAdventure().getBegin(),getAdventure().getEnd()).getDays() > 1){ //Check if its possible to reserve a room or adventure has duration of one day
            if (bookingData == null) {
                HotelInterface hotelInterface = getAdventure().getBroker().getHotelInterface();
                try {
                    bookingData = hotelInterface.reserveRoom(new RestRoomBookingData(getAdventure().getRoomType().getType(),
                            getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNif(),
                            getAdventure().getBroker().getIban(), getAdventure().getID()));
                } catch (HotelException he) {
                    getAdventure().setState(State.UNDO);
                    return;
                } catch (RemoteAccessException rae) {
                    incNumOfRemoteErrors();
                    if (getNumOfRemoteErrors() == MAX_REMOTE_ERRORS) {
                        getAdventure().setState(State.UNDO);
                    }
                    return;
                }
            }
        

            getAdventure().setRoomConfirmation(bookingData.getReference());
            getAdventure().incAmountToPay((long) (bookingData.getPrice()*1000));

        }
        if (getAdventure().shouldRentVehicle()) {
            getAdventure().setState(State.RENT_VEHICLE);
        } else {
            getAdventure().setState(State.PROCESS_PAYMENT);
        }
    }

}

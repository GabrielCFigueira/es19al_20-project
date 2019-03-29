package pt.ulisboa.tecnico.softeng.broker.domain;


import java.util.Set;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

public class BookRoomState extends BookRoomState_Base {
	public static final int MAX_REMOTE_ERRORS = 10;

	@Override
	public State getValue() {
		return State.BOOK_ROOM;
	}

	@Override
	public void process() {
		try {
			RestRoomBookingData restdata = new RestRoomBookingData(Type.SINGLE,
			getAdventure().getBegin(), getAdventure().getEnd(), getAdventure().getBroker().getNifAsBuyer(),
			getAdventure().getBroker().getIban(), getAdventure().getID());
			Set<BulkRoomBooking> bulks = getAdventure().getBroker().getRoomBulkBookingSet();
			Boolean foundRoom = false;
			RestRoomBookingData bookingData = null;
			for(BulkRoomBooking bulk : bulks) {/*comparar datas*/
				System.out.println(bulk.getArrival());
				System.out.println(bulk.getDeparture());
				System.out.println("\n\n");
				if (getAdventure().getBegin().isAfter(bulk.getArrival()) && getAdventure().getEnd().isBefore(bulk.getDeparture())){
					bookingData = bulk.getRoomBookingData4Type(Type.SINGLE.toString());
					if(bookingData!= null){
						foundRoom = true;
						break;
					}
				}
			}
			if (!foundRoom){
				bookingData = getAdventure().getBroker().getHotelInterface().reserveRoom(restdata);
			}
			getAdventure().setRoomConfirmation(bookingData.getReference());
			getAdventure().incAmountToPay(bookingData.getPrice());
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

		if (getAdventure().shouldRentVehicle()) {
			getAdventure().setState(State.RENT_VEHICLE);
		} else {
			getAdventure().setState(State.PROCESS_PAYMENT);
		}
	}

}

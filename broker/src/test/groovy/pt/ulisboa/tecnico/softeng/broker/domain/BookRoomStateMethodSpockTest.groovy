package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll
import org.joda.time.LocalDate
import static pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    def broker
    def hotelInterface
    def client
    def adventure
    def bookingData

    @Override
    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), hotelInterface, new CarInterface(), new BankInterface(), new TaxInterface())
        def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        bookingData = new RestRoomBookingData()
        bookingData.setRoomType(SINGLE)
        bookingData.setArrival(BEGIN)
        bookingData.setDeparture(END)
        bookingData.setReference(ROOM_CONFIRMATION)
        bookingData.setPrice(80.0)

        adventure.setState(Adventure.State.BOOK_ROOM)
    }

    def 'success book room fom bulk booking move to payment'() {
        given: 'get the write booking from a bulk booked room is successful'
        hotelInterface.getRoomBookingData(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state progresses to process payment'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
        and: 'the room is confirmed'
        adventure.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'success book room move to payment'() {
        given: 'the hotel reservation is successful'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adventure.process()

        then: 'the adventure state progresses to process payment'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
        and: 'the room is confirmed'
        adventure.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'success book room move to renting'() {
        given: 'an adventure wich includes renting'
        def adv = new Adventure(broker, BEGIN, END, client, MARGIN, true)
        and: 'in book room state'
        adv.setState(Adventure.State.BOOK_ROOM)
        and: 'a successful room booking'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adv.process()

        then: 'the adventure state progresses to rent vehicle'
        adv.getState().getValue() == Adventure.State.RENT_VEHICLE
        and: 'the room is confirmed'
        adv.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    @Unroll('#process_iterations #exception is thrown')
    def '#process_iterations #exception exception'() {
        given: 'the hotel reservation throws a hotel exception'
        hotelInterface.reserveRoom(_) >> { throw mock_exception }

        when: 'a next step in the adventure is processed'
        1.upto(process_iterations) { adventure.process() }

        then: 'the adventure state progresses to undo'
        adventure.getState().getValue() == state
        and: 'the room confirmation is null'
        adventure.getRoomConfirmation() == null

        where:
        mock_exception              | state                     | process_iterations                  | exception
        new HotelException()        | Adventure.State.UNDO      | 1                                   | 'HotelException'
        new RemoteAccessException() | Adventure.State.BOOK_ROOM | 1                                   | 'RemoteAccessException'
        new RemoteAccessException() | Adventure.State.BOOK_ROOM | BookRoomState.MAX_REMOTE_ERRORS - 1 | 'RemoteAccessException'
        new RemoteAccessException() | Adventure.State.UNDO      | BookRoomState.MAX_REMOTE_ERRORS     | 'RemoteAccessException'
    }

    def 'five remote access exception one success'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                { throw new RemoteAccessException() } >>
                bookingData

        when: 'the adventure is processed 6 times'
        1.upto(6) { adventure.process() }

        then: 'the adventure state progresses to process payment'
        adventure.getState().getValue() == Adventure.State.PROCESS_PAYMENT
        and: 'the room is confirmed'
        adventure.getRoomConfirmation() == ROOM_CONFIRMATION
    }

    def 'one remote access exception and one hotel exception'() {
        given: 'the hotel reservation throws a remote access exception'
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when: 'the adventure is processed 5 times'
        adventure.process()

        then: 'the adventure state is book room'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
        and: 'the number of errors is 1'
        adventure.getState().getNumOfRemoteErrors() == 1

        when: 'the adventure is processed again'
        adventure.process()

        then: 'the hotel reservation throws and hotel exception'
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }
        and: 'the adventure state progresses to undo'
        adventure.getState().getValue() == Adventure.State.UNDO
        and: 'the room is confirmation is null'
        adventure.getRoomConfirmation() == null
    }

/*Tests for RoomType refactoring*/
    def 'reserveAdventureWithDurationOfOneDay'(){
        given: 'an adventure which includes reserving'
        def begin = new LocalDate(2016, 12, 19);
        def end = new LocalDate(2016, 12, 20);
        def adv = new Adventure(broker, begin, end, client, MARGIN,true,true);
        def roomType = new RoomType(adv,Type.SINGLE);
        and: 'in book room state'
        adv.setState(Adventure.State.BOOK_ROOM)
        and: 'a successful room booking'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adv.process()

        then: 'the adventure state doesnt progress due to adventures duration'
        adv.getCurrentAmount() == 80.0
    }

    def 'reserveAdventureWithFalseReserveRoomFlag'(){
        given: 'an adventure which includes reserving'
        def begin = new LocalDate(2016, 12, 19);
        def end = new LocalDate(2016, 12, 21);
        def adv = new Adventure(broker, begin, end, client, MARGIN,true,false);
        def roomType = new RoomType(adv,Type.SINGLE);
        and: 'in book room state'
        adv.setState(Adventure.State.BOOK_ROOM)
        and: 'a successful room booking'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adv.process()

        then: 'the adventure state doesnt progress due to adventures reserveRoom flag'
        adv.getCurrentAmount() == 80.0
    }

    def 'sucessfulReserveAdventureWithTrueReserveRoomFlag'(){
        given: 'an adventure which includes reserving'
        def begin = new LocalDate(2016, 12, 19);
        def end = new LocalDate(2016, 12, 21);
        def adv = new Adventure(broker, begin, end, client, MARGIN,true,true);
        def roomType = new RoomType(adv,Type.SINGLE);
        and: 'in book room state'
        adv.setState(Adventure.State.BOOK_ROOM)
        and: 'a successful room booking'
        hotelInterface.reserveRoom(_) >> bookingData

        when: 'a next step in the adventure is processed'
        adv.process()

        then: 'the adventure state progresses to rent vehicle state'
        adv.getState().getValue() == Adventure.State.RENT_VEHICLE
        and: 'the room is confirmed'
        adv.getRoomConfirmation() == ROOM_CONFIRMATION
    }

}

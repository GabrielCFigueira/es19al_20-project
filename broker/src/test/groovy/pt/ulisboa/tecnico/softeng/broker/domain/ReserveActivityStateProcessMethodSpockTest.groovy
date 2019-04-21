package pt.ulisboa.tecnico.softeng.broker.domain

import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*

class ReserveActivityStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def activityInterface
    def broker
    def client
    def adventure
    def bookingData

    @Override
    def populate4Test() {
        activityInterface = Mock(ActivityInterface)
        broker = new Broker("BR01", "eXtremeADVENTURE", NIF_AS_BUYER, BROKER_IBAN,
                            activityInterface, new HotelInterface(), new CarInterface(), new BankInterface(),
                            new TaxInterface())
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        adventure = new Adventure(broker, BEGIN, END, client, MARGIN, CarInterface.Type.CAR, HotelInterface.Type.SINGLE)
        adventure.setState(Adventure.State.RESERVE_ACTIVITY)

        bookingData = new RestActivityBookingData()
        bookingData.setReference(ACTIVITY_CONFIRMATION)
        bookingData.setPrice(767800)
    }

    @Unroll('#label: #rent_a_car #adventure_state')
    def 'success processing adventure'() {
        given: 'activity reserved'
        activityInterface.reserveActivity(_) >> bookingData
        and: 'an adventure on the same day'
        def sameDayAdventure = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, rent_a_car, HotelInterface.Type.SINGLE)
        sameDayAdventure.setState(Adventure.State.RESERVE_ACTIVITY)

        when: 'the adventure is processed'
        sameDayAdventure.process()

        then: 'state of adventure is as expected'
        sameDayAdventure.getState().getValue() == adventure_state

        where:
        rent_a_car              | adventure_state                 | label
        CarInterface.Type.CAR   | Adventure.State.RENT_VEHICLE    | 'success to rent vehicle'
        CarInterface.Type.NONE  | Adventure.State.PROCESS_PAYMENT | 'success no book room'
    }

    def 'success book room'() {
        given: 'activity reserved'
        activityInterface.reserveActivity(_) >> bookingData

        when: 'adventure is processed'
        adventure.process()

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }

    @Unroll('#label: #mock_exception')
    def 'exceptional states'() {
        given: 'activity reservation throws exception'
        activityInterface.reserveActivity(_) >> {throw mock_exception}

        when: 'adventure is processed #process_iterations time(s)'
        1.upto(process_iterations) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == adventure_state

        where:
        mock_exception              | adventure_state                  | process_iterations | label
        new ActivityException()     | Adventure.State.UNDO             | 1                  | 'activity exception'
        new RemoteAccessException() | Adventure.State.RESERVE_ACTIVITY | 1                  | 'single remote access exception'
        new RemoteAccessException() | Adventure.State.UNDO             | 5                  | 'max remote access exception'
        new RemoteAccessException() | Adventure.State.RESERVE_ACTIVITY | 4                  | 'max minus one remote access exception'
    }


    def 'two remote access exception and success'() {
        given: 'activity reservation fails with two remote exceptions and then succeeds'
        activityInterface.reserveActivity(_) >>
                {throw new RemoteAccessException()} >>
                {throw new RemoteAccessException()}  >>
                bookingData

        when: 'adventure is processes 3 times'
        1.upto(3) {
          adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.BOOK_ROOM
    }

    def 'one remote access exception and one activity exception'() {
        given: 'activity reservation fails with a remote exception followed by an activity exception'
        activityInterface.reserveActivity(_) >>
                {throw new RemoteAccessException()} >>
                {throw new ActivityException()}

        when: 'adventure is processes 2 times'
        1.upto(2) {
            adventure.process()
        }

        then: 'state of adventure is as expected'
        adventure.getState().getValue() == Adventure.State.UNDO
    }
}
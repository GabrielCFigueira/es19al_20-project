package pt.ulisboa.tecnico.softeng.broker.domain


import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

import spock.lang.Unroll


// JFF: no need to use public modifier
public class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
	
	def taxInterface = Mock(TaxInterface)
    def hotelInterface = Mock(HotelInterface)
    def bankInterface = Mock(BankInterface)
    def carInterface = Mock(CarInterface)
    def activityInterface = Mock(ActivityInterface)


	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,
				  activityInterface, taxInterface, bankInterface, hotelInterface, carInterface,
				  new RestActivityBookingData(), new RestRentingData(), new RestRoomBookingData())
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

		adventure.setState(State.UNDO)
	}


	def 'success revert payment'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

		bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        when:
		adventure.process()

        then:
		State.CANCELLED == adventure.getState().getValue()
	}

	@Unroll()
    def 'fail revert payment'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)

		bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> { throw exception }

        when:
		adventure.process()

        then:
		State.UNDO == adventure.getState().getValue()

		where:
		exception 					| _
		new BankException() 		| _
		new RemoteAccessException() | _
	}

	def 'success revert activity'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when:
		adventure.process()

        then:
		State.CANCELLED == adventure.getState().getValue()
	}

	@Unroll()
	def 'fail revert activity'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

		activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw exception }

        when:
		adventure.process()

        then:
	    State.UNDO == adventure.getState().getValue()

		where:
		exception 					| _
		new ActivityException() 	| _
		new RemoteAccessException() | _
	}

	def 'success revert room booking'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION

        when:
		adventure.process();

        then:
		State.CANCELLED == adventure.getState().getValue()
	}

	@Unroll()
	def 'success revert room booking with Exception'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> { throw exception }

        when:
		adventure.process()

        then:
		State.UNDO == adventure.getState().getValue()

		where:
		exception 					| _
		new HotelException() 		| _
		new RemoteAccessException() | _
	}

	def 'success revert rent car'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		adventure.setRoomCancellation(ROOM_CANCELLATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        when:
		adventure.process()

        then:
		State.CANCELLED == adventure.getState().getValue()
	}

	@Unroll()
	def 'fail revert rent car'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		adventure.setRoomCancellation(ROOM_CANCELLATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> { throw exception }

        when:
		adventure.process();

        then:
		State.UNDO == adventure.getState().getValue()

		where:
		exception 					| _
		new CarException()	 		| _
		new RemoteAccessException() | _
	}

	def 'success cancel invoice'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		adventure.setRoomCancellation(ROOM_CANCELLATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		adventure.setRentingCancellation(RENTING_CONFIRMATION)
		adventure.setInvoiceReference(INVOICE_REFERENCE)

		taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when:
		adventure.process()

        then:
		State.CANCELLED == adventure.getState().getValue()
	}

	@Unroll()
	def 'fail cancel invoice TaxException'() {
        given:
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)
		adventure.setRoomCancellation(ROOM_CANCELLATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)
		adventure.setRentingCancellation(RENTING_CONFIRMATION)
		adventure.setInvoiceReference(INVOICE_REFERENCE)

		taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw exception }

        when:
		adventure.process()

        then:
		State.UNDO == adventure.getState().getValue()

		where:
		exception 					| _
		new TaxException()	 		| _
		new RemoteAccessException() | _
	}

}
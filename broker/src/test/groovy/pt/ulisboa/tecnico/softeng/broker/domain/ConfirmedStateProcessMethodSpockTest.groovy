package pt.ulisboa.tecnico.softeng.broker.domain


import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll

class ConfirmedStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
	def activityReservationData = Mock(RestActivityBookingData)
	def rentingData = Mock(RestRentingData)
	def roomBookingData = Mock(RestRoomBookingData)
	def bankInterface = Mock(BankInterface)
	def activityInterface = Mock(ActivityInterface)
	def roomInterface = Mock(HotelInterface)
	def carInterface = Mock(CarInterface)
	def taxInterface = Mock(TaxInterface)

	def broker
	def client
	def adventure

	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN, activityInterface, taxInterface,
				bankInterface, roomInterface, carInterface, activityReservationData, rentingData, roomBookingData)

		/*
		adventure.setTaxInterface(taxInterface)
		adventure.setBankInterface(bankInterface)
		adventure.setHotelInterface(roomInterface)
		adventure.setActivityInterface(activityInterface)
		adventure.setCarInterface(carInterface)
		adventure.setActivityBookingData(activityReservationData)
		adventure.setRentingData(rentingData)
		adventure.setRoomBookingData(roomBookingData)
		*/

		adventure.setState(State.CONFIRMED)
	}

	def 'success in all'() {
		given: 'this confirmations and operations'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRentingConfirmation(RENTING_CONFIRMATION)
			adventure.setRoomConfirmation(ROOM_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData
			roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE
			rentingData.getPaymentReference() >> REFERENCE
			rentingData.getInvoiceReference() >> REFERENCE
			roomBookingData.getPaymentReference() >> REFERENCE
			roomBookingData.getInvoiceReference() >> REFERENCE


		when: 'processing the adventure'
			adventure.process()

		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()
	}

	def 'success in activity and hotel'() {
		given: 'this confirmations and operations'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRoomConfirmation(ROOM_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
			roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE
			roomBookingData.getPaymentReference() >> REFERENCE
			roomBookingData.getInvoiceReference() >> REFERENCE

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()
	}

	def 'success in activity and car'() {
		given: 'this confirmations and operations'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRentingConfirmation(RENTING_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData
			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE
			rentingData.getPaymentReference() >> REFERENCE
			rentingData.getInvoiceReference() >> REFERENCE


		when: 'processing this adventure'
			adventure.process()
		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()
	}

	def 'success in activity'() {
		given: 'this confirmations and operations'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> new RestBankOperationData()
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()
	}

	def 'one bank exception'() {
		given: 'bank exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw new BankException()}

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()

	}

	@Unroll('#max_number, #state')
	def 'max bank exception'() {
		given: 'this exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw new BankException()}

		when: 'processing this adventure max_number times'
			1.upto(max_number) {
				adventure.process()
			}
			/*
			for (int i = 0; i < max_number; i++) {
				this.adventure.process()
			}
			*/

		then: 'adventure is state'
			state == adventure.getState().getValue()

		where:
			max_number                             | state
			ConfirmedState.MAX_BANK_EXCEPTIONS     | State.UNDO
			ConfirmedState.MAX_BANK_EXCEPTIONS - 1 | State.CONFIRMED

	}

	def 'one remote access exception in payment'() {
		given: 'this exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw new RemoteAccessException()}

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is confirmed'
			State.CONFIRMED == adventure.getState().getValue()
	}

	@Unroll('#exception, #state')
	def 'activity exceptions'() {
		given: 'this exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> {throw exception}

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is state'
			state == adventure.getState().getValue()

		where:
			exception                   | state
			new ActivityException()     | State.UNDO
			new RemoteAccessException() | State.CONFIRMED
	}

	def 'activity no payment confirmation'() {
		given: 'that payment is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()
	}

	def 'activity no invoice reference'() {
		given: 'that reference is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()
	}

	@Unroll('#exception, #state')
	def 'car exceptions'() {
		given: 'this exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRentingConfirmation(RENTING_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			carInterface.getRentingData(RENTING_CONFIRMATION) >> {throw exception}

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is state'
			state == adventure.getState().getValue()

		where:
			exception                   | state
			new CarException()          | State.UNDO
			new RemoteAccessException() | State.CONFIRMED
	}

	def 'car no payment confirmation'() {
		given: 'that payment is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRentingConfirmation(RENTING_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

			rentingData.getPaymentReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()

	}

	def 'car no invoice reference'() {
		given: 'that invoice is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRentingConfirmation(RENTING_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

			rentingData.getPaymentReference() >> REFERENCE
			rentingData.getInvoiceReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()

	}

	@Unroll('#exception, #state')
	def 'hotel exceptions'() {
		given: 'this exception throw'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRoomConfirmation(ROOM_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> {throw exception}

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is state'
			state == adventure.getState().getValue()

		where:
			exception                   | state
			new HotelException()        | State.UNDO
			new RemoteAccessException() | State.CONFIRMED
	}

	def 'hotel no payment confirmation'() {
		given: 'that payment is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRoomConfirmation(ROOM_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

			roomBookingData.getPaymentReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()
	}

	def 'hotel no invoice confirmation'() {
		given: 'that payment is null'
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
			adventure.setRoomConfirmation(ROOM_CONFIRMATION)

			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> _
			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> activityReservationData

			activityReservationData.getPaymentReference() >> REFERENCE
			activityReservationData.getInvoiceReference() >> REFERENCE

			roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData

			roomBookingData.getPaymentReference() >> REFERENCE
			roomBookingData.getInvoiceReference() >> null

		when: 'processing this adventure'
			adventure.process()

		then: 'adventure is undo'
			State.UNDO == adventure.getState().getValue()
	}

}

package pt.ulisboa.tecnico.softeng.broker.domain


import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException


class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {
	def bookingActivityData
	def bookingRoomData
	def rentingData
	def taxInterface = Mock(TaxInterface)
	def bankInterface = Mock(BankInterface)
	def activityInterface = Mock(ActivityInterface)
	def hotelInterface = Mock(HotelInterface)
	def carInterface = Mock(CarInterface)
	def broker
	def client


	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, BROKER_NIF_AS_BUYER, BROKER_IBAN)
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

		bookingActivityData = new RestActivityBookingData()
		bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
		bookingActivityData.setPrice(70.0)
		bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
		bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

		bookingRoomData = new RestRoomBookingData()
		bookingRoomData.setReference(ROOM_CONFIRMATION)
		bookingRoomData.setPrice(80.0)
		bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
		bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

		rentingData = new RestRentingData()
		rentingData.setReference(RENTING_CONFIRMATION)
		rentingData.setPrice(60.0)
		rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
		rentingData.setInvoiceReference(INVOICE_REFERENCE)
	}


	def successSequence() {
		// Testing: book activity, hotel, car, pay, tax, confirm
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

			carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String, _ as LocalDate, _ as LocalDate, _ as String) >> rentingData

			bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

			taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

			bankInterface.getOperationData(PAYMENT_CONFIRMATION)

			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

			hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CONFIRMED == adventure.getState().getValue()
	}

	def successSequenceOneNoCar() {
		// Testing: book activity, hotel, pay, tax, confirm
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

			bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

			taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

			bankInterface.getOperationData(PAYMENT_CONFIRMATION)

			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

			hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> bookingRoomData

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CONFIRMED == adventure.getState().getValue()
	}


	def successSequenceNoHotel() {

		// Testing: book activity, car, pay, tax, confirm
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
					_ as LocalDate, _ as LocalDate, _ as String) >> rentingData

			bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

			taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

			bankInterface.getOperationData(PAYMENT_CONFIRMATION)

			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

			carInterface.getRentingData(RENTING_CONFIRMATION) >> rentingData

			def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CONFIRMED == adventure.getState().getValue()
	}


	def successSequenceNoHotelNoCar() {
		// Testing: book activity, pay, tax, confirm
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

			taxInterface.submitInvoice(_ as RestInvoiceData) >> INVOICE_DATA

			bankInterface.getOperationData(PAYMENT_CONFIRMATION)

			activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> bookingActivityData

			def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CONFIRMED == adventure.getState().getValue()
	}


	def unsuccessSequenceFailActivity() {
		// Testing: fail activity, undo, cancelled
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> {throw new ActivityException()}

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)

		when:
			adventure.process()
			adventure.process()

		then:
			State.CANCELLED == adventure.getState().getValue()
	}


	def unsuccessSequenceFailHotel() {
		// Testing: activity, fail hotel, undo, cancelled
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> {throw new HotelException()}

			activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CANCELLED == adventure.getState().getValue()
	}


	def unsuccessSequenceFailCar() {
		// Testing: activity, fail car, undo, cancelled
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
					_ as LocalDate, _ as LocalDate, _ as String) >> {throw new CarException()}

			activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

			def adventure = new Adventure(broker, ARRIVAL, ARRIVAL, client, MARGIN, true)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CANCELLED == adventure.getState().getValue()
	}


	def unsuccessSequenceFailPayment() {

		// Testing: activity, room, car, fail payment, undo, cancelled
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

			carInterface.rentCar(_ as CarInterface.Type, _ as String, _ as String, _ as String,
					_ as LocalDate, _ as LocalDate, _ as String) >> rentingData

			bankInterface.processPayment(_ as RestBankOperationData) >> {throw new BankException()}

			activityInterface.cancelReservation(_ as String) >> ACTIVITY_CANCELLATION

			hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION

			carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CANCELLED == adventure.getState().getValue()
	}


	def unsuccessSequenceFailTax() {
		// Testing: activity, room, car, payment, fail tax, undo, cancelled
		given:
			activityInterface.reserveActivity(_ as RestActivityBookingData) >> bookingActivityData

			hotelInterface.reserveRoom(_ as RestRoomBookingData) >> bookingRoomData

			carInterface.rentCar(CarInterface.Type.CAR, _ as String, _ as String, _ as String,
					_ as LocalDate, _ as LocalDate, _ as String) >> rentingData

			bankInterface.processPayment(_ as RestBankOperationData) >> PAYMENT_CONFIRMATION

			taxInterface.submitInvoice(_ as RestInvoiceData) >> {throw new TaxException()}

			activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

			hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION

			carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

			bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

			def adventure = new Adventure(broker, ARRIVAL, DEPARTURE, client, MARGIN, true)
			adventure.setActivityInterface(activityInterface)
			adventure.setBankInterface(bankInterface)
			adventure.setHotelInterface(hotelInterface)
			adventure.setTaxInterface(taxInterface)
			adventure.setCarInterface(carInterface)

		when:
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()
			adventure.process()

		then:
			State.CANCELLED == adventure.getState().getValue()
	}

}
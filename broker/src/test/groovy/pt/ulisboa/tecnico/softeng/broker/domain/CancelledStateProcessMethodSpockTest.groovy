package pt.ulisboa.tecnico.softeng.broker.domain 

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State 
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface 
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface 
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface 
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface 
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData 
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException 
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException 
import spock.lang.Unroll

class CancelledStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

	def taxInterface = Mock(TaxInterface) 
	def bankInterface = Mock(BankInterface)
	def activityInterface = Mock(ActivityInterface)
	def hotelInterface = Mock(HotelInterface)
	def carInterface= Mock(CarInterface)
	
	@Override
	def populate4Test() {
		broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN) 
		client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE) 
		adventure = new Adventure(broker, BEGIN, END, client, MARGIN) 
		adventure.setTaxInterface(taxInterface)
		adventure.setBankInterface(bankInterface)
		adventure.setActivityInterface(activityInterface)
		adventure.setHotelInterface(hotelInterface)
		adventure.setCarInterface(carInterface)
		adventure.setState(State.CANCELLED) 
	}


	def 'didNotPayed'() {
		when:
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue() 
		and:	
			0 * bankInterface.getOperationData(_ as String) >> null
				 
			0 * activityInterface.getActivityReservationData(_ as String) >> null  

			0 * hotelInterface.getRoomBookingData(_ as String) >> null
				  
	}


	
	@Unroll('cancelledPaymentFirst:#_exception')
	def 'cancelledPaymentFirst'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw _exception }
		when:
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue() 
		where:
			_exception						| _
			new BankException()				| _
			new RemoteAccessException() 	| _
			

	}
	def 'cancelledPaymentSecondBankException'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw new RestBankOperationData()} >> {throw new BankException()}
			
		when:	
			adventure.process() 
		then:	
		
			State.CANCELLED == adventure.getState().getValue() 
	}

	def 'cancelledPaymentSecondRemoteAccessException'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> {throw new RestBankOperationData()} >> {throw new RemoteAccessException()} 
			
		when:	
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue()
	}

	def 'cancelledPayment'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> null
			bankInterface.getOperationData(PAYMENT_CANCELLATION) >> null
					
		when:	
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue()
	}


	def 'cancelledActivity'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION) 
			adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> null
			bankInterface.getOperationData(PAYMENT_CANCELLATION) >> null
			activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> null
			
		when:
			adventure.process() 
		then:	

			State.CANCELLED == adventure.getState().getValue()
	}

	def 'cancelledRoom'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION) 
			adventure.setActivityCancellation(ACTIVITY_CANCELLATION) 
			adventure.setRoomConfirmation(ROOM_CONFIRMATION) 
			adventure.setRoomCancellation(ROOM_CANCELLATION)
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> null
			bankInterface.getOperationData(PAYMENT_CANCELLATION) >> null
			activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> null
			hotelInterface.getRoomBookingData(ROOM_CANCELLATION) >> null
		when:			
			adventure.process() 
			
		then:	

			State.CANCELLED == adventure.getState().getValue()
	}

	def 'cancelledRenting'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION) 
			adventure.setActivityCancellation(ACTIVITY_CANCELLATION) 
			adventure.setRentingConfirmation(RENTING_CONFIRMATION) 
			adventure.setRentingCancellation(RENTING_CANCELLATION) 
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> null
			bankInterface.getOperationData(PAYMENT_CANCELLATION) >> null
			activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> null
			carInterface.getRentingData(RENTING_CANCELLATION) >> null
		
		when:	
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue()
	}


	def 'cancelledBookAndRenting'() {
		given:
			adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION) 
			adventure.setPaymentCancellation(PAYMENT_CANCELLATION) 
			adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION) 
			adventure.setActivityCancellation(ACTIVITY_CANCELLATION) 
			adventure.setRoomConfirmation(ROOM_CONFIRMATION) 
			adventure.setRoomCancellation(ROOM_CANCELLATION) 
			adventure.setRentingConfirmation(RENTING_CONFIRMATION) 
			adventure.setRentingCancellation(RENTING_CANCELLATION) 
			bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> null
			bankInterface.getOperationData(PAYMENT_CANCELLATION) >> null
			activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> null
			hotelInterface.getRoomBookingData(ROOM_CANCELLATION) >> null
			carInterface.getRentingData(RENTING_CANCELLATION) >> null
			
		when:	
			adventure.process() 
		then:	
			State.CANCELLED == adventure.getState().getValue() 
	}

}
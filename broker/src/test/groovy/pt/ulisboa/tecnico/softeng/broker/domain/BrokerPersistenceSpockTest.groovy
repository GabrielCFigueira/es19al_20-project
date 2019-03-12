package pt.ulisboa.tecnico.softeng.broker.domain  

import org.joda.time.LocalDate  
import spock.lang.Shared

import pt.ist.fenixframework.FenixFramework

class BrokerPersistenceSpockTest extends SpockPersistenceTestAbstractClass implements SpockBaseTest{

	@Override
	def whenCreateInDatabase() {
		def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)  
		def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)  
		new Adventure(broker, begin, end, client, MARGIN, true)  

		def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, begin, end, NIF_AS_BUYER,
				CLIENT_IBAN)  

		new Reference(bulk, REF_ONE)  
	}

	@Override
	def thenAssert() {
		assert 1 == FenixFramework.getDomainRoot().getBrokerSet().size()

		def brokers = new ArrayList<>(FenixFramework.getDomainRoot().getBrokerSet())  
		def broker = brokers.get(0)  

		assert BROKER_CODE == broker.getCode()
		assert BROKER_NAME == broker.getName()  
		assert 1 == broker.getAdventureSet().size()  
		assert 1 == broker.getRoomBulkBookingSet().size()
		assert NIF_AS_BUYER == broker.getNifAsBuyer()  
		assert BROKER_NIF_AS_SELLER == broker.getNifAsSeller()
		assert BROKER_IBAN == broker.getIban()

		def adventures = new ArrayList<>(broker.getAdventureSet())  
		def adventure = adventures.get(0)  

		assert adventure.getID() != null  
		assert broker == adventure.getBroker() 
		assert begin == adventure.getBegin() 
		assert end == adventure.getEnd()
		assert AGE == adventure.getAge()  
		assert CLIENT_IBAN == adventure.getIban()
		assert adventure.getPaymentConfirmation() == null  
		assert adventure.getPaymentCancellation()== null 
		assert adventure.getRentingConfirmation()== null 
		assert adventure.getRentingCancellation()== null 
		assert adventure.getActivityConfirmation()== null  
		assert adventure.getActivityCancellation()== null 
		assert adventure.getRentingConfirmation()== null 
		assert adventure.getRentingCancellation()== null  
		assert adventure.getInvoiceReference()== null 
		assert adventure.getInvoiceCancelled() == false  
		assert adventure.getRentVehicle() == true  
		assert adventure.getTime() != null  
		assert MARGIN == adventure.getMargin()  
		assert 0.0 == adventure.getCurrentAmount()  
		assert 1 == adventure.getClient().getAdventureSet().size()

		assert Adventure.State.RESERVE_ACTIVITY == adventure.getState().getValue() 
		assert 0 == adventure.getState().getNumOfRemoteErrors()

		def bulks = new ArrayList<>(broker.getRoomBulkBookingSet())  
		def bulk = bulks.get(0)  

		assert bulk != null  
		assert begin == bulk.getArrival()  
		assert end == bulk.getDeparture() 
		assert NUMBER_OF_BULK == bulk.getNumber()  
		assert bulk.getCancelled() == false
		assert 0 == bulk.getNumberOfHotelExceptions()  
		assert 0 == bulk.getNumberOfRemoteErrors()
		assert 1 == bulk.getReferenceSet().size()  
		assert CLIENT_IBAN == bulk.getBuyerIban()  
		assert NIF_AS_BUYER == bulk.getBuyerNif()  

		def references = new ArrayList<>(bulk.getReferenceSet())  
		def reference = references.get(0)  
		assert REF_ONE == reference.getValue()

		def client = adventure.getClient()  
		assert CLIENT_IBAN == client.getIban() 
		assert CLIENT_NIF == client.getNif() 
		assert AGE == client.getAge() 
		assert DRIVING_LICENSE == client.getDrivingLicense()  
	}

	@Override
	def deleteFromDatabase() {
		for (def broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			broker.delete()  
		}
	}

}

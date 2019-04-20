package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.services.remote.*

class BrokerPersistenceSpockTest extends SpockPersistenceTestAbstractClass implements SharedDefinitions {

    @Override
    def whenCreateInDatabase() {
        def broker = new Broker(BROKER_CODE, BROKER_NAME, NIF_AS_BUYER, BROKER_IBAN,
                new ActivityInterface(), new HotelInterface(), new CarInterface(), new BankInterface(), new TaxInterface())
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        new Adventure(broker, this.BEGIN, this.END, client, MARGIN, true)

        def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, this.BEGIN, this.END, NIF_AS_BUYER, CLIENT_IBAN)

        new Reference(bulk, REF_ONE)
    }

    @Override
    def thenAssert() {
        FenixFramework.getDomainRoot().getBrokerSet().size() == 1

        def brokers = new ArrayList<>(FenixFramework.getDomainRoot().getBrokerSet())
        def broker = brokers.get(0)

        broker.getCode().equals(BROKER_CODE)
        broker.getName().equals(BROKER_NAME)
        broker.getAdventureSet().size() == 1
        broker.getRoomBulkBookingSet().size() == 1
        broker.getNif().equals(NIF_AS_BUYER)
        broker.getIban().equals(BROKER_IBAN)

        def adventures = new ArrayList<>(broker.getAdventureSet())
        def adventure = adventures.get(0)

        adventure.getID() != null
        adventure.getBroker() == broker
        adventure.getBegin() == BEGIN
        adventure.getEnd() == END
        adventure.getAge() == AGE
        adventure.getIban().equals(CLIENT_IBAN)
        adventure.getPaymentConfirmation() == null
        adventure.getPaymentCancellation() == null
        adventure.getRentingConfirmation() == null
        adventure.getRentingCancellation() == null
        adventure.getActivityConfirmation() == null
        adventure.getActivityCancellation() == null
        adventure.getRentingConfirmation() == null
        adventure.getRentingCancellation() == null
        adventure.getInvoiceReference() == null
        !adventure.getInvoiceCancelled()
        adventure.getRentVehicle()
        adventure.getTime() != null
        adventure.getMargin() == MARGIN
        adventure.getCurrentAmount() == 0
        adventure.getClient().getAdventureSet().size() == 1

        adventure.getState().getValue() == Adventure.State.RESERVE_ACTIVITY
        adventure.getState().getNumOfRemoteErrors() == 0

        def bulks = new ArrayList<>(broker.getRoomBulkBookingSet())
        def bulk = bulks.get(0)

        bulk != null
        bulk.getArrival() == BEGIN
        bulk.getDeparture() == END
        bulk.getNumber() == NUMBER_OF_BULK
        !bulk.getCancelled()
        bulk.getNumberOfHotelExceptions() == 0
        bulk.getNumberOfRemoteErrors() == 0
        bulk.getReferenceSet().size() == 1
        bulk.getBuyerIban() == CLIENT_IBAN
        bulk.getBuyerNif() == NIF_AS_BUYER

        def references = new ArrayList<>(bulk.getReferenceSet())
        def reference = references.get(0)
        reference.getValue().equals(REF_ONE)

        def client = adventure.getClient()
        client.getIban().equals(CLIENT_IBAN)
        client.getNif().equals(CLIENT_NIF)
        client.getAge() == AGE
        client.getDrivingLicense().equals(DRIVING_LICENSE)
    }

    @Override
    def deleteFromDatabase() {
        for (def broker : FenixFramework.getDomainRoot().getBrokerSet()) {
            broker.delete()
        }
    }
}

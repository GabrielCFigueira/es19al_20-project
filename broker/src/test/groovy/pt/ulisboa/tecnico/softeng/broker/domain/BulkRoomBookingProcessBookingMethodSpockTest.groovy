package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import java.util.Arrays
import java.util.HashSet
import java.util.Set

class BulkRoomBookingProcessBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
    
    def bulk

    def hotelInterface = Mock(HotelInterface)

    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
        bulk.setHotelInterface(hotelInterface)
    }

    def 'success'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
        
        when:
        bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
    }

    def 'successTwice'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> new HashSet<>(Arrays.asList("ref1", "ref2")) >>
         new HashSet<>(Arrays.asList("ref3", "ref4"))
        
        when:
        for (def i = 0; i < 2; i++) 
            bulk.processBooking()
        
        then:
        2 == bulk.getReferences().size()
    }
    
    def 'oneHotelException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new HotelException()} >>
         new HashSet<>(Arrays.asList("ref1", "ref2"))
        
        when:
        for (def i = 0; i < 2; i++) 
            bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }

    def 'maxHotelException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new HotelException()}
        
        when:
        for (def i = 0; i < 3; i++) 
            bulk.processBooking()

        then:
        0 == bulk.getReferences().size()
        true == bulk.getCancelled()
    }

    def 'maxMinusOneHotelException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new HotelException()} >>
         new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        for (def i = 0; i < 3; i++) 
            bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }
    
    def 'hotelExceptionValueIsResetByRemoteException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new HotelException()} >>
         {throw new RemoteAccessException()} >> {throw new HotelException()} >> {throw new HotelException()} >> new HashSet<>(Arrays.asList("ref1", "ref2"))
        
        when:
        for (def i = 0; i < 6; i++) 
            bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }

    def 'oneRemoteException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new RemoteAccessException()} >>
         new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        for (def i = 0; i < 2; i++) 
            bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }

    def 'maxRemoteException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new RemoteAccessException()}

        when:
        def iter = BulkRoomBooking.MAX_REMOTE_ERRORS + 1
        for (def i = 0; i < iter; i++)
            bulk.processBooking()

        then:
        0 == bulk.getReferences().size()
        true == bulk.getCancelled()
    }

    def 'maxMinusOneRemoteException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new RemoteAccessException()} >>
         new HashSet<>(Arrays.asList("ref1", "ref2")) >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> 
         new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        def iter = BulkRoomBooking.MAX_REMOTE_ERRORS * 2
        for (def i = 0; i < iter; i++)
            bulk.processBooking()

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }

    def 'remoteExceptionValueIsResetByHotelException'() {
        given:
        hotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER, bulk.getId()) >> {throw new RemoteAccessException()} >>
         {throw new HotelException()} >> {throw new RemoteAccessException()} >> {throw new RemoteAccessException()} >> 
         new HashSet<>(Arrays.asList("ref1", "ref2"))

        when:
        def iter = BulkRoomBooking.MAX_REMOTE_ERRORS * 2
        for (def i = 0; i < iter; i++)
            bulk.processBooking();

        then:
        2 == bulk.getReferences().size()
        false == bulk.getCancelled()
    }

}
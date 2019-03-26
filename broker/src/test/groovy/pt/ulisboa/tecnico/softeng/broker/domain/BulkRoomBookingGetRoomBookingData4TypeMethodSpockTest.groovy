package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import spock.lang.Unroll
import spock.lang.Shared

class BulkRoomBookingGetRoomBookingData4TypeMethodSpockTest extends SpockRollbackTestAbstractClass {
    def BulkRoomBooking bulk
    def hotelInterface = Mock(HotelInterface)

    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        bulk.setHotelInterface(hotelInterface)
        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)
    }

    @Unroll()
    def 'success'() {
        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(type)
        hotelInterface.getRoomBookingData(_ as String) >> roomBookingData 
        
        when:
        bulk.getRoomBookingData4Type(type)

        then:
        bulk.getReferences().size() == 1

        where:
        type   | _
        SINGLE | _
        DOUBLE | _
    }

    @Unroll("exceptions: #exception, #size")
    def 'exceptions'() {
        given:
        HotelInterface.getRoomBookingData(_ as String) >> {throw exception}

        when:
        bulk.getRoomBookingData4Type(DOUBLE) == null

        then:
        this.bulk.getReferences().size() == size

        where:
        exception                   | size
        new HotelException()        | 2
        new RemoteAccessException() | 2
    }
    
    def 'maxRemoteException'() {
        given:
        hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() }

        when:
        for(int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2; i++){
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        bulk.getReferences().size() == 2
        bulk.getCancelled() == true
    }

    def 'maxMinusOneRemoteException'() {
        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)
        hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        roomBookingData

        when:
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 -1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }
        bulk.getRoomBookingData4Type(DOUBLE)
        
        then:
        bulk.getReferences().size() == 1
    }

    @Unroll()
    def 'remoteExceptionValueIsResetBySuccess'() {
        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)
        hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        roomBookingData >> { throw new RemoteAccessException() }

        when:
         for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        bulk.getReferences().size() == 1
        
        when:
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        bulk.getCancelled() == false

    }
    
    def 'remoteExceptionValueIsResetByHotelException'() {
        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)
        hotelInterface.getRoomBookingData(_ as String) >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>
        { throw new HotelException() } >> { throw new RemoteAccessException() }

        when:
         for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        bulk.getReferences().size() == 2
        
        when:
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        bulk.getCancelled() == false



    }



}

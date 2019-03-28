package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import spock.lang.Unroll

 class TaxPaymentStateSpockTest extends SpockRollbackTestAbstractClass {
    def TRANSACTION_SOURCE = "ADVENTURE"

    def taxInterface = Mock(TaxInterface)
    def broker
    def client
    def adventure

    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        adventure.setTaxInterface(taxInterface)

        adventure.setState(State.TAX_PAYMENT)
    }

    def 'success'() {
        given: 'this invoice'
            taxInterface.submitInvoice(_) >> INVOICE_DATA
        when:  'processing an adventure'
            adventure.process()
        then:   'should succeed'
            adventure.getState().getValue() == State.CONFIRMED
    }
    
    @Unroll(' AccessExceptions: #times , #state , #exception ')
    def 'access exceptions'() {
        given:  'this invoice'
            taxInterface.submitInvoice(_) >> { throw exception }
        
        when:   'processing the adventure X times'
            1.upto(times) { adventure.process() }
        then:   'should succeed'
            adventure.getState().getValue() == state

        where:
            times | state                | exception                  
            2     |State.CANCELLED       | new TaxException()        
            1     |State.TAX_PAYMENT     | new RemoteAccessException()
            4     |State.CANCELLED       | new RemoteAccessException()
            2     |State.TAX_PAYMENT     | new RemoteAccessException()
            3     |State.UNDO            | new RemoteAccessException()
    }

    def 'two remote access exception one success'() {
        given:  'this invoice'
            taxInterface.submitInvoice(_) >> 
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >> PAYMENT_CONFIRMATION
        
        when:   'processing 3 times an adventure'
            1.upto(3) { adventure.process() }

        then:   'should succeed'
            adventure.getState().getValue() == State.CONFIRMED
    }
 
    def 'one remote access exception one tax exception'() {
        given:  'this invoice'
            taxInterface.submitInvoice(_) >> 
            { throw new RemoteAccessException() } >> { throw new TaxException() }
        
        when:   'processing 3 times an adventure'
            1.upto(3) { adventure.process() }

        then:   'should succeed'
            adventure.getState().getValue() == State.CANCELLED
    }
    
}

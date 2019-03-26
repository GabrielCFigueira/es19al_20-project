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
        given:
            taxInterface.submitInvoice(_) >> INVOICE_DATA
        when:
            adventure.process()
        then:
            adventure.getState().getValue() == State.CONFIRMED
    }
    
    @Unroll(' AccessExceptions: #times , #state , #exception ')
    def 'AccessExceptions'() {
        given:
            taxInterface.submitInvoice(_) >> { throw exception }
        
        when:
            for(def i = 0; i < times; i++){
                adventure.process()
            }

        then:
            adventure.getState().getValue() == state

        where:
            times | state                | exception                  
            2     |State.CANCELLED       | new TaxException()        
            1     |State.TAX_PAYMENT     | new RemoteAccessException()
            4     |State.CANCELLED       | new RemoteAccessException()
            2     |State.TAX_PAYMENT     | new RemoteAccessException()
    }

    def 'oneRemoteAccessExceptionOneSuccess'() {
        given:
            taxInterface.submitInvoice(_) >> 
        { throw new RemoteAccessException() } >> PAYMENT_CONFIRMATION
        
        when:
            adventure.process()
            adventure.process()

        then:
            adventure.getState().getValue() == State.CONFIRMED
    }
 
    def 'oneRemoteAccessExceptionOneTaxException'() {
        given:
            taxInterface.submitInvoice(_) >> 
            { throw new RemoteAccessException() } >> { throw new TaxException() }
        
        when:
            adventure.process()
            adventure.process()
            adventure.process()

        then:
            adventure.getState().getValue() == State.CANCELLED
    }
    
}

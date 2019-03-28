package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

import spock.lang.Unroll

 class ProcessPaymentStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {
    def TRANSACTION_SOURCE = "ADVENTURE"

    def taxInterface = Mock(TaxInterface)
    def bankInterface = Mock(BankInterface)
    def broker
    def client
    def adventure

    @Override
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        adventure.setBankInterface(bankInterface)

        adventure.setState(State.PROCESS_PAYMENT)
    }

    def 'success'() {
        given:
            bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        when:
            adventure.process()
        then:
            adventure.getState().getValue() == State.TAX_PAYMENT
    }
    
    @Unroll(' AccessExceptions: #times , #state , #exception ')
    def 'AccessExceptions'() {
        when:
        bankInterface.processPayment(_) >> { throw exception }
        
        then:
        for(def i = 0; i < times; i++){
            adventure.process()
        }
        adventure.getState().getValue() == state

        where:
        times | state                | exception                  
        2     |State.CANCELLED       | new BankException()        
        1     |State.PROCESS_PAYMENT | new RemoteAccessException()
        4     |State.CANCELLED       | new RemoteAccessException()
        2     |State.PROCESS_PAYMENT | new RemoteAccessException()
    }

    def 'twoRemoteAccessExceptionOneSucces'() {
        when:
        3 * bankInterface.processPayment(_) >> 
        { throw new RemoteAccessException() } >> { throw new RemoteAccessException() } >>> [PAYMENT_CONFIRMATION]
        
        then:
        adventure.process()
        adventure.process()
        adventure.process()

        adventure.getState().getValue() == State.TAX_PAYMENT
    }
    
    def 'oneRemoteAccessExceptionOneBankException'() {
        when:
        bankInterface.processPayment(_) >> 
        { throw new RemoteAccessException() } >> { throw new BankException() }
        
        then:
        adventure.process()
        adventure.process()
        adventure.process()

        adventure.getState().getValue() == State.CANCELLED
    }
    
}

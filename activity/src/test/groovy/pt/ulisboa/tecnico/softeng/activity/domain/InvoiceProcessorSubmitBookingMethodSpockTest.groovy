package pt.ulisboa.tecnico.softeng.activity.domain 

import org.joda.time.LocalDate 



import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException 
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import spock.lang.Unroll


class InvoiceProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	      def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference" 
	      def INVOICE_REFERENCE = "InvoiceReference" 
	      def PAYMENT_REFERENCE = "PaymentReference" 
	      def AMOUNT = 30 
	      def IBAN = "IBAN" 
	      def NIF = "123456789" 
		  def provider 
		  def offer 
		  def booking 
		  
		  def taxInterface = Mock(TaxInterface)
		  def bankInterface = Mock(BankInterface)

	@Override
	def populate4Test() {
		def processor = new Processor(bankInterface, taxInterface)
		 provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor) 
		 def activity = new Activity( provider, "Bush Walking", 18, 80, 10) 

		 def begin = new LocalDate(2016, 12, 19) 
		 def end = new LocalDate(2016, 12, 21) 
		 offer = new ActivityOffer(activity, begin, end, AMOUNT) 
		 booking = new Booking( provider,  offer, NIF, IBAN) 
	}

	
	def 'success'() {
		when:
			bankInterface.processPayment(_) >> null
			taxInterface.submitInvoice(_) >> null
		then: 
		 	provider.getProcessor().submitBooking( booking) 

	} 
	
	def 'successCancel'() {
		when:
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
		then:
			taxInterface.cancelInvoice(_ as String) >> null
			bankInterface.cancelPayment(_ as String) >> null
		
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
	}
	
	@Unroll('oneFailureSubmitInvoice:#_exception')
	def 'oneFailureSubmitInvoice'(){
		given:
			bankInterface.processPayment(_) >> PAYMENT_REFERENCE 
			taxInterface.submitInvoice(_) >> {throw _exception} >> INVOICE_REFERENCE
		when:	
			 provider.getProcessor().submitBooking( booking) 
		then:
			1 * taxInterface.submitInvoice(_) >> {throw _exception} 
		when:	
			 provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 
		then: 
			 2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE


		where:
      		_exception					| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	@Unroll('oneFailureProcessPayment:#exception')
	def 'oneFailureProcessPayment'(){
		given:
			bankInterface.processPayment(_) >> {throw exception} >> PAYMENT_REFERENCE
			taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		when:	
		 	provider.getProcessor().submitBooking( booking) 
		then: 
			1 * bankInterface.processPayment(_) >> {throw exception}
		when:	
		 	provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 
		then: 
		 	2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE

		where:
			  exception					| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

	
	
	@Unroll('oneExceptionCancelPayment:#exception')
	def 'oneExceptionCancelPayment'(){
		given:
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
			bankInterface.cancelPayment(_) >> {throw exception}  >> CANCEL_PAYMENT_REFERENCE
			taxInterface.cancelInvoice(_) >> null
			
		when:	
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN))
		then:
			2 * bankInterface.cancelPayment(_) >> {throw exception}  >> CANCEL_PAYMENT_REFERENCE
		
		where:
			  exception					| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

		
	
	@Unroll('oneExceptionCancelInvoice:#exception')
	def 'oneExceptionCancelInvoice'(){
		given:
			bankInterface.processPayment(_) >> null 
			taxInterface.submitInvoice(_) >> null 	
			bankInterface.cancelPayment(_) >> CANCEL_PAYMENT_REFERENCE 
			taxInterface.cancelInvoice(_) >> {throw exception} 
		when:
			provider.getProcessor().submitBooking( booking) 
			booking.cancel()
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN))
		then:
			
			2 * taxInterface.cancelInvoice(_) >> {throw exception} 
			
		where:
			
			  exception					| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	
}	

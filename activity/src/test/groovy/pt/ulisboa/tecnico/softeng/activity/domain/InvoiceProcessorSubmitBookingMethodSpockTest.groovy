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
		when:'mocking the remote invocations to succeed and return null'
			bankInterface.processPayment(_) >> null
			taxInterface.submitInvoice(_) >> null
		then: 'should succeed'
		 	provider.getProcessor().submitBooking( booking) 
	} 
	
	def 'successCancel'() {
		when:'mocking the remote invocations to succeed and return null'
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
		then:'should cancel'
			taxInterface.cancelInvoice(_ as String) >> null
			bankInterface.cancelPayment(_ as String) >> null
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
	}
	
	@Unroll('oneFailureSubmitInvoice:#_exception')
	def 'oneFailureSubmitInvoice'(){
		given:'given mocking the remote invocations to succeed and return references'
			bankInterface.processPayment(_) >> PAYMENT_REFERENCE 
			taxInterface.submitInvoice(_) >> {throw _exception} >> INVOICE_REFERENCE
		when:'when submitting booking'	
			 provider.getProcessor().submitBooking( booking) 
		then:'submit invoice throws exception'
			1 * taxInterface.submitInvoice(_) >> {throw _exception} 
		when:'when submitting new booking'	
			 provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 
		then: 'return invoice reference'
			 2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		where:
      		_exception					| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	@Unroll('oneFailureProcessPayment:#_exception')
	def 'oneFailureProcessPayment'(){
		given:'given mocking the remote invocations to succeed and return references'
			bankInterface.processPayment(_) >> {throw _exception} >> PAYMENT_REFERENCE
			taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		when:'when submitting booking'	
		 	provider.getProcessor().submitBooking( booking) 
		then:'then throws exception' 
			1 * bankInterface.processPayment(_) >> {throw _exception}
		when:'when submitting new booking'	
		 	provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 
		then:'then returns payment reference' 
		 	2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE

		where:
			  _exception				| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

	
	
	@Unroll('oneExceptionCancelPayment:#_exception')
	def 'oneExceptionCancelPayment'(){
		given:'given mocking the remote invocations to succeed and return references or null'
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
			bankInterface.cancelPayment(_) >> {throw _exception}  >> CANCEL_PAYMENT_REFERENCE
			taxInterface.cancelInvoice(_) >> null
			
		when:'submits booking, cancels a booking, submits new booking'	
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN))
		then:'throws exception, returns reference'
			2 * bankInterface.cancelPayment(_) >> {throw _exception}  >> CANCEL_PAYMENT_REFERENCE
		
		where:
			  _exception				| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

		
	
	@Unroll('oneExceptionCancelInvoice:#_exception')
	def 'oneExceptionCancelInvoice'(){
		given: 'given mocking the remote invocations to succeed and return references or null'
			bankInterface.processPayment(_) >> null 
			taxInterface.submitInvoice(_) >> null 	
			bankInterface.cancelPayment(_) >> CANCEL_PAYMENT_REFERENCE 
			taxInterface.cancelInvoice(_) >> {throw _exception} 
		when:'submits booking, cancels a booking, submits new booking'
			provider.getProcessor().submitBooking( booking) 
			booking.cancel()
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN))
		then:'throws exception'
			
			2 * taxInterface.cancelInvoice(_) >> {throw _exception} 
			
		where:
			
			  _exception				| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	
}	

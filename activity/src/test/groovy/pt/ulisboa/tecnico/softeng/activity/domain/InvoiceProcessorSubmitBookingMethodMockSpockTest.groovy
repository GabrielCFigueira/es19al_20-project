package pt.ulisboa.tecnico.softeng.activity.domain 

import org.joda.time.LocalDate 
import org.junit.Test 
import org.junit.runner.RunWith 


import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException 
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException 
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import spock.lang.Unroll
import spock.lang.Shared

class InvoiceProcessorSubmitBookingMethodMockSpockTest extends SpockRollbackTestAbstractClass {
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
		 provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN) 
		 def activity = new Activity( provider, "Bush Walking", 18, 80, 10) 

		 def begin = new LocalDate(2016, 12, 19) 
		 def end = new LocalDate(2016, 12, 21) 
		 offer = new ActivityOffer(activity, begin, end, AMOUNT) 
		 booking = new Booking( provider,  offer, NIF, IBAN) 
	}

	
	def 'success'() {
		when:
			bankInterface.processPayment(_) >> "" 
			taxInterface.submitInvoice(_) >> "" 
			
		then: 

		 provider.getProcessor().submitBooking( booking) 

	} 
	
	def 'successCancel'() {
		when:
			taxInterface.submitInvoice(_) >> ""
			bankInterface.processPayment(_) >> ""
		then:
			taxInterface.cancelInvoice(_ as String) >> ""
			bankInterface.cancelPayment(_ as String) >> ""
		
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
	}
	
	@Unroll('oneFailureSubmitInvoice:#exception')
	def 'oneFailureSubmitInvoice'(){
		when:
			bankInterface.processPayment(_) >> PAYMENT_REFERENCE 
			taxInterface.submitInvoice(_) >> {throw exception} >> INVOICE_REFERENCE
		then:	
		 provider.getProcessor().submitBooking( booking) 
		 provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 

		 for(def i=0;i<3;i++)
			 taxInterface.submitInvoice(_) >> "" 
		where:
      		exception					| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	@Unroll('oneFailureProcessPayment:#exception')
	def 'oneFailureProcessPayment'(){
		when:
			bankInterface.processPayment(_) >> {throw exception} >> PAYMENT_REFERENCE
			taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

		then:	
		 provider.getProcessor().submitBooking( booking) 
		 provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 

		 for(def i=0;i<3;i++)
			 bankInterface.processPayment(_) >> ""
		where:
			  exception					| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

	
	
	@Unroll('oneExceptionCancelPayment:#exception')
	def 'oneExceptionCancelPayment'(){
		when:
			taxInterface.submitInvoice(_) >> ""
			bankInterface.processPayment(_) >> ""
		then:
			bankInterface.cancelPayment(_ as String) >> {throw exception}  >> CANCEL_PAYMENT_REFERENCE
			taxInterface.cancelInvoice(_ as String) >> ""
	
			provider.getProcessor().submitBooking( booking)
			booking.cancel()
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN))

			for(def i=0;i<2;i++)
				bankInterface.cancelPayment(_ as String) >> ""

		where:
			  exception					| _
			new BankException()			| _
			new RemoteAccessException()	| _
	}

		
	
	@Unroll('oneExceptionCancelInvoice:#exception')
	def 'oneExceptionCancelInvoice'(){
		when:
			bankInterface.processPayment(_) >> "" 
			taxInterface.submitInvoice(_) >> "" 
		then:		
			bankInterface.cancelPayment(_ as String) >> CANCEL_PAYMENT_REFERENCE 
			taxInterface.cancelInvoice(_ as String) >> {throw exception} 
		 

			provider.getProcessor().submitBooking( booking) 
			booking.cancel() 
			provider.getProcessor().submitBooking(new Booking( provider,  offer, NIF, IBAN)) 

			
			for(def i=0;i<2;i++)
			   bankInterface.cancelPayment(_ as String) >> ""

		where:
			
			  exception					| _
			new TaxException()			| _
			new RemoteAccessException()	| _
	}
	
	
}	

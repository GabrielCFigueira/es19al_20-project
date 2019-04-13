package pt.ulisboa.tecnico.softeng.hotel.domain 

import org.joda.time.LocalDate 

import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException 
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException 
import spock.lang.Unroll

class ProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass {
	def arrival = new LocalDate(2016, 12, 19) 
	def departure = new LocalDate(2016, 12, 24) 
	def arrivalTwo = new LocalDate(2016, 12, 25) 
	def departureTwo = new LocalDate(2016, 12, 28) 
	def NIF_HOTEL = "123456700" 
	def NIF_BUYER = "123456789" 
	def IBAN_BUYER = "IBAN_BUYER" 

	def taxInterface = Mock(TaxInterface)
	def bankInterface = Mock(BankInterface)


	def hotel 
	def room 
	def booking
	def processor 

	@Override
	def populate4Test() {
		processor = new Processor(bankInterface,taxInterface)
		hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0,processor)
		room = new Room(hotel, "01", Room.Type.SINGLE) 
		booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER) 
	}

	def 'success'() {
		when: 'mocking the remote invocations to succeed and return references'
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null
		then:'submitting a booking'
			hotel.getProcessor().submitBooking(booking)  // JFF: you could use the return values from the mocks to enrich the test
	}


	@Unroll
	def 'taxExceptions'() {
		given:'mocking the remote invocations to succeed and return references'
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			taxInterface.submitInvoice(_ as RestInvoiceData) >> {throw exception}
		when:'submitting a booking'
			hotel.getProcessor().submitBooking(booking)
			hotel.getProcessor().submitBooking(new Booking(room, arrivalTwo, departureTwo, NIF_BUYER, IBAN_BUYER)) 
		then:'invoke the tax interface'
			3 * taxInterface.submitInvoice(_ as RestInvoiceData) >> null
		where:
			exception		                |  _ 
			new TaxException()  			|  _
			new RemoteAccessException()     |  _
			
	}

	@Unroll
	def 'bankExceptions'() {
		given:'mocking the remote invocations to succeed and return references'
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null
			bankInterface.processPayment(_ as RestBankOperationData) >> {throw exception} 
		when:'submitting two different bookings'
			hotel.getProcessor().submitBooking(booking) 
			hotel.getProcessor().submitBooking(new Booking(room, arrivalTwo, departureTwo, NIF_BUYER, IBAN_BUYER)) 
		then:'invokes the bank interface'
			times * bankInterface.processPayment(_ as RestBankOperationData)
		where:
			exception		                | times 
			new BankException() 			|  3
			new RemoteAccessException()     |  3
			
	}

	def 'successCancel'() {
		given:'mocking the remote invocations to succeed and return references'
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			taxInterface.cancelInvoice(_ as String) >> null 
			bankInterface.cancelPayment(_ as String) >> null 
		expect:'submitting booking and cancelling it'
			hotel.getProcessor().submitBooking(booking) 
			booking.cancel() 
	}

	@Unroll
	def 'oneExceptions'() {
		given:'mocking the remote invocations to succeed and return references'
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null 
			bankInterface.processPayment(_ as RestBankOperationData) >> null 
			taxInterface.cancelInvoice(_ as String) >> null 
			bankInterface.cancelPayment(_ as String) >> {throw exception}
		when:'submitting booking'
			hotel.getProcessor().submitBooking(booking) 
		then:'cancelling payment'
			bankInterface.cancelPayment(_ as String) >> null
		when:'cancelling booking and submitting booking'
			booking.cancel() 
			hotel.getProcessor().submitBooking(new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)) 
		then:'cancelling payment'
			bankInterface.cancelPayment(_ as String) >> null
		where:
			exception                   | _
			new BankException()         | _
			new RemoteAccessException()	| _
	}	

	@Unroll
	def 'oneTaxExceptionOnCancelInvoice'() {
		given:'mocking the remote invocations to succeed and return references'
			taxInterface.submitInvoice(_ as RestInvoiceData) >> null
			bankInterface.processPayment(_ as RestBankOperationData) >> null
			bankInterface.cancelPayment(_ as String) >> null
			taxInterface.cancelInvoice(_ as String) >> {throw exception} 
		when:'submitting booking'
			hotel.getProcessor().submitBooking(booking)
		then:
			taxInterface.cancelInvoice(_ as String) >> null
		when:'cancelling and submitting booking'
			booking.cancel() 
			hotel.getProcessor().submitBooking(new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)) 
		then:'cancelling invoice'
			taxInterface.cancelInvoice(_ as String) >> null
		where: 
			exception 					| _ 
			new TaxException()  		| _
			new RemoteAccessException() | _
	}

}
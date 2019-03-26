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
		processor = new Processor()
		processor.setBankInterface(bankInterface);
		processor.setTaxInterface(taxInterface);
		hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0)
		hotel.setProcessor(processor) 
		room = new Room(hotel, "01", Room.Type.SINGLE) 
		booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER) 
	}

	def 'success'() {
		when:
			bankInterface.processPayment(_) >> null
			taxInterface.submitInvoice(_) >> null
		then:
			hotel.getProcessor().submitBooking(booking) 
	}


	@Unroll
	def 'taxExceptions'() {
		given:
			bankInterface.processPayment(_) >> null
			taxInterface.submitInvoice(_) >> {throw exception}
		when:
			hotel.getProcessor().submitBooking(booking) 
		then:
			1 * taxInterface.submitInvoice(_) >> null
		when:
			hotel.getProcessor().submitBooking(new Booking(room, arrivalTwo, departureTwo, NIF_BUYER, IBAN_BUYER)) 
		then:
			2 * taxInterface.submitInvoice(_) >> null
		where:
			exception		                |  _ 
			new TaxException()  			|  _
			new RemoteAccessException()     |  _
			
	}

	@Unroll
	def 'bankExceptions'() {
		given:
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> {throw exception} 
		when:
			hotel.getProcessor().submitBooking(booking) 
			hotel.getProcessor().submitBooking(new Booking(room, arrivalTwo, departureTwo, NIF_BUYER, IBAN_BUYER)) 
		then:
			times * bankInterface.processPayment(_)
		where:
			exception		                | times 
			new BankException() 			|  3
			new RemoteAccessException()     |  3
			
	}

	def 'successCancel'() {
		given:
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
			taxInterface.cancelInvoice(_) >> null 
			bankInterface.cancelPayment(_) >> null 
		expect:
			hotel.getProcessor().submitBooking(booking) 
			booking.cancel() 
	}

	@Unroll
	def 'oneExceptions'() {
		given:
			taxInterface.submitInvoice(_) >> null 
			bankInterface.processPayment(_) >> null 
			taxInterface.cancelInvoice(_) >> null 
			bankInterface.cancelPayment(_) >> {throw exception}
		when:
			hotel.getProcessor().submitBooking(booking) 
		then:
			bankInterface.cancelPayment(_) >> null
		when:
			booking.cancel() 
			hotel.getProcessor().submitBooking(new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)) 
		then: 
			bankInterface.cancelPayment(_) >> null
		where:
			exception                   | _
			new BankException()         | _
			new RemoteAccessException()	| _
	}	

	@Unroll
	def 'oneTaxExceptionOnCancelInvoice'() {
		given:
			taxInterface.submitInvoice(_) >> null
			bankInterface.processPayment(_) >> null
			bankInterface.cancelPayment(_) >> null
			taxInterface.cancelInvoice(_) >> {throw exception} 
		when:
			hotel.getProcessor().submitBooking(booking)
		then:
			taxInterface.cancelInvoice(_) >> null
		when:
			booking.cancel() 
			hotel.getProcessor().submitBooking(new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)) 
		then: 
			taxInterface.cancelInvoice(_) >> null
		where: 
			exception 					| _ 
			new TaxException()  		| _
			new RemoteAccessException() | _
	}

}
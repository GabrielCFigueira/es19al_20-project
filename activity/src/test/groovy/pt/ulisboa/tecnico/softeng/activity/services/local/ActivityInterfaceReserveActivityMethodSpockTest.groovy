package pt.ulisboa.tecnico.softeng.activity.services.local 

import org.joda.time.LocalDate 

import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.domain.Activity 
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer 
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider 
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException 
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface 
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface 
import pt.ulisboa.tecnico.softeng.activity.domain.*
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData 
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData 

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass {
	def IBAN = "IBAN" 
	def NIF = "123456789" 
	def MIN_AGE = 18 
	def MAX_AGE = 50 
	def CAPACITY = 30 

	def provider1 
	def provider2 
    def taxInterface
    def bankInterface
	def processor1
	def processor2

	@Override
	def populate4Test() {
        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
		processor1 = new Processor(bankInterface,taxInterface)
		processor2= new Processor(bankInterface,taxInterface)
		provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN,processor1) 
		provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN,processor2) 
	}

	def 'reserveActivity'() {
        given:'mocking the remote invocations to succeed and return references'
            bankInterface.processPayment(_ as RestBankOperationData) >> null
            taxInterface.submitInvoice(_ as RestInvoiceData) >> null
		when:'executing method'
            def activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY) 
            new ActivityOffer(activity, new LocalDate(2018, 02, 19), new LocalDate(2018, 12, 20), 30) 
            def activityBookingData = new RestActivityBookingData() 
            activityBookingData.setAge(20) 
            activityBookingData.setBegin(new LocalDate(2018, 02, 19)) 
            activityBookingData.setEnd(new LocalDate(2018, 12, 20)) 
            activityBookingData.setIban(IBAN) 
            activityBookingData.setNif(NIF) 
            def bookingData = ActivityInterface.reserveActivity(activityBookingData) 
        then:'testing the bookingData'
		    bookingData != null
		    bookingData.getReference().startsWith("XtremX")
	}

	def 'reserveActivityNoOption'() {
		given:'creating activityBookingData'
			def activityBookingData = new RestActivityBookingData() 
		when:'testing the activityBookingData'
			activityBookingData.setAge(20) 
			activityBookingData.setBegin(new LocalDate(2018, 02, 19)) 
			activityBookingData.setEnd(new LocalDate(2018, 12, 20)) 
			activityBookingData.setIban(IBAN) 
			activityBookingData.setNif(NIF) 
			def bookingData = ActivityInterface.reserveActivity(activityBookingData) 
		then:'throws an exception'
			thrown(ActivityException)
	}

}
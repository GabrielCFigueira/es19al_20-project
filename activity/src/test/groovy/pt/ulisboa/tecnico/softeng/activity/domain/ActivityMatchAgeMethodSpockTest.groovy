package pt.ulisboa.tecnico.softeng.activity.domain

class ActivityMatchAgeMethodSpockTest extends SpockRollbackTestAbstractClass {
	def MIN_AGE = 25
	def MAX_AGE = 80
	def CAPACITY = 30
	
	def activity
	
	def populate4Test() {
		def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
		activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
	}


	// JFF: duplication could be avoided by using data tables
	// JFF: ==true unnecessary
	def 'success'(){
		when: 'difference between MAX_AGE and MIN_AGE'
		def value = (MAX_AGE - MIN_AGE)
		
		then:
		true == activity.matchAge(value.intdiv(2))
	}
	
	def 'successEqualMinAge'(){
		expect:
		true == activity.matchAge(MIN_AGE)
	}
	
	def lessThanMinAge(){
		false == activity.matchAge(MIN_AGE - 1)
	}
	
	def successEqualMaxAge(){
		false == activity.matchAge(MAX_AGE)
	}
	
	def greaterThanMaxAge(){
		false == activity.matchAge(MAX_AGE + 1)
	}
}

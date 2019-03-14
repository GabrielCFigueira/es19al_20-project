package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Shared
import spock.lang.Unroll

class ActivityProviderConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def PROVIDER_CODE = "XtremX"
	@Shared def PROVIDER_NAME = "Adventure++"
	@Shared def IBAN = "IBAN"
	@Shared def NIF = "NIF"

	@Override
	def populate4Test() {
	}


	def 'success'() {
		when: 'creating an activity provider'
		def provider = new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN)

		then: 'should succeed'
		PROVIDER_NAME == provider.getName()
		provider.getCode().length() == ActivityProvider.CODE_SIZE
		1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()
		0 == provider.getActivitySet().size()
	}


	@Unroll('ActivityProvider: #code, #name, #nif, #iban')
	def 'exceptions'() {
		when: 'creating an activity provider with wrong parameters'
		new ActivityProvider(code, name, nif, iban)

		then: 'throws an exception'
		thrown(ActivityException)

		where:
		code          | name          | nif     | iban
		null          | PROVIDER_NAME | NIF     | IBAN
		"      "      | PROVIDER_NAME | NIF     | IBAN
		PROVIDER_CODE | null          | NIF     | IBAN
		PROVIDER_CODE | "    "        | NIF     | IBAN
		"12345"       | PROVIDER_NAME | NIF     | IBAN
		"1234567"     | PROVIDER_NAME | NIF     | IBAN
		PROVIDER_CODE | PROVIDER_NAME | null    | IBAN
		PROVIDER_CODE | PROVIDER_NAME | "   "   | IBAN
	}


	@Unroll('ActivityProvider: #code, #name, #nif')
	def 'duplications'() {
		given: 'an activity provider'
		new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME, NIF, IBAN)

		when: 'creating an activity provider with duplicated parameters'
		new ActivityProvider(code, name, nif, IBAN)

		then: 'throws an exception and it only exists one provider'
		thrown(ActivityException)
		1 == FenixFramework.getDomainRoot().getActivityProviderSet().size()

		where:
		code          | name          | nif
		PROVIDER_CODE | "Hello"       | NIF + "2"
		"123456"      | PROVIDER_NAME | NIF + "2"
		"123456"      | "jdgdsk"      | NIF

	}

}

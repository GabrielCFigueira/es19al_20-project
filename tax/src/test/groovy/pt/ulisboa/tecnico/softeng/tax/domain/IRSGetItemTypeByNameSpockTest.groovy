package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll


class IRSGetItemTypeByNameSpockTest extends SpockRollbackTestAbstractClass {

    def private static final FOOD = "FOOD"
	def private static final VALUE = 16

    def private irs


    @Override
    def populate4Test() {
		this.irs = IRS.getIRSInstance()
		new ItemType(this.irs, FOOD, VALUE)
	}

    def success() {
        when:
   		ItemType itemType = this.irs.getItemTypeByName(FOOD)

        then:
        itemType.getName() != null
		FOOD == itemType.getName()
    }

    @Unroll('testing invalid names')
    def 'test name'() {
        expect:
        itemType == this.irs.getItemTypeByName(name)

        where:
        name | itemType
        null | null
        ""   | null
        "CAR"| null
    }
}
package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def CAR = "CAR"
	@Shared def TAX = 23

	@Shared def irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
	}

    def 'success'() {
        when: 'creating a new ItemType'
        def new_irs = IRS.getIRSInstance()
        def itemType = new ItemType(new_irs, CAR, TAX)

        then: 'should succeed'
        itemType.getName() == CAR
        itemType.getTax() == TAX
        null != IRS.getIRSInstance().getItemTypeByName(CAR)
        new_irs.getItemTypeByName(CAR) == itemType
    }

    def 'uniqueName'() {
        given:
        def itemType = new ItemType(irs, CAR, TAX)
        
        when:
        new ItemType(irs, CAR, TAX)
        
        then:
        thrown(TaxException)
        itemType == IRS.getIRSInstance().getItemTypeByName(CAR)
    }

    def zeroTax() {
        new ItemType(irs, CAR, 0)
    }

    @Unroll('ItemType: #_irs, #_itemType, #_tax')
    def 'exceptions'() {
        when:'creating an ItemType with invalid arguments'
        new ItemType(_irs, _itemType, _tax)

        then:
        thrown(TaxException)

        where: 'cases where some arguments are null or invalid'
        _irs | _itemType | _tax
        irs | null     | TAX
        irs | ""       | TAX
        irs | CAR      | -34


    }
}
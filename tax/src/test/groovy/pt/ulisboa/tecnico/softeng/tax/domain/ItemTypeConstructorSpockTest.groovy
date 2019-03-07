package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.junit.Assert.assertNotNull;

class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared String CAR = "CAR";
	@Shared int TAX = 23;

	@Shared def IRS irs;

	@Override
	def populate4Test() {
		this.irs = IRS.getIRSInstance();
	}

    def 'success'() {
        when: 'creating a new ItemType'
        def IRS irs = IRS.getIRSInstance();
        def itemType = new ItemType(irs, CAR, TAX);

        then: 'should succeed'
        itemType.getName() == CAR
        itemType.getTax() == TAX
        assertNotNull IRS.getIRSInstance().getItemTypeByName(CAR)
        irs.getItemTypeByName(CAR) == itemType
    }

    def 'uniqueName'() {
        def itemType = new ItemType(this.irs, CAR, TAX)
        shouldFail TaxException, {
            new ItemType(this.irs, CAR, TAX)
        }
        assert itemType == IRS.getIRSInstance().getItemTypeByName(CAR)
    }

    def 'zeroTax'() {
        new ItemType(this.irs, CAR, 0)
    }

    @Unroll('ItemType: #irs, #itemType, #tax')
    def 'exceptions'() {
        when:'creating an ItemType with invalid arguments'
        new ItemType(this.irs, itemType, tax)

        then:
        thrown(TaxException)

        where: 'cases where some arguments are null or invalid'
        irs      | itemType | tax
        this.irs | null     | TAX
        this.irs | ""       | TAX
        this.irs | CAR      | -34


    }
}
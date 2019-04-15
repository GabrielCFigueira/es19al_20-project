package pt.ulisboa.tecnico.softeng.car.domain

import spock.lang.Shared
import spock.lang.Unroll

import pt.ulisboa.tecnico.softeng.car.exception.CarException
import pt.ulisboa.tecnico.softeng.car.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.car.services.remote.TaxInterface


class VehicleConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def PLATE_CAR = '22-33-HZ'
	def PLATE_MOTORCYCLE = '44-33-HZ'
	def RENT_A_CAR_NAME = 'Eartz'
	def NIF = 'NIF'
	def IBAN = 'IBAN'
	@Shared def rentACar

	@Override
	def populate4Test() {
		def bankInterface = new BankInterface()
		def taxInterface = new TaxInterface()
		def processor = new Processor(bankInterface, taxInterface)

		rentACar = new RentACar(RENT_A_CAR_NAME, NIF, IBAN, processor)
	}

	def 'success'() {
		when:
		def car = new Car(PLATE_CAR, 10, 20000, rentACar)
		def motorcycle = new Motorcycle(PLATE_MOTORCYCLE, 30, 40000, rentACar)

		then:
		with(rentACar) {
			rentACar.hasVehicle(PLATE_CAR)
			rentACar.hasVehicle(PLATE_MOTORCYCLE)
		}

		with(car) {
			getPlate() == PLATE_CAR
			getKilometers() == 10
			getPrice() == 20000
		}

		with(motorcycle) {
			getPlate() == PLATE_MOTORCYCLE
			getKilometers() == 30
			getPrice() == 40000
		}
	}

	@Unroll('RentACar: #plate, #km, #price, #rac')
	def 'exceptions'() {
		when: 'creating a car with wrong parameters'
		new Car(plate, km, price, rac)

		then: 'throws an exception'
		thrown(CarException)

		where:
		plate       | km | price | rac
		PLATE_CAR   | 0  | 10000 | null
		PLATE_CAR   | -1 | 10000 | rentACar
		'AA-XX-aaa' | 10 | 10000 | rentACar
		'AA-XX-a'   | 10 | 10000 | rentACar
		null        | 10 | 10000 | rentACar
		''          | 10 | 10000 | rentACar
	}

	def 'duplicated plate'() {
		given: 'a car'
		new Car(PLATE_CAR, 0, 10000, rentACar)

		when: 'creating another car with the same plate'
		new Car(PLATE_CAR, 0, 10000, rentACar)

		then: 'throws an exception'
		thrown(CarException)
	}

	def 'duplicated plate different rent a car'() {
		given: 'create a car in rent-a-car'
		new Car(PLATE_CAR, 0, 10000, rentACar)
		and: 'another rent a car'
		def rentACar2 = new RentACar(RENT_A_CAR_NAME + '2', NIF + "1", IBAN,
				new Processor(new BankInterface(), new TaxInterface()))

		when: 'creating a car in the other rent-a-car with the same plate'
		new Car(PLATE_CAR, 2, 10000, rentACar2)

		then: 'throws an exception'
		thrown(CarException)
	}
}

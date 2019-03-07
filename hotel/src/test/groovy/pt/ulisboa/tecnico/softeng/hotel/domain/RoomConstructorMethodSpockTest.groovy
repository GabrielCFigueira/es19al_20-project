package pt.ulisboa.tecnico.softeng.hotel.domain 

import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type 
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException 
import spock.lang.Shared
import spock.lang.Unroll

class RoomConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def hotel 

	
	def populate4Test() {
		hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0) 
	}

	def 'success'() {
		when: 'creating a new room'
			def room = new Room(hotel, "01", Type.DOUBLE) 
		then: 'should succeed'
			hotel == room.getHotel() 
			"01" == room.getNumber() 
			Type.DOUBLE == room.getType() 
			1 == hotel.getRoomSet().size() 
	}

@Unroll('Room: #_hotel, #_name, #_type')
	def 'exceptions'(){
		when: 'creating a new room with wrong parameters'
			new Room(_hotel,_name,_type)
		then: 'throws an exception'
			thrown(HotelException)
		where:
			_hotel  | _name   | _type                
			null    | "01"     | Type.DOUBLE             
			hotel   | null     | Type.DOUBLE             
			hotel   | ""       | Type.DOUBLE               
			hotel   | "     "  | Type.DOUBLE 
			hotel   | "JOSE"   | Type.DOUBLE
			hotel   | "01"     | null
	}

	def 'nonUniqueRoomNumber'() {
		given: 'given a room'
			new Room(hotel, "01", Type.SINGLE) 
		when: 'when creating a similar room'
			new Room(hotel, "01", Type.DOUBLE) 
		then: 'throws exception'
			thrown(HotelException)
		and: 'check if only one room was created'
			1 == hotel.getRoomSet().size()
	}


}

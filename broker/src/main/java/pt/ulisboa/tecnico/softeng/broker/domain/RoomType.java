package pt.ulisboa.tecnico.softeng.broker.domain;
import static pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type;

public class RoomType extends RoomType_Base {
    
    public RoomType(Adventure adv,Type type) {
        setAdventure(adv);
        setType(type);
    }

    public void delete() {
        setType(null);
        setAdventure(null);

		deleteDomainObject();
	}
    
}

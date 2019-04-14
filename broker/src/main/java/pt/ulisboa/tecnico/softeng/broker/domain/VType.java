package pt.ulisboa.tecnico.softeng.broker.domain;
import static pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface.Type;

public class VType extends VType_Base {

    public VType(Adventure adventure, Type vehicleType) {
        setAdventure(adventure);
        setType(vehicleType);
    }

    public void delete() {
        setType(null);
        setAdventure(null);

        deleteDomainObject();
    }
    
}

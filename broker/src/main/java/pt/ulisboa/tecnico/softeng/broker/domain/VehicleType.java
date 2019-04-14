package pt.ulisboa.tecnico.softeng.broker.domain;
import static pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface.Type;

public class VehicleType extends VehicleType_Base {

    public VehicleType(Adventure adventure, Type vehicleType) {
        setAdventure(adventure);
        setType(vehicleType);
    }

    public void delete() {
        setType(null);
        setAdventure(null);

        deleteDomainObject();
    }
    
}

package pt.ulisboa.tecnico.softeng.car.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.car.services.local.RentACarInterface;



@Controller
@RequestMapping(value = "/rentacars/code/{code}/processor")
public class ProcessorController {
    private static Logger logger = LoggerFactory.getLogger(ProcessorController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String pendingShow(Model model, @PathVariable String code) {
        logger.info("processor");
        RentACarInterface rentACarInterface = new RentACarInterface();
        model.addAttribute("pending", rentACarInterface.getPendingRentings(code));
        return "processorView";
    }

}


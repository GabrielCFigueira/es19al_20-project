package pt.ulisboa.tecnico.softeng.activity.presentation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityOfferData;
import pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects.ActivityProviderData;
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData;

@Controller
@RequestMapping(value = "/providers/{codeProvider}/activities/{codeActivity}/offers")
public class ActivityOfferController {
	private static Logger logger = LoggerFactory.getLogger(ActivityOfferController.class);

	private static final ActivityInterface activityInterface = new ActivityInterface();

	@RequestMapping(method = RequestMethod.GET)
	public String offerForm(Model model, @PathVariable String codeProvider, @PathVariable String codeActivity) {
		logger.info("offerForm codeProvider:{}, codeActivity:{}", codeProvider, codeActivity);

		ActivityData activityData = activityInterface.getActivityDataByCode(codeProvider, codeActivity);

		if (activityData == null) {
			model.addAttribute("error", "Error: it does not exist an activity with code " + codeActivity
					+ " in provider with code " + codeProvider);
			model.addAttribute("provider", new ActivityProviderData());
			model.addAttribute("providers", ActivityInterface.getProviders());
			return "providers";
		} else {

			int numOfCancelations = 0;
			for(ActivityOfferData AOD: activityData.getOffers()){
					for(RestActivityBookingData RABD: AOD.getReservations())
						if(RABD.getCancellation() != null)
							numOfCancelations += 1;
				
				AOD.setAvailableCapacity(AOD.getCapacity() - AOD.getReservations().size() + numOfCancelations);
				numOfCancelations = 0;
			}
				
			
			model.addAttribute("offer", new ActivityOfferData());
			model.addAttribute("activity", activityData);
			return "offers";
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String offerSubmit(Model model, @PathVariable String codeProvider, @PathVariable String codeActivity,
			@ModelAttribute ActivityOfferData offer) {
		logger.info("offerSubmit codeProvider:{}, codeActivity:{}, begin:{}, end:{}", codeProvider, codeActivity,
				offer.getBegin(), offer.getEnd());

		try {
			if(offer.getBegin() == null || offer.getEnd() == null)
				throw new ActivityException();
			activityInterface.createOffer(codeProvider, codeActivity, offer);
		} catch (ActivityException e) {

			int numOfCancelations = 0;
			for(RestActivityBookingData RABD: offer.getReservations())
				if(RABD.getCancellation() != null)
					numOfCancelations += 1;
				
			offer.setAvailableCapacity(offer.getCapacity() - offer.getReservations().size() + numOfCancelations);

			model.addAttribute("error", "Error: it was not possible to create de offer");
			model.addAttribute("offer", offer);
			model.addAttribute("activity", activityInterface.getActivityDataByCode(codeProvider, codeActivity));
			return "offers";
		}

		return "redirect:/providers/" + codeProvider + "/activities/" + codeActivity + "/offers";
	}

}

package topica.linhnv5.video.teaching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for home page
 * @author ljnk975
 */
@Controller
public class HomeController {

	@GetMapping(value="/")
	public ModelAndView home() {
		ModelAndView model = new ModelAndView("task");
	    return model;
	}

}

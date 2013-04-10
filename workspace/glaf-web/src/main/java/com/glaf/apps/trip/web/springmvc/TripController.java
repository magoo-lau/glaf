package com.glaf.apps.trip.web.springmvc;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
 

@Controller("/apps/trip")
@RequestMapping("/apps/trip.do")
public class TripController extends TripBaseController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public TripController() {
	    
	}
 
}

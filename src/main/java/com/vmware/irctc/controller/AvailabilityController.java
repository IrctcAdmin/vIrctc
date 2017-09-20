package com.vmware.irctc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.irctc.model.Coaches;
import com.vmware.irctc.service.AvailabilityService;

@RestController
public class AvailabilityController {
	
	@Autowired
	AvailabilityService avail;

	@RequestMapping(value = "/", method=RequestMethod.GET)
	public @ResponseBody String index() {
		return new String("Welcome to Seats Availability API!!!");
	}
	
	@RequestMapping(value = "/availability/{src}/{dest}/{trainId}/{date}", method=RequestMethod.GET)
	public @ResponseBody List<Coaches> showAvailability(@PathVariable("trainId") Integer trainId, @PathVariable("src") String src, @PathVariable("dest") String dest, @PathVariable("date") String date) {
		return avail.getAllAvailableSeatsBySrcAndDestAndDateAndTrainId(trainId, src, dest, date);
	}
	
}

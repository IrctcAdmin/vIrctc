package com.vmware.irctc.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vmware.irctc.model.Coaches;


public interface AvailabilityService {
	
	public List<Coaches> getAllAvailableSeatsBySrcAndDestAndDateAndTrainId(int trainId, String src, String dest, String date);

}

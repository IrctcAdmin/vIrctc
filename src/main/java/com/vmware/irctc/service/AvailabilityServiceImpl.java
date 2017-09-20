package com.vmware.irctc.service;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Session;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmware.irctc.utils.DatabaseService;
import com.vmware.irctc.model.Coaches;

@Service
public class AvailabilityServiceImpl implements AvailabilityService{
	
	@Autowired
    private DatabaseService dbInstance;

	@Override
	public List<Coaches> getAllAvailableSeatsBySrcAndDestAndDateAndTrainId(int trainId, String src, String dest, String date) {
		StringBuffer queryStrBuff = new StringBuffer("SELECT ");
        queryStrBuff.append("coach.coachId, ");
        queryStrBuff.append("coach.coachName ");
        queryStrBuff.append("FROM Coaches coach ");
        queryStrBuff.append("WHERE coach.trainId =:trainId ");
        
        Query query = dbInstance.getEntityManagerInstance().createQuery(queryStrBuff.toString()).setParameter("trainId", trainId);
        List<Object[]> listResult = query.getResultList();
		
		List<Coaches> coaches = new ArrayList<Coaches>();
		for(int i = 0; i < listResult.size(); i++) {
            Object[] obj = listResult.get(i);
            Coaches coach = new Coaches();
            coach.setCoachId((Integer)obj[0]);
            coach.setCoachName((String)obj[1]);
            coaches.add(coach);
        }
        return coaches;
		
	}

}

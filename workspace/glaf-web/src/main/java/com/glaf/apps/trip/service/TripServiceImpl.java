package com.glaf.apps.trip.service;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glaf.core.id.*;
import com.glaf.core.dao.*;
import com.glaf.apps.trip.mapper.*;
import com.glaf.apps.trip.model.*;
import com.glaf.apps.trip.query.*;

@Service("tripService")
@Transactional(readOnly = true) 
public class TripServiceImpl implements TripService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
 
        protected EntityDAO entityDAO;

	protected IdGenerator idGenerator;

	protected SqlSessionTemplate sqlSessionTemplate;

	protected TripMapper tripMapper;

	public TripServiceImpl() {

	}

	@Transactional
	public void deleteById(String id) {
	     if(id != null ){
		tripMapper.deleteTripById(id);
	     }
	}

	@Transactional
	public void deleteByIds(List<String> ids) {
	    if(ids != null && !ids.isEmpty()){
		 for(String id:ids){
			 tripMapper.deleteTripById(id);
		 }
	    }
	}

	public int count(TripQuery query) {
		query.ensureInitialized();
		return tripMapper.getTripCount(query);
	}

	public List<Trip> list(TripQuery query) {
		query.ensureInitialized();
		List<Trip> list = tripMapper.getTrips(query);
		return list;
	}

         
	public int getTripCountByQueryCriteria(TripQuery query) {
		return tripMapper.getTripCount(query);
	}

	 
	public List<Trip> getTripsByQueryCriteria(int start, int pageSize,
			TripQuery query) {
		RowBounds rowBounds = new RowBounds(start, pageSize);
		List<Trip> rows = sqlSessionTemplate.selectList(
				"getTrips", query, rowBounds);
		return rows;
	}


	public Trip getTrip(String id) {
	        if(id == null){
		    return null;
		}
		Trip trip = tripMapper.getTripById(id);
		return trip;
	}

	@Transactional
	public void save(Trip trip) {
           if (StringUtils.isEmpty(trip.getId())) {
			trip.setId(idGenerator.getNextId("X_APP_TRIP"));
			//trip.setCreateDate(new Date());
			trip.setDeleteFlag(0);
			tripMapper.insertTrip(trip);
		} else {
			tripMapper.updateTrip(trip);
		}
	}

	@javax.annotation.Resource 
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	 
	@javax.annotation.Resource 
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
 

	@javax.annotation.Resource
	public void setTripMapper(TripMapper tripMapper) {
		this.tripMapper = tripMapper;
	}

        @javax.annotation.Resource
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

}

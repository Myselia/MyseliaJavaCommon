package com.mycelia.common.communication.distributors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.mycelia.common.communication.Addressable;

public class DistributorFactory {

	/**
	 * Given all the information about the distributor
	 * 
	 * @param distributorType
	 * @param map
	 * @param systemList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Distributor makeDistributor(DistributorType distributorType,
			Map<?, ?> map,
			List<?> systemList) {
		
		switch (distributorType) {
		case FORWARDER:
			return new ForwardDistributor((HashMap<String, CopyOnWriteArrayList<Addressable>>)map, (CopyOnWriteArrayList<Addressable>)systemList);
		case LOAD_BALANCER:
			return new LoadBalanceDistributor();
		default:
			return null;
		}
		
	}

}

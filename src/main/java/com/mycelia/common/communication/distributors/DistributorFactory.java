package com.mycelia.common.communication.distributors;

import java.util.ArrayList;
import java.util.HashMap;

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
	public static Distributor makeDistributor(DistributorType distributorType,
			HashMap<String, ArrayList<Addressable>> map,
			ArrayList<Addressable> systemList) {
		
		switch (distributorType) {
		case FORWARDER:
			return new ForwardDistributor(map, systemList);
		case LOAD_BALANCER:
			return new LoadBalanceDistributor();
		default:
			break;
		}

		return null;
	}

}

package com.mycelia.common.communication.distributors;

import java.util.ArrayList;
import java.util.HashMap;

import com.mycelia.common.communication.Addressable;

public class ForwardDistributor implements Distributor {
	
	private HashMap<String, ArrayList<Addressable>> distributeMap;

	public ForwardDistributor(HashMap<String, ArrayList<Addressable>> map) {
		
	}
	
	@Override
	public void tick() {
		
	}

}

package com.mycelia.common.communication;

import java.util.Collection;
import java.util.HashMap;

import com.mycelia.common.communication.distributors.Distributor;

public class MailService {
    HashMap<String, Collection<Addressable>> mapping;
    Distributor distributor;
     
    public MailService() {
        mapping = new HashMap<String, Collection<Addressable>>();
        initialize_distributor();    
    }
    
    public void register(String field, Addressable subsystem) {
        //mapping.put(field, subsystem);
    }
    
    private void initialize_distributor(){
        
    }
    
    
}
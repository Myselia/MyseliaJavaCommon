package com.mycelia.sandbox.testapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.framework.runtime.*;

public class TestAppMain
{
	private static Logger logger=LoggerFactory.getLogger(TestAppMain.class);
	
	public static void main(String[] args) throws InterruptedException
	{
		MyceliaApplication<MasterNode, SlaveNode> application=
				new MyceliaApplication<MasterNode, SlaveNode>(MasterNode.class, SlaveNode.class);
		
		logger.info("Starting test app...");
		application.start();
		
		Thread.sleep(10*1000);
		
		logger.info("Stopping test app...");
		application.stop();
	}
}

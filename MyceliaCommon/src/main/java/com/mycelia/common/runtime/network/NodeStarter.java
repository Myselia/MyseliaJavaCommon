package com.mycelia.common.runtime.network;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.NodeType;
import com.mycelia.common.runtime.ApplicationRuntimeFactory;
import com.mycelia.common.runtime.ApplicationRuntimeType;
import com.mycelia.common.runtime.LoadBalancerStrategy;

/**
 * This class is called by the Daemon as the Main class of the application jar
 * to bootstrap a specific node (master or slave).
 * 
 * Note: This class' fully qualified class name (FQCN) needs to be in sync with the Daemon.
 */
public class NodeStarter
{
	private static Logger logger=LoggerFactory.getLogger(NodeStarter.class);
	private static final int NODETYPE_ARG=0;
	private static final int MASTER_CLASS_ARG=1;
	private static final int SLAVE_CLASS_ARG=2;
	private static final int DAEMON_PORT_ARG=3;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		//args: NODETYPE MASTERCLASS SLAVECLASS DAEMONPORT
		
		if(args.length!=4)
		{
			printInvalidArguments();
			System.exit(-1);
		}
		
		NodeType nodeType;
		Class<? extends MyceliaMasterNode> masterClass;
		Class<? extends MyceliaSlaveNode> slaveClass;
		int daemonPort;
		
		try
		{
			nodeType=NodeType.getNodeType(args[NODETYPE_ARG]);
			masterClass=(Class<? extends MyceliaMasterNode>)Class.forName(args[MASTER_CLASS_ARG]);
			slaveClass=(Class<? extends MyceliaSlaveNode>)Class.forName(args[SLAVE_CLASS_ARG]);
			daemonPort=Integer.parseInt(args[DAEMON_PORT_ARG]);
		}
		catch(IllegalArgumentException | ClassNotFoundException | ClassCastException e)
		{
			logger.error("invalid arguments: ", e);
			
			printInvalidArguments();
			System.exit(-1);
			return; //Just to javac does not complain.
		}
		
		startNode(nodeType, masterClass, slaveClass, daemonPort);
	}
	
	private static void startNode(NodeType nodeType, Class<? extends MyceliaMasterNode> masterClass,
			Class<? extends MyceliaSlaveNode> slaveClass, int daemonPort)
	{
		Map<String, Object> options=new HashMap<String, Object>(2);
		options.put(NetworkRuntimeOptions.DAEMON_PORT, daemonPort);
		options.put(NetworkRuntimeOptions.LOCAL_NODE_TYPE, nodeType);
		
		NetworkApplicationRuntime runtime=(NetworkApplicationRuntime)ApplicationRuntimeFactory.getInstance().getApplicationRuntime(
				ApplicationRuntimeType.NETWORK, LoadBalancerStrategy.ROUND_ROBIN,
				masterClass, slaveClass, options);
		
		runtime.startLocalNode();
	}
	
	private static void printInvalidArguments()
	{
		logger.error("NodeStarter: Invalid arguments.");
	}
}

package com.mycelia.common.runtime;

import java.util.Set;

import com.mycelia.common.exception.MyceliaRuntimeException;
import com.mycelia.common.framework.MyceliaMasterNode;
import com.mycelia.common.framework.MyceliaSlaveNode;
import com.mycelia.common.framework.RemoteSlaveNode;

public interface NodeContainer
{
	public <M extends MyceliaMasterNode, S extends MyceliaSlaveNode>
		void setNodeClasses(Class<M> masterNodeClass, Class<S> slaveNodeClass);
	
	public void setLoadBalancer(LoadBalancer loadBalancer);
	
	/**
	 * Creates and start the master node.
	 * 
	 * @return
	 * 			The master node's ID.
	 */
	public String createAndStartMasterNode() throws MyceliaRuntimeException;
	
	/**
	 * Stop the master node and deletes all slave nodes.
	 */
	public void stopMasterAndDeleteAllNodes(String localNodeId);
	
	public String getMasterNodeId();
	
	public Set<String> getAllSlaveNodeIds();
	
	/**
	 * Creates a new slave node.
	 */
	public RemoteSlaveNode createSlaveNode(String localNodeId);
	
	/**
	 * Creates multiple new slave nodes.
	 * 
	 * @param numberOfNodes
	 * 			The number of slave nodes to create.
	 */
	public Set<RemoteSlaveNode> createSlaveNodes(String localNodeId, int numberOfNodes);
	
	/**
	 * Gets the <code>RemoteSlaveNode</code> object associated with the specified node ID.  
	 */
	public RemoteSlaveNode getRemoteSlaveNode(String localNodeId, String remoteNodeId);
	
	/**
	 * Deletes a slave node.
	 * This stop execution of all task instances on the node and releases all node resources.
	 * 
	 * @param nodeId
	 * 			The node ID of the node to delete.
	 */
	public void deleteSlaveNode(String localNodeId, String remoteNodeId);
	
	/**
	 * Deletes multiple slave nodes.
	 * This stop execution of all task instances on the nodes and releases all the nodes' resources.
	 * 
	 * @param numberOfNodes
	 * 			The number of slave nodes to delete.
	 */
	public void deleteSlaveNodes(String localNodeId, int numberOfNodes);
	
	/**
	 * Deletes all slave nodes.
	 * This stop execution of all task instances on all the nodes and releases all the nodes' resources.
	 */
	public void deleteAllSlaveNodes(String localNodeId);
}

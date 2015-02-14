package com.mycelia.common.framework;

import java.io.Serializable;
import java.util.Set;

import com.mycelia.common.runtime.LoadBalancer;
import com.mycelia.common.runtime.NodeContainer;

/**
 * The Mycelia Master node is the single node that manages the slave nodes.
 * 
 * The Master node is tasked to mange the number of nodes present in the Mycelia application.
 * It can start new tasks on the slave nodes and get the tasks' results.
 */
public abstract class MyceliaMasterNode extends MyceliaNode
{
	private NodeContainer nodeContainer;
	private LoadBalancer loadBalancer;
	
	//Methods provided by Mycelia framework.
	
	public MyceliaMasterNode()
	{
		//Do nothing.
	}
	
	@Override
	public final void setNodeContainer(NodeContainer nodeContainer)
	{
		this.nodeContainer=nodeContainer;
	}
	
	@Override
	public final void setLoadBalancer(LoadBalancer loadBalancer)
	{
		this.loadBalancer=loadBalancer;
	}
	
	/**
	 * Creates a new slave node.
	 */
	protected final RemoteSlaveNode createSlaveNode()
	{
		return nodeContainer.createSlaveNode(getNodeId());
	}
	
	/**
	 * Creates multiple new slave nodes.
	 * 
	 * @param numberOfNodes
	 * 			The number of slave nodes to create.
	 */
	protected final Set<RemoteSlaveNode> createSlaveNodes(int numberOfNodes)
	{
		return nodeContainer.createSlaveNodes(getNodeId(), numberOfNodes);
	}
	
	/**
	 * Start the specified task on any salve node.
	 * 
	 * @param task
	 * 			The task to execute on a slave node.
	 * 
	 * @return
	 * 			The task instance created.
	 */
	protected final TaskInstance startTaskOnAnyNode(Task task, Serializable... parameters)
	{
		String slaveNodeId=loadBalancer.selectSlaveNode();
		
		RemoteSlaveNode remoteSlave=nodeContainer.getRemoteSlaveNode(getNodeId(), slaveNodeId);
		int taskInstanceId=remoteSlave.startTask(task, parameters);
		
		return remoteSlave.getTaskInstance(taskInstanceId);
	}
	
	/**
	 * Gets the <code>RemoteSlaveNode</code> object associated with the specified node ID.
	 * 
	 * The <code>RemoteSlaveNode</code> class is an interface to
	 * performs operations on a running slave node instance.
	 */
	protected final RemoteSlaveNode getRemoteSlaveNode(String nodeId)
	{
		return nodeContainer.getRemoteSlaveNode(getNodeId(), nodeId);
	}
	
	/**
	 * Deletes a slave node.
	 * This stop execution of all task instances on the node and releases all node resources.
	 * 
	 * @param nodeId
	 * 			The node ID of the node to delete.
	 */
	protected final void deleteSlaveNode(String nodeId)
	{
		nodeContainer.deleteSlaveNode(getNodeId(), nodeId);
	}
	
	/**
	 * Deletes multiple slave nodes.
	 * This stop execution of all task instances on the nodes and releases all the nodes' resources.
	 * 
	 * @param numberOfNodes
	 * 			The number of slave nodes to delete.
	 */
	protected final void deleteSlaveNodes(int numberOfNodes)
	{
		nodeContainer.deleteSlaveNodes(getNodeId(), numberOfNodes);
	}
	
	/**
	 * Deletes all slave nodes.
	 * This stop execution of all task instances on all the nodes and releases all the nodes' resources.
	 */
	protected final void deleteAllSlaveNodes()
	{
		nodeContainer.deleteAllSlaveNodes(getNodeId());
	}
	
	//Methods implemented by the end user.
	
	/**
	 * Life cycle <strong>start</strong> event: called when a Mycelia application starts.  
	 */
	public abstract void start();
	
	/**
	 * Life cycle <strong>stop</strong> event: called when a Mycelia application is requested to stop.  
	 */
	public abstract void stop();
}

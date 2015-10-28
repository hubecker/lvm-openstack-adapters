package com.sap.lvm.storage.openstack.util;

public class CloudClientException extends Exception{
	
	private static final long serialVersionUID = 4553109132767619859L;

	/**
	 * @param message
	 */
	public CloudClientException(String message){
		super (message);
	}
	
	/**
	 * @param t
	 */
	public CloudClientException(Throwable t){
		super (t);
	}

	public CloudClientException(String message, Throwable t){
		super (message, t);
	}
}

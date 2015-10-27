package com.sap.lvm.storage.openstack.block;

import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.core.transport.ProxyHost;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.VolumeAttachment;
import org.openstack4j.model.identity.Access;
import org.openstack4j.model.identity.Access.Service;
import org.openstack4j.model.storage.block.BlockLimits;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.openstack4j.model.storage.block.builder.VolumeBuilder;
import org.openstack4j.openstack.OSFactory;

import com.sap.lvm.storage.openstack.util.CloudClientException;
import com.sap.lvm.storage.openstack.util.OpenstackConstants.OpenstackVolumeStates;
import com.sap.lvm.util.MiscUtil;




public class OpenstackBlockCloudStorageController {
	OSClient os;
	private String region = "default";
	String accessKey;
	String secretKey;
	String endpoint;
	String username;
	String password;
	String tenant;
	String accountId;

	private String proxyHost;
	private int proxyPortint;
	private String proxyPort;
	private String proxyPassword; //TODO: not supported in Openstack4j, remove from UI
	private String proxyUsername; //TODO: not supported in Openstack4j, remove from UI

	public OpenstackBlockCloudStorageController 
	(String accountId,String endpoint, String region,
			String username, String password, String tenant, String proxyHost,
			String proxyPort, String proxyUsername, String proxyPassword) throws CloudClientException {
		try{
			this.region = region;
			this.accountId=accountId;
			if (endpoint != null)
				this.endpoint = endpoint;

			if (username != null)
				this.username = username;

			if (password != null)
				this.password = password;
			if (tenant != null)
				this.tenant = tenant;
			if (proxyHost != null)
				this.proxyHost = proxyHost;
			if (proxyHost != null)
				this.proxyHost = proxyHost;
			if (proxyPort != null)
				this.proxyPort = proxyPort;
			if (tenant != null)
				this.tenant = tenant;

			if (MiscUtil.notNullAndEmpty(proxyHost) && (proxyPort!=null))
			{	int proxyPortint=Integer.parseInt(proxyPort);
			this.proxyPortint=proxyPortint;
			//			System.setProperty("https.proxySet", "true");
			//			System.setProperty("https.proxyHost",this.proxyHost);
			//			System.setProperty("https.proxyPort", this.proxyPort);
			os =

				OSFactory.builder().endpoint(this.endpoint).credentials(username,
						password).tenantName(tenant).withConfig(Config.newConfig().withProxy(ProxyHost.of("http://"+this.proxyHost, this.proxyPortint)))
						.authenticate();
			}
			else
				os =

					OSFactory.builder().endpoint(this.endpoint).credentials(username,
							password).tenantName(tenant).withConfig(Config.newConfig())
							.authenticate();


		} catch (RuntimeException e) {
			throw new CloudClientException("Failed to get Openstack client",e);
		}
	}


	public synchronized List<Volume> getVolumesByServiceId(String serviceId) throws CloudClientException {
		return listVolumes(serviceId);
	}

	public synchronized Volume createVolume(String snapshotId, String availabilityZone, String volumeType,  String description ) throws CloudClientException {


		OSClient os=getOs();
		snapshotId = getOpenstackId(snapshotId);
		availabilityZone = getOpenstackId(availabilityZone);
		VolumeSnapshot sourceSnapshot=getSnapshot(snapshotId);
	
		VolumeBuilder volBuilder = Builders.volume()
		.name("Cloned Volume")
		.description(description)
		.snapshot(snapshotId)
		.zone(availabilityZone);
		if ((volumeType!=null) && (!volumeType.equals("None")))
			volBuilder.volumeType(volumeType);  //in openstack this is just a label with no inherent semantic value and must be created by user in advance

		Volume v = os.blockStorage().volumes()
		.create(volBuilder.build());
		return v;

	}

	public synchronized VolumeSnapshot getSnapshotStatus(String snapshotId) throws CloudClientException {

		snapshotId = getOpenstackId(snapshotId);

		OSClient os=getOs();

		VolumeSnapshot snapshot = os.blockStorage().snapshots().get(snapshotId);
		return snapshot;
	}


	public synchronized VolumeSnapshot createSnapshot(String volumeId, String description, boolean force) throws CloudClientException {

		volumeId = getOpenstackId(volumeId);
		OSClient os=getOs();
		if (volumeId.contains(":"))
			volumeId=volumeId.split(":")[1];
		VolumeSnapshot snap = os.blockStorage().snapshots()
		.create(Builders.volumeSnapshot()
				.name("Backup Volume")
				.description(description)
				.volume(volumeId).force(force)  
				.build());

		return snap;


	}

	private synchronized OSClient getClient(String region) {
		OSClient os = getOs(); //OS not supported for multiple regions at this time
		return os;
	}



	public OSClient getOs() {

		if (MiscUtil.notNullAndEmpty(this.proxyHost) && (this.proxyPort!=null))
		{			

			//	System.setProperty("https.proxySet", "true");
			//	System.setProperty("https.proxyHost",this.proxyHost);
			//	System.setProperty("https.proxyPort", this.proxyPort);
			os =

				OSFactory.builder().endpoint(this.endpoint).credentials(username,
						this.password).tenantName(this.tenant).withConfig(Config.newConfig().withProxy(ProxyHost.of("http://"+this.proxyHost, this.proxyPortint)))
						.authenticate();
		}
		else
			os = OSFactory.builder().endpoint(this.endpoint).credentials(
					this.username, this.password).tenantName(this.tenant)
					.withConfig(Config.newConfig()).authenticate();
		return os;
	}

	

	public synchronized List<String> getRegions() throws CloudClientException {
		List<String> regions = new ArrayList<String>();

		OSClient os=getOs();

		Access access = os.getAccess();
		List<? extends Service> cat = (access.getServiceCatalog());//
		//this just grabs the first region of the first resource ; should make more general solution
		String region = cat.get(0).getEndpoints().get(0).getRegion();


		regions.add(region);


		if (regions.size() < 1)
			regions.add(this.region);
		return regions;
	}

	public String getAccountId() {

		return this.accountId; 
	}

	public String getOpenstackId(String storageSystemId) {
		if (storageSystemId.contains(":"))
			storageSystemId=storageSystemId.split(":")[1];
		return storageSystemId; 
	}

	public List<String> listAvailabilityZones(String region) {
		OSClient os=getOs();
		ArrayList<String> zoneNames=new ArrayList<String>();
//		List<? extends AvailabilityZone> zones = os.compute().zones().list();
//		//zoneItr=zones.iterator();
//		if (zones.size()>0)
//			for (AvailabilityZone zone:zones)
//				zoneNames.add(zone.getZoneName());
//		else
			zoneNames.add("nova")	;
		return zoneNames;
	}

	public String getRegion(String poolId) {

		return this.region;
	}

	public List<Volume> listVolumes(String storagePoolId) {
		// TODO listVolumesBy region and/or zone? 
		OSClient os=getOs();
		List<? extends Volume> volumes = os.blockStorage().volumes().list();


		return (List<Volume>) volumes;
	}
	public BlockLimits getBlockStorageLimits(){
		OSClient os=getOs();
		return os.blockStorage().getLimits();


	}
	public Volume getVolume(String volumeId) {
		OSClient os=getOs();
		//TODO: make this more elegant
		if (volumeId.contains(":"))
			volumeId=volumeId.split(":")[1];
		Volume volume = os.blockStorage().volumes().get(volumeId);

		return volume;
	}

	public ActionResponse detachVolume(String storageVolumeId) {
		OSClient os=getOs();
		if (storageVolumeId.contains(":"))
			storageVolumeId=storageVolumeId.split(":")[1];
		Volume volume=os.blockStorage().volumes().get(storageVolumeId);
		String serverId=volume.getAttachments().get(0).getServerId(); //TODO: verify this
		ActionResponse response = os.compute().servers().detachVolume(serverId, storageVolumeId);
		return response;

	}

	public synchronized void  attachVolume(String storageVolumeId, String instanceId,
			String device) {
	
		OSClient os=getOs();
		VolumeAttachment res =null;
		storageVolumeId=getOpenstackId(storageVolumeId);
		String updatedStatus=getVolume(storageVolumeId).getStatus().toString();
		//we have to check here if the volume is available before attaching 
		//due to a race condition LVM could try to mount the same volume twice and end up throwing an error here on the second try

		if (OpenstackVolumeStates.available.toString().equals(updatedStatus)) 				
			os.compute().servers().attachVolume(instanceId, storageVolumeId, device);
	
	}


	public void deleteVolume(String storageVolumeId) {
		OSClient os=getOs();
		storageVolumeId=getOpenstackId(storageVolumeId);
		os.blockStorage().volumes().delete(storageVolumeId);
	}

	public VolumeSnapshot getSnapshot(String snapshotId) {
		OSClient os=getOs();
		VolumeSnapshot snap = os.blockStorage().snapshots().get(snapshotId);
		return snap;
	}

	public void deleteSnapshot(String snapshotId) throws CloudClientException  {
	
		OSClient os=getOs();
		ActionResponse result = os.blockStorage().snapshots().delete(snapshotId);
		
		if (!result.isSuccess())		
				throw new CloudClientException("Failed to delete Openstack snapshot:"+result.toString());
	}

	public Server findInstanceByInternalIP(String region, String hostAddress) {
		// not universally supported in Openstack 
		return null;
	}


	public boolean supportsIOPS(String ebsType) {	
		return false;
	}


	public VolumeSnapshot copy(String snapshotId, String targetRegion,
			String string) {
		//copy volumes across regions not supported 
		return null;
	}

}

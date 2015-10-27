package com.sap.lvm.storage.openstack.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.core.transport.ProxyHost;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.ext.AvailabilityZone;
import org.openstack4j.model.identity.Access;
import org.openstack4j.model.identity.Access.Service;
import org.openstack4j.model.storage.file.Share;
import org.openstack4j.model.storage.file.ShareAccessMapping;
import org.openstack4j.model.storage.file.SharePool;
import org.openstack4j.model.storage.file.ShareSnapshot;
import org.openstack4j.model.storage.file.Share.Status;
import org.openstack4j.openstack.OSFactory;

import com.sap.lvm.util.MiscUtil;
import com.sap.lvm.storage.openstack.util.CloudClientException;


public class OpenstackFileCloudStorageController {

	static OSClient os;
	private String defaultregion = "default";
	private String defaultbackend = "defaultbackend";
	private String defaultpool = "defaultpool";
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

	//	private HttpProxyDataImpl httpProxy;

	public OpenstackFileCloudStorageController 
	(String accountId,String endpoint, String region,
			String username, String password, String tenant, String proxyHost,
			String proxyPort, String proxyUsername, String proxyPassword) throws CloudClientException {

		try{
			this.defaultregion = region;
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
			os = OSFactory.builder().endpoint(this.endpoint).credentials(username,
						password).tenantName(tenant).withConfig(Config.newConfig().withProxy(ProxyHost.of("http://"+this.proxyHost, this.proxyPortint)))
						.authenticate();
			}
			else
				os = OSFactory.builder().endpoint(this.endpoint).credentials(username,
							password).tenantName(tenant).withConfig(Config.newConfig())
							.authenticate();
		} catch (RuntimeException e) {
			throw new CloudClientException("Failed to get Openstack client",e);
		}
	}


	public synchronized List<Share> getVolumesByServiceId(String serviceId) throws CloudClientException {
		return listShares(serviceId);
	}

	public synchronized Share createShare(String snapshotId, String availabilityZone, String shareType,  String description ) throws CloudClientException {
		OSClient os=getOs();
		snapshotId = getOpenstackId(snapshotId);
		if (shareType==null)
			shareType="general";

		Share builder = Builders.share().name("Cloned Share").description(description).snapshot(snapshotId).zone(availabilityZone).build();
		Share share = os.fileStorage().shares().create(builder);

		return share;
	}

	public synchronized ShareSnapshot createShareSnapshot(String shareId, String name, String description) throws CloudClientException {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		ShareSnapshot builder = Builders.shareSnapshot().name(name).description(description).share(shareId).build();
		ShareSnapshot snap = os.fileStorage().snapshots().create(builder);
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
			os = OSFactory.builder().endpoint(this.endpoint).credentials(username,
						this.password).tenantName(this.tenant).withConfig(Config.newConfig().withProxy(ProxyHost.of("http://"+this.proxyHost, this.proxyPortint)))
						.authenticate();
		}
		else
			os = OSFactory.builder().endpoint(this.endpoint).credentials(
					this.username, this.password).tenantName(this.tenant)
					.withConfig(Config.newConfig()).authenticate();
		return os;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listBackends() {
		OSClient os=getOs();
		Set<String> backends = new HashSet();		
		List<? extends SharePool> sharePools = os.fileStorage().schedulerStats().listPools();
		if (sharePools.size()>0)
			for (SharePool sharePool : sharePools) {
				backends.add(sharePool.getBackend());
			}
		else
			backends.add(defaultbackend);
		return new ArrayList<String>(backends);
	}	

	public String getAccountId() {
		return this.accountId; 
	}

	public String getOpenstackId(String id) {
		if (id.contains(":"))
			id=id.split(":")[1];
		return id; 
	}

	public String getBackend(String shareId) {
		
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		shareId = shareId.trim();
		Share share = os.fileStorage().shares().get(shareId);
		String spId = share.getHost().split("@")[1];
		String backend = spId.split("#")[0];

		return backend; 
	}

	public List<String> listPools(String backend) {
		OSClient os=getOs();
		ArrayList<String> sPools=new ArrayList<String>();
		List<? extends SharePool> sharePools = os.fileStorage().schedulerStats().listPools();
		if (sharePools.size()>0)
			for (SharePool sharePool : sharePools) {
				if (MiscUtil.notNullAndEmpty(backend)) {
					String spId = sharePool.getBackend();
					if (MiscUtil.equals(spId, backend)) {
						sPools.add(sharePool.getPool());
					}
				} else {
					sPools.add(sharePool.getPool());
				}
			}
		else
			sPools.add(defaultpool);
		return sPools;
	}

	public String getPool(String shareId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		Share share = os.fileStorage().shares().get(shareId);
		String spId = share.getHost().split("@")[1];
		String pool = spId.split("#")[1];

		return pool;
	}

	public String getRegion(String poolId) {
		return this.defaultregion;
	}

	public List<Share> listShares(String storagePoolId) {

		OSClient os=getOs();
		storagePoolId = getOpenstackId(storagePoolId);
		List<Share> listShares = new ArrayList<Share>();
		
		List<? extends Share> shares = os.fileStorage().shares().list();
		for (Share share : shares) {
			if (MiscUtil.notNullAndEmpty(storagePoolId)) {
				String spId = share.getHost().split("@")[1];
				spId = spId.replace("#",":");
				if (MiscUtil.equals(spId, storagePoolId)) {
					listShares.add(share);
				}
			} else {
				listShares.add(share);
			}
		}
		return listShares;
	}

	public List<? extends ShareAccessMapping> listAccess(String shareId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);		
		List<? extends ShareAccessMapping> accessList = os.fileStorage().shares().access().list(shareId);
		return accessList;
	}

	public ShareAccessMapping getAccess(String shareId, String shareAccessId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);		
		List<? extends ShareAccessMapping> accessList = os.fileStorage().shares().access().list(shareId);
		for (ShareAccessMapping shareAccessMapping : accessList) {
			if (MiscUtil.equals(shareAccessMapping.getId(),shareAccessId)) {
				return shareAccessMapping;
			} 
		}
		return null;
	}

	public Boolean allowAccess(String shareId, String accessTo) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);		
		ShareAccessMapping resp = os.fileStorage().shares().access().allow(accessTo, "ip", shareId);
		String shareAccessId = resp.getId();
		Boolean active = false;
		while (!MiscUtil.equals(resp.getState(), "active")) {
			resp = getAccess(shareId, shareAccessId);
		}
		return active;
	}
	
	public Share getShare(String shareId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		Share share = os.fileStorage().shares().get(shareId);
		return share;
	}

	public Share getSharebyExport(String exportlocation) {
		OSClient os=getOs();
		exportlocation = exportlocation.trim();
		List<? extends Share> shares = os.fileStorage().shares().list();
		for (Share share : shares) {
			if (exportlocation.contains(share.getExport().trim())) {
				return share;
			}
		}
		return null;
	}

	public Share getSharebyName(String shareName) {
		OSClient os=getOs();
		List<? extends Share> shares = os.fileStorage().shares().list();
		for (Share share : shares) {
			if (MiscUtil.equals(share.getName(), shareName)) {
				return share;
			}
		}
		return null;
	}

	public Status getShareStatus(String shareId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		Status status = os.fileStorage().shares().get(shareId).getStatus();
		return status;
	}

	public void deleteShare(String shareId) throws CloudClientException {
		OSClient os=getOs();
		shareId=getOpenstackId(shareId);
		ActionResponse response = os.fileStorage().shares().delete(shareId);
		if (!response.isSuccess())		
			throw new CloudClientException("Failed to delete Openstack share:"+response.toString());
	}

	public ShareSnapshot getSnapshot(String snapshotId) {
		OSClient os=getOs();
		snapshotId = getOpenstackId(snapshotId);
		ShareSnapshot snap = os.fileStorage().snapshots().get(snapshotId);
		return snap;
	}

	public List<ShareSnapshot> getVCMSnapshots(String shareId) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		List<ShareSnapshot> VCMshareSnapshots = new ArrayList<ShareSnapshot>();
		Map<String, String> filteringParams = new HashMap<String, String>();
		filteringParams.put("share_id", shareId);
		List<? extends ShareSnapshot> shareSnapshots = os.fileStorage().snapshots().list(filteringParams);
		for (ShareSnapshot shareSnapshot : shareSnapshots) {
			VCMshareSnapshots.add(os.fileStorage().snapshots().get(shareSnapshot.getId()));
		}
		return VCMshareSnapshots;
	}
	public ShareSnapshot getVCMSnapshot(String shareId,String sapshotName) {
		OSClient os=getOs();
		shareId = getOpenstackId(shareId);
		ShareSnapshot VCMshareSnapshot = null;
		Map<String, String> filteringParams = new HashMap<String, String>();
		filteringParams.put("share_id", shareId);
		List<? extends ShareSnapshot> shareSnapshots = os.fileStorage().snapshots().list(filteringParams);
		for (ShareSnapshot shareSnapshot : shareSnapshots) {
			if (shareSnapshot.getName().contains(sapshotName)) {
				VCMshareSnapshot = os.fileStorage().snapshots().get(shareSnapshot.getId());
				break;
			}

		}
		return VCMshareSnapshot;
	}

	public synchronized Status getSnapshotStatus(String snapshotId) throws CloudClientException {
		OSClient os=getOs();
		snapshotId = getOpenstackId(snapshotId);
		Status status = os.fileStorage().snapshots().get(snapshotId).getStatus();
		return status;
	}

	public void deleteSnapshot(String snapshotId) throws CloudClientException  {
		OSClient os=getOs();
		snapshotId = getOpenstackId(snapshotId);
		ActionResponse response = os.fileStorage().snapshots().delete(snapshotId);
		if (!response.isSuccess())		
			throw new CloudClientException("Failed to delete Openstack snapshot:"+response.toString());
	}

	public ShareSnapshot copy(String snapshotId, String backend, String string) {
		//copy volumes across backends not supported
		return null;
	}

	public Share createShareFromSnapshot(String shareName, ShareSnapshot snapshot, String shareType) {

		OSClient os=getOs(); 
		Share sharePrototype = Builders.share().snapshot(snapshot.getId()).shareType(shareType).name(shareName).size(snapshot.getSize()).protocol(snapshot.getProtocol()).build();
		Share share = os.fileStorage().shares().create(sharePrototype);
		return share;
	}

	public ShareSnapshot createSnapshot(Share share, String snapshotName) {

		OSClient os=getOs(); 
		ShareSnapshot builder = Builders.shareSnapshot().share(share.getId()).name(snapshotName).build();
		ShareSnapshot snapshot = os.fileStorage().snapshots().create(builder);
		return snapshot;
	}

	private static ShareAccessMapping allowAccess(OSClient os, String accessTo,String accessType, Share share) {

		ShareAccessMapping accessMapping = os.fileStorage().shares().access().allow(accessTo, accessType, share.getId());
		return accessMapping;
	}
	public static boolean isCloneAvailable(String cloneShareId) {

		Share cloneShare = os.fileStorage().shares().get(cloneShareId);
		if (Status.AVAILABLE.equals(cloneShare.getStatus())) {
			allowAccess(os, "0.0.0.0/0", "ip", cloneShare);
			return true;
		}
		return false;
	}

	public static boolean isCloneNotAvailable(String cloneShareId) {
		return !isCloneAvailable(cloneShareId);
	}

	public static boolean areClonesAvailable(Set<String> cloneShareSet) {

		for (String cloneShareId : cloneShareSet) {
			if (isCloneNotAvailable(cloneShareId)) {
				return false;
			}
		}
		return true;
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
			regions.add(this.defaultregion);
		return regions;
	}


	public List<String> listAvailabilityZones(String region) {
		OSClient os=getOs();
		ArrayList<String> zoneNames=new ArrayList<String>();
		List<? extends AvailabilityZone> zones = os.compute().zones().list();
		//zoneItr=zones.iterator();
		if (zones.size()>0)
			for (AvailabilityZone zone:zones)
				zoneNames.add(zone.getZoneName());
		else
			zoneNames.add("DefaultZone")	;
		return zoneNames;
	}

}




package com.sap.lvm.storage.openstack.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openstack4j.model.storage.block.Volume;

import com.sap.lvm.storage.openstack.util.CloudClientException;
import com.sap.lvm.storage.openstack.util.OpenstackConstants;
import com.sap.nw.lm.aci.engine.api.base.property.IPropertyType.ValueType;
import com.sap.nw.lm.aci.engine.base.api.i18n.TranslatableString;
import com.sap.tc.vcm.infrastructure.api.adapter.InfrastructAdapterException;
import com.sap.tc.vcm.infrastructure.api.adapter.IInfrastructAdapter.ExternalURL;
import com.sap.tc.vcm.infrastructure.api.adapter.config.ConfigPropMetaData;
import com.sap.tc.vcm.infrastructure.api.adapter.config.IInfrastructAdapterConfigMetaData.ConfigRequirement;
import com.sap.tc.vcm.infrastructure.api.adapter.request.IJavaEeLog;
import com.sap.tc.vcm.storage.adapter.api.IStorageManagerAdapter;
import com.sap.tc.vcm.storage.adapter.api.IStorageManagerAdapterFactory;
import com.sap.tc.vcm.storage.adapter.api.base.IStorageManagerAdapterConfig;
import com.sap.tc.vcm.storage.adapter.api.base.StorageManagerAdapterConfigMetaData;
import com.sap.tc.vcm.storage.adapter.api.retrieval.GetStorageSystemsRequest;
import com.sap.tc.vcm.storage.adapter.api.types.StorageSystem;




public class OpenstackBlockStorageAdapterFactory implements IStorageManagerAdapterFactory {
	static public String DOMAIN_INFRASTRUCTURE="InfrastructureTexts";

	@Override
	public String getPartnerId() {
		return OpenstackConstants.Openstack_VENDOR;
	}

	@Override
	public String getRequiredStorageLibraryVersion() {
		return OpenstackConstants.Openstack_VERSION;
	}

	@Override
	public List<StorageSystem> getStorageSystems(
			IStorageManagerAdapterConfig sMgrConfig)
			throws InfrastructAdapterException {

		GetStorageSystemsRequest request  = new GetStorageSystemsRequest();
		return createAdapterInstance(sMgrConfig).getStorageRetrieval().getStorageSystems(request).getPayload().storageSystems;


	}

	@Override
	public boolean isExportPathEqual(String exportPath1, String exportPath2) {
		if (exportPath1 == null) return false;
		return exportPath1.equals(exportPath2);
	}

	@Override
	public boolean isMountOptionsEqual(String mountOptions1,
			String mountOptions2) {
		if (mountOptions1 == null) return false;
		return mountOptions1.equals(mountOptions2);
	}

	@Override
	public boolean isNasSupported() {
		return false;
	}

	@Override
	public boolean isSanSupported() {
		return true;
	}

	@Override
	public boolean isStorageLibraryVersionCompatible(short major, short minor,
			short patch) {

		return true;
	}

	@Override
	public IStorageManagerAdapter createAdapterInstance(
			IStorageManagerAdapterConfig config)
	throws InfrastructAdapterException {
		return new OpenstackBlockStorageManagerAdapter(config);
	}

	@Override
	public StorageManagerAdapterConfigMetaData getAdapterConfigMetaData() {
		StorageManagerAdapterConfigMetaData adapterConfigData = new StorageManagerAdapterConfigMetaData();

		List<ConfigPropMetaData> configMetaDatas = new ArrayList<ConfigPropMetaData>();


		TranslatableString key1 = new TranslatableString(OpenstackConstants.TENANT, DOMAIN_INFRASTRUCTURE, "TENANT");
		TranslatableString description1 = new TranslatableString(OpenstackConstants.TENANT_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "TENANT_DESCRIPTION");

		TranslatableString key2 = new TranslatableString(OpenstackConstants.REGION, DOMAIN_INFRASTRUCTURE, "REGION");
		TranslatableString description2 = new TranslatableString(OpenstackConstants.REGION, DOMAIN_INFRASTRUCTURE, "REGION");
		
		TranslatableString key4 = new TranslatableString(OpenstackConstants.PROXY_HOST, DOMAIN_INFRASTRUCTURE, "PROXY_HOST");
		TranslatableString description4 = new TranslatableString(OpenstackConstants.PROXY_HOST_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_HOST_DESCRIPTION");

		TranslatableString key5 = new TranslatableString(OpenstackConstants.PROXY_PORT, DOMAIN_INFRASTRUCTURE, "PROXY_PORT");
		TranslatableString description5 = new TranslatableString(OpenstackConstants.PROXY_PORT_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_PORT_DESCRIPTION");

		TranslatableString key6 = new TranslatableString(OpenstackConstants.PROXY_USER_NAME, DOMAIN_INFRASTRUCTURE, "PROXY_USER_NAME");
		TranslatableString description6 = new TranslatableString(OpenstackConstants.PROXY_USER_NAME_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_USER_NAME_DESCRIPTION");

		TranslatableString key7 = new TranslatableString(OpenstackConstants.PROXY_PASS, DOMAIN_INFRASTRUCTURE, "PROXY_PASS");
		TranslatableString description7 = new TranslatableString(OpenstackConstants.PROXY_PASS_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_PASS_DESCRIPTION");
		Volume v;
		//TENANT
		ConfigPropMetaData configMetaData1 = new ConfigPropMetaData(key1, ValueType.STRING, description1, true);
		configMetaDatas.add(configMetaData1);

		//REGION
		ConfigPropMetaData configMetaData2 = new ConfigPropMetaData(key2, ValueType.STRING, description2, true);
		configMetaDatas.add(configMetaData2);


		//proxy host
		ConfigPropMetaData configMetaData4 = new ConfigPropMetaData(key4, ValueType.STRING, description4, false, false, true);
		configMetaDatas.add(configMetaData4);

		//proxy port
		ConfigPropMetaData configMetaData5 = new ConfigPropMetaData(key5, ValueType.STRING, description5);
		configMetaDatas.add(configMetaData5);

		//proxy user
		ConfigPropMetaData configMetaData6 = new ConfigPropMetaData(key6, ValueType.STRING, description6);
		configMetaDatas.add(configMetaData6);

		//proxy pass
		ConfigPropMetaData configMetaData7 = new ConfigPropMetaData(key7, ValueType.STRING, description7, false, true);
		configMetaDatas.add(configMetaData7);

		adapterConfigData.setUrlRequired(ConfigRequirement.Required);
		adapterConfigData.setUserRequired(ConfigRequirement.Required);
		adapterConfigData.setPasswordRequired(ConfigRequirement.Required);
		adapterConfigData.setStorageManagerConfigPropMetaData(configMetaDatas);

		return adapterConfigData;
	}


	@Override
	public String getDescription() {

		return OpenstackConstants.OpenstackBlock_ADAPTER_DESCRIPTION;
	}

	@Override
	public String getFactoryId() {
		return this.getClass().getName();
	}

	@Override
	public String getProduct() {
		return OpenstackConstants.OpenstackBlock_ADAPTER_NAME;
	}

	@Override
	public String getVendor() {
		return OpenstackConstants.Openstack_VENDOR;
	}

	@Override
	public String getVersion() {
		return OpenstackConstants.Openstack_VERSION;
	}

	@Override
	public String testConnection(IStorageManagerAdapterConfig config)
	throws InfrastructAdapterException {

		Map<String, String> connectionProps = config.getStorageManagerAdditionalConfigProps();
		Map<String, String> secProps = config.getStorageManagerAdditionalSecConfigProps();
		try {
			OpenstackBlockCloudStorageController controller = new OpenstackBlockCloudStorageController(config.getLabel(),config.getUrl(),  connectionProps.get(OpenstackConstants.REGION),
					config.getUser(),config.getPassword(), connectionProps.get(OpenstackConstants.TENANT),connectionProps.get(OpenstackConstants.PROXY_HOST), 
					connectionProps.get(OpenstackConstants.PROXY_PORT), connectionProps.get(OpenstackConstants.PROXY_USER_NAME), 
					secProps.get(OpenstackConstants.PROXY_PASS));//,config.getLogger());

			controller.getRegions();
			controller.getRegions();
		} catch (CloudClientException e) {
			config.getLogger().traceThrowable(IJavaEeLog.SEVERITY_DEBUG, this.getClass().getName(), "testConnection:" + e.getMessage(), null,e);
			throw new InfrastructAdapterException(e.getMessage());
		}
		return null;
	}

	@Override
	public List<ExternalURL> getExternalUrls() {
		return null;
	}

}

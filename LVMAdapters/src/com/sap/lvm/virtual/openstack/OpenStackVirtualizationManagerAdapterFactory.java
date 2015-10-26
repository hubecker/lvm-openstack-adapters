package com.sap.lvm.virtual.openstack;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.sap.nw.lm.aci.engine.api.base.property.IPropertyType.ValueType;
import com.sap.nw.lm.aci.engine.base.api.i18n.TranslatableString;
import com.sap.tc.vcm.infrastructure.api.adapter.InfrastructAdapterException;
import com.sap.tc.vcm.infrastructure.api.adapter.IInfrastructAdapter.ExternalCustomTab;
import com.sap.tc.vcm.infrastructure.api.adapter.IInfrastructAdapter.ExternalURL;
import com.sap.tc.vcm.infrastructure.api.adapter.config.ConfigPropMetaData;
import com.sap.tc.vcm.infrastructure.api.adapter.config.IInfrastructAdapterConfigMetaData.ConfigRequirement;
import com.sap.tc.vcm.virtualization.adapter.api.IVirtualizationManagerAdapter;
import com.sap.tc.vcm.virtualization.adapter.api.IVirtualizationManagerAdapterFactory;
import com.sap.tc.vcm.virtualization.adapter.api.config.IVirtManagerAdapterConfig;
import com.sap.tc.vcm.virtualization.adapter.api.config.VirtManagerAdapterConfigMetaData;


public class OpenStackVirtualizationManagerAdapterFactory implements IVirtualizationManagerAdapterFactory {
static public String DOMAIN_INFRASTRUCTURE="InfrastructureTexts";
	@Override
	public IVirtualizationManagerAdapter createAdapterInstance(IVirtManagerAdapterConfig config) throws InfrastructAdapterException {
		try {
			return new OpenStackVirtualizationManagerAdapter(config);
		} catch (CloudClientException e) {
	
			throw new InfrastructAdapterException ("failed to create openstack client",e);
		}
	}

	@Override
	public VirtManagerAdapterConfigMetaData getAdapterConfigMetaData() {
		VirtManagerAdapterConfigMetaData adapterConfigData = new VirtManagerAdapterConfigMetaData();
	
		List<ConfigPropMetaData> configMetaDatas = new ArrayList<ConfigPropMetaData>();
		
		TranslatableString key1 = new TranslatableString(OpenStackConstants.TENANT, DOMAIN_INFRASTRUCTURE, "TENANT");
		TranslatableString description1 = new TranslatableString(OpenStackConstants.TENANT_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "TENANT_DESCRIPTION");
		
		TranslatableString key2 = new TranslatableString(OpenStackConstants.REGION, DOMAIN_INFRASTRUCTURE, "REGION");
		TranslatableString description2 = new TranslatableString(OpenStackConstants.REGION, DOMAIN_INFRASTRUCTURE, "REGION");
//		
//		TranslatableString key3 = new TranslatableString(OpenStackConstants.SECRET_KEY, DOMAIN_INFRASTRUCTURE, "SECRET_KEY");
//		TranslatableString description3 = new TranslatableString(OpenStackConstants.SECRET_KEY_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "SECRET_KEY_DESCRIPTION");
//		
		TranslatableString key4 = new TranslatableString(OpenStackConstants.PROXY_HOST, DOMAIN_INFRASTRUCTURE, "PROXY_HOST");
		TranslatableString description4 = new TranslatableString(OpenStackConstants.PROXY_HOST_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_HOST_DESCRIPTION");
		
		TranslatableString key5 = new TranslatableString(OpenStackConstants.PROXY_PORT, DOMAIN_INFRASTRUCTURE, "PROXY_PORT");
		TranslatableString description5 = new TranslatableString(OpenStackConstants.PROXY_PORT_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_PORT_DESCRIPTION");
		
//		TranslatableString key6 = new TranslatableString(OpenStackConstants.PROXY_USER_NAME, DOMAIN_INFRASTRUCTURE, "PROXY_USER_NAME");
//		TranslatableString description6 = new TranslatableString(OpenStackConstants.PROXY_USER_NAME_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_USER_NAME_DESCRIPTION");
//		
//		TranslatableString key7 = new TranslatableString(OpenStackConstants.PROXY_PASS, DOMAIN_INFRASTRUCTURE, "PROXY_PASS");
//		TranslatableString description7 = new TranslatableString(OpenStackConstants.PROXY_PASS_DESCRIPTION, DOMAIN_INFRASTRUCTURE, "PROXY_PASS_DESCRIPTION");
//		Volume v;
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
		
//		//proxy user
//		ConfigPropMetaData configMetaData6 = new ConfigPropMetaData(key6, ValueType.STRING, description6);
//		configMetaDatas.add(configMetaData6);
//		
//		//proxy pass
//		ConfigPropMetaData configMetaData7 = new ConfigPropMetaData(key7, ValueType.STRING, description7, false, true);
//		configMetaDatas.add(configMetaData7);

		adapterConfigData.setUrlRequired(ConfigRequirement.Required);
		adapterConfigData.setUserRequired(ConfigRequirement.Required);
		adapterConfigData.setPasswordRequired(ConfigRequirement.Required);
		adapterConfigData.setVirtManagerConfigPropMetaData(configMetaDatas);
		
		return adapterConfigData;
	}

	@Override
	public String getDescription() {
		return OpenStackConstants.OpenStack_CLOUD;
	}

	@Override
	public String getFactoryId() {
		return OpenStackConstants.OpenStack_FACTORY_ID;
	}

	@Override
	public String getProduct() {
		return OpenStackConstants.OpenStack_VIRTUALIZATION_ADAPTER;
	}

	@Override
	public String getVendor() {
		return OpenStackConstants.OpenStack_VENDOR;
	}

	@Override
	public String getVersion() {
		return OpenStackConstants.OpenStack_VERSION;
	}

	@Override
	public String testConnection(IVirtManagerAdapterConfig config) throws InfrastructAdapterException {
		Properties connectionProps = config.getVirtManagerAdditionalConfigProps();
		Properties secProps = config.getVirtManagerAdditionalSecConfigProps();
		
		try {
					OpenStack_CloudController controller = new OpenStack_CloudController(config.getUrl(),  connectionProps.getProperty(OpenStackConstants.REGION),config.getUser(),config.getPassword(), connectionProps.getProperty(OpenStackConstants.TENANT),connectionProps.getProperty(OpenStackConstants.PROXY_HOST), connectionProps.getProperty(OpenStackConstants.PROXY_PORT), connectionProps.getProperty(OpenStackConstants.PROXY_USER_NAME), secProps.getProperty(OpenStackConstants.PROXY_PASS));//,config.getLogger());

			controller.getRegions();
		} catch (CloudClientException e) {
			throw new InfrastructAdapterException("Failed to connect to OpenStack", e);
		}
		return null;
	}

	@Override
	public List<ExternalURL> getExternalUrls() {
		return null;
	}

	@Override
	public List<ExternalCustomTab> getExternalCustomTabs() {
		return null;
	}
	
}

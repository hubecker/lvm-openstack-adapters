
# lvm-openstack-adapters
Virtualization and storage adapters that enable users to manage their OpenStack clouds via LVM (Landscape &amp; Virtualization Management), including all LVM functions e.g. copy, clone, relocate, refresh, etc. While these adapters, provided by SAP, are tested with several configurations, everyone can customize them to work with their own environments.

## How is it done?

This OpenStack virtualization adapter and storage adapter that connects LVM to OpenStack works similarly to other LVM adapters, the main difference being that this one is open source with Apache License Version 2.0. We use Apache Maven (https://maven.apache.org/) to build the war files and package them into the deployable ear file. We used the OpenStack4j (http://openstack4j.com/) library to interact with the OpenStack server web services. These dependecies will be automatically downloaded from maven repository during the build process.  


##Building, installing and using the adapters


The two Openstack adapter projects, StorageManager  and Virtualization Manager compile to war files and are packaged into a single deployable ear file.




Here are the instructions for downloading, compiling and deploying the LVM Openstack Virtualization and Storage adapters. 

Github repo is https://github.com/SAP/lvm-openstack-adapters
It’s currently private so only members can see it. Send me your github username  (create one if you don’t have one!) and I will give you permissions.

You need git client installed locally to clone the project, I’ve tested with: git version 1.9.5.msysgit.1

Steps to build and deploy:

### Clone the project
```
 git clone https://github.com/SAP/lvm-openstack-adapters
```
###Build the project

You will need maven installed to package the ear file (tested with Apache Maven 3.0.5):

```
cd lvm-openstack-adapters
mvn clean package
```

Note: For maven build be sure to set your JAVA_HOME variable to a Java 1.6 jdk (tested with jdk1.6.0_45)

###Deploy project
You should now have an ear file in lvm-openstack-adapters\LVMAdaptersApp\target\LVMAdaptersApp-1.0.ear 

You can deploy this using eclipse: open the deploy view, select External Deployable Archives, find the ear file in the popup, right click and select deploy.

Alternatively you can:
Copy the Files you need to deploy to the LVM machine and then deploy it via Telnet: 

SSH to host and then execute:
```
Telnet localhost 50008 
Administrator
<password> 
jump 0
add deploy
deploy /<locationOnHost>/<yourFile>
```
Now you should see the Adapters in LVM. Procedure is the same As with any other adapter:
1.  navigate to Infrastructure->Virtualization Managers 
2.  select add 
3.  choose “Openstack” 
 
Now configure the adapter; again this process is similar to configuring other adapters (like AWS):
Fill in the form with Openstack connection details:
* OpenStack username 
* OpenStack password
* URL - e.g. http://<<Openstack hostname>>:5000/v2.0
* Region - e.g. RegionOne
* Tenant - also known as "Project" 
 
Now add the Openstack Storage Manager. Navigate to Infrastructure->Storage Managers and repeat the process above to add the Openstack adapter.

You should now see a list of Openstack VMs under Operations -> Virtualization and a list of volumes under Operations->Storage.


##Code structure in github repository

The top level contains the following directories:
StorageManagerOpenstack         :  contains java sources for the Openstack storage manager adapter and a pom.xml to build the war file
VirtualizationManagerOpenstack: contains java sources for the Openstack virtualization manager adapter and a pom.xml to build the war file
VirtualizationAPIs: contains 3 API jar files required by adapters above
LVMAdaptersApp: App project, contains a pom.xml that builds the deployable ear file containing the above war files and some SAP specific files                  


Note: To make modifications to the code in eclipse, first generate the eclipse files:
```
mvn  eclipse:eclipse
```

Then import the external projects under File->Import Existing projects into workspace and then navigate to the lvm-openstack-adapters directory


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

You will need maven installed to package the ear file (tested with [Apache Maven 3.0.5](http://archive.apache.org/dist/maven/maven-3/3.0.5/binaries/) (does not work with maven 2.x or 3.3.x):

```
cd lvm-openstack-adapters
mvn clean package
```

Note: For maven build be sure to set your JAVA_HOME variable to a Java 1.6 jdk (tested with jdk1.6.0_45)

###Deploy project
You should now have an ear file in lvm-openstack-adapters\LVMAdaptersApp\target\LVMAdaptersApp-1.0.ear 

You can deploy this using eclipse: open the deploy view, select External Deployable Archives, find the ear file in the popup, right click and select deploy.

The EAR file can de deployed either via the IDE, or using the Telnet commands.


In case you want to deploy it using Telnet, these are the steps you must follow:
  0. Copy the File you need to deploy to the LVM machine and then deploy it via Telnet: 
  1.  Open a Telnet connection to the AS Java on which you want to deploy the application. On Windows, you can do this from a DOS prompt with the command:
telnet localhost <port>,
where <port> is the Telnet port of your server. For example, if your server installation is c:\usr\sap\<some_three_letter_SID>\JCxx\..., then your Telnet port should be 5xx08.
  2.  Log on using your AS Java administrator user name and password.
  3.  When you have logged on, type the following Telnet commands:
> lsc

This will list the cluster elements. Find "Server 0", look the value in the "ID" column and type your next command:

> jump x

where x is this ID. Then type these commands:

> add deploy
> deploy <path to the location of the file >\LVMAdaptersApp-1.0.ear

For more information on telnet deploy see: http://help.sap.com/saphelp_banking50/helpdata/en/44/ee4a09d85a627de10000000a155369/content.htm


###Configure and Use
Now you should see the Adapters in LVM. Procedure is the same As with any other adapter:
1.  navigate to Infrastructure->Virtualization Managers 
2.  select add 
3.  choose “Openstack” 
 
Now configure the adapter; again this process is similar to configuring other adapters (like AWS):
Fill in the form with Openstack connection details:
* OpenStack username 
* OpenStack password
* URL - e.g. http://Openstack_hostname:5000/v2.0
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

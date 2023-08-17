Use Vagrant, and run Gemp in the Vagrant Virtual Machine, on Windows
====================================================================

## Introduction

This tutorial is designed to help Windows users get started using Vagrant, git, and provide them with a pathway to successfully running Gemp on their system.

## Gotchas:

⚠️ `git` must be configured to use the UNIX line endings that come from the git repository and NOT convert them in to Windows line endings. A Unix/Linux line ending is just a single newline character (`\n`) while a Windows line ending is a carriage return (`\r`) AND a newline (`\n`). As Unix/Linux does not expect the carriage return, it will consider the carriage return part of the text strings. ⚠️


⚠️ Vagrant connects to the Virtual Machine using SSH on port 2222. You must add a Firewall Rule to allow TCP port 2222 to the Virtual Machine created by Vagrant. ⚠️


⚠️ The Gemp service runs on HTTP port 80. You must add a Firewall Rule to allow TCP port 80 to the Virtual Machine created by Vagrant. ⚠️



## Install Dependencies

1. Install [**Git for Windows**](https://git-scm.com/downloads), also known as GitBash. When installing, be sure that you use the **Unix** line endings, _NOT_ the Windows line endings.
2. Install [**VirtualBox**](https://www.virtualbox.org/)
3. Install [**Vagrant**](https://www.vagrantup.com/)

### 1. [Download and Install Git for Windows](https://git-scm.com/downloads)

#### Download Git
![Download Git](vagrant_windows/download_git.png)

#### GPL
![GPL](vagrant_windows/git1.png)

#### Installation Path
![Installation Path](vagrant_windows/git2.png)

#### Components to install
![Components to install](vagrant_windows/git3.png)

#### Default git editor
![Default git editor](vagrant_windows/git4.png)

#### Default Git branch name
![Default Git branch name](vagrant_windows/git5.png)

#### PATH environment variable
![PATH environment variable](vagrant_windows/git6.png)

#### Use Bundled OpenSSH
![Use Bundled OpenSSH](vagrant_windows/git7.png)

#### Use the OpenSSL Library
![Use the OpenSSL Library](vagrant_windows/git8.png)

#### ⚠️ Unix Line Endings ⚠️

Ensure that the line endings are UNIX line endings or you will have a bad time.

![⚠️ Unix Line Endings ⚠️](vagrant_windows/git9.png)

#### Terminal Emulator
![Terminal Emulator](vagrant_windows/git10.png)

#### git pull default behavior
![git pull default behavior](vagrant_windows/git11.png)

#### Credentials Helper
![Credentials Helper](vagrant_windows/git12.png)

#### Extra Options
![Extra Options](vagrant_windows/git13.png)

#### Experimental Options
![Experimental Options](vagrant_windows/git14.png)


#### Git Installed
![Git Installed](vagrant_windows/git_setup_complete.png)


### 2. [Download and Install VirtualBox for Windows and the VirtualBox Extension Pack](https://www.virtualbox.org/)

#### Download VirtualBox
![Download VirtualBox](vagrant_windows/virtualbox1.png)

#### Download Extensions Pack
![Download Extensions Pack](vagrant_windows/virtualbox2.png)

#### Install the Defaults
![Install the Defaults](vagrant_windows/virtualbox3.png)

#### Installation Complete
![Installation Complete](vagrant_windows/virtualbox4.png)

#### Extension Pack Manager
![Extension Pack Manager](vagrant_windows/virtualbox5.png)

#### Tools Install
![Tools Install](vagrant_windows/virtualbox6.png)

#### Install the Oracle VirtualBox Extensions
![Install the Oracle VirtualBox Extensions](vagrant_windows/virtualbox7.png)

#### Oracle VirtualBox Extensions Installed
![Oracle VirtualBox Extensions Installed](vagrant_windows/virtualbox8.png)



### 3. [Download and Install Vagrant](https://www.vagrantup.com/)

#### Download Vagrant

![Download Vagrant](vagrant_windows/download_vagrant.png)

#### Install Vagrant

![Install Vagrant](vagrant_windows/vagrant1.png)

![Vagrant Installed](vagrant_windows/vagrant2.png)



## Clone the Gemp git repo

Open **Powershell** and clone the git repo:

```bash
git clone https://github.com/PlayersCommittee/gemp-swccg-public.git
cd gemp-swccg-public
```

![Clone the Git Repo](vagrant_windows/git_clone.png)


## Create Vagrant Virtual Machine

Vagrant is a utility for creating VirtualBox powered Virtual Machines. An Ubuntu 20.04 LTS (focal) Virtual Machine will be created by Vagrant. The `Vagrantfile` defines how the virtual machine will take shape. There is a provisioning script, `vagrant-bootstrap/bootstrap.sh`, that gets run when the virtual machine is created. The bootstrap script will install all of the dependencies, including the correct version of Java, Maven, and MariaDB. Using Vagrant allows us to create a development environment that matches the production hosting environment.

```bash
vagrant up
vagrant status
```

![Start the Vagrant VM](vagrant_windows/vagrant_up_success.png)


## Connect to the Vagrant Virtual Machine using SSH

```bash
vagrant status
vagrant ssh
```

![SSH to the Vagrant VM](vagrant_windows/vagrant_ssh.png)

## After connecting, compile Gemp and Run Gemp

You need to be on the Gemp virtual machine. If you did not already SSH to the VM, connect using the `vagrant ssh` command.

### Compile Gemp using Maven

```bash
mvn clean install
```

![Compile Gemp using Maven](vagrant_windows/mvn_clean_install.png)

### Run Gemp

```bash
./run-gemp.sh
```

![Run Gemp](vagrant_windows/run-gemp_sh.png)

### Connect to Gemp using the web browser from the host machine

Use your web browser of choice on the host machine. Treat the Vagrant Virtual Machine like a remote system that you are connecting to.

![Connect to Gemp using your web browser of choice](vagrant_windows/welcome_to-gemp-swccg.png)



## Stopping the Virtual Machine

You can shutdown the Virtual Machine and keep it as-is for future use

```bash
vagrant stop
```

## Destroying the Virtual Machine

Destroying the Virtual Machine will stop it and delete it.
The next time you want to run gemp, you will need to recreate the Virtual Machine.

```bash
vagrant destroy
```
















## Add Windows Firewall Rules

* Vagrant connects to the Virtual Machine using SSH on port 2222. You must add a Firewall Rule to allow TCP port 2222 to the Virtual Machine created by Vagrant.

* The Gemp service runs on HTTP port 80. You must add a Firewall Rule to allow TCP port 80 to the Virtual Machine created by Vagrant.


### Launch the Windows Firewall Control Panel

![](vagrant_windows/windows_firewall.png)

### Set the interfaces protected by the Public Firewall

![](vagrant_windows/public_profile_windows_defender_firewall_properties.png)


### Go in to the Advanced Firewall settings and create new rules

![](vagrant_windows/firewall_advanced_settings.png)

![Advanced, Public Profile, Customize](vagrant_windows/public_profile.png)

![Set the public networks to be "Local Area Connection" and Wifi](vagrant_windows/protected_network_connections_for_public.png)


### Go to the Inbound Rules and create new rules

Create new rules for TCP80, TCP8080, TCP22, and TCP2222.

![Create new Inbound Rules](vagrant_windows/inbound_rules.png)



#### TCP 80

![2 - Protocols and Ports ](vagrant_windows/new_inbound_rule_port.png)
![2 - Specific Port 80](vagrant_windows/specific_port_80.png)
![3 - Action allow connection](vagrant_windows/allow_the_connection.png)
![4 - Domain and Private networks](vagrant_windows/domain_private.png)
![5 - Name http80 ](vagrant_windows/name_http80.png)

*********

#### TCP 8080

![2 - Protocols and Ports ](vagrant_windows/new_inbound_rule_port.png)
![2 - Specific Port 8080](vagrant_windows/specific_port_8080.png)
![3 - Action allow connection](vagrant_windows/allow_the_connection.png)
![4 - Domain and Private networks](vagrant_windows/domain_private.png)
![5 - Name htp8080](vagrant_windows/new_inbound_rule_http8080.png)

*********

#### TCP 22

![2 - Protocols and Ports ](vagrant_windows/new_inbound_rule_port.png)
![3 - Action allow connection](vagrant_windows/allow_the_connection.png)
![4 - Domain and Private networks](vagrant_windows/domain_private.png)
![5 - Name vagrantSsh22](vagrant_windows/vagrant_ssh_22.png)

*********

#### TCP 2222

![2 - Protocols and Ports ](vagrant_windows/new_inbound_rule_port.png)
![2 - Specific Port 2222](vagrant_windows/specific_port_2222.png)
![3 - Action allow connection](vagrant_windows/allow_the_connection.png)
![4 - Domain and Private networks](vagrant_windows/domain_private.png)

*********

## Troubleshooting

### `vagrant up` times out

![](vagrant_windows/vagrant_up_timeout.png)

Add TCP ports 2222 and 80 to your Windows Firewall on the non-public networks:

![](vagrant_windows/domain_private.png)







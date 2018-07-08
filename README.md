# gemp-swccg
GEMP-SWCCG - server/client for playing Star Wars CCG using a web browser. The program takes care of the rules so you don't have to.


## Running a Dev VM with Vagrant

Vagrant is a tool for automating the creation of virtual machines. This allows us to mimic the server environment more closely and have a single set of commands to run the program, regardless of what OS is used for development. Your dev machine can be used to code and compile in whatever IDE you like. The code is automatically shared with the VM, which is used to run it. The virtual machine is also capable of compiling the source using maven.

### Setup

Install vagrant (https://www.vagrantup.com/downloads.html) and virtualbox (https://www.virtualbox.org/wiki/Downloads) for your OS.

From the command line run:
```
vagrant plugin install vagrant-vbguest
```

This plugin keeps the host and guest OS in sync and allows directories to be seamlessly shared.


### Spinning up the VM

The following command will spin up the virtual machine, install all dependencies needed for running and compiling GEMP, and create the database if it does not already exist. From the `gemp-swccg` repository root directory, run the following command:
```
vagrant up
```

### Logging into the VM

Log into the VM by using the following command from the repository root:
```
vagrant ssh
```

### Compiling

The VM can use pre-compiled .jar and .zip files if they are located in the `gemp-swccg-async/target/` directory. However, you can also compile on the VM directly.

After running `vagrant ssh`, run the following command ON THE VM:
```
mvn clean install
```

### Running GEMP

From the SSH login direcory on the VM (`/vagrant`) run:
```
./run-gemp.sh
```

You can now access GEMP by visiting `http://192.168.50.94/gemp-swccg/` in your browser.


### Other VM Commands

* `vagrant halt`: Hard shutdown of the VM
* `vagrant suspend`: Soft shutdown of the VM (this is faster)
* `vagrant destroy`: Remove VM from your system

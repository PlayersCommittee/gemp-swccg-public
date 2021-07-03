# Vagrantfile for GEMP SWCCG development box
# -*- mode: ruby -*-
# vi: set ft=ruby :

require 'fileutils'

PROVISION_SCRIPT = File.join(File.dirname(__FILE__), "vagrant-build/bootstrap.sh")
VAGRANTFILE_API_VERSION = "2"
VAGRANT_INSTANCE_NAME   = "gemp"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.hostname = VAGRANT_INSTANCE_NAME

  config.vm.define VAGRANT_INSTANCE_NAME do |d|
    # Ensure vbguest is updated and running on host.
    if Vagrant.has_plugin?("vagrant-vbguest")
      config.vbguest.auto_update = false
    end

    # Ubuntu 20.04 LTS
    d.vm.box = "ubuntu/focal64"

    # Create a private network, which allows host-only 
    # access to the machine using a specific IP.
    d.vm.network "private_network", ip: "192.168.50.94"

    config.vm.provider "virtualbox" do |vb|
      vb.name = VAGRANT_INSTANCE_NAME

      # Customize the amount of memory on the VM:
      vb.memory = "2048"
    end


    if File.exist?(PROVISION_SCRIPT)
      d.vm.provision :file,  :source => "#{PROVISION_SCRIPT}", :destination => "/tmp/provision.sh"
      d.vm.provision :shell, :inline => "chmod 0755 /tmp/provision.sh ; /tmp/provision.sh", :privileged => true
    end ### provision

  end
end

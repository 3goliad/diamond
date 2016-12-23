# -*- mode: ruby -*-
# vi: set ft=ruby :
Vagrant.configure("2") do |config|
  config.ssh.insert_key = false

  config.vm.box = "bugyt/archlinux"
  config.vm.synced_folder ".", "/vagrant", type: "sshfs"
  config.vm.network "forwarded_port", guest: 80, host: 8080
  config.vm.provider "libvirt" do |libvirt|
  end
  config.vm.provision :salt do |salt|
	  salt.masterless = true
	  salt.minion_config = "salt/minion"
	  salt.run_highstate = true
	  salt.install_master = false
	  salt.install_type = "stable"
	  salt.colorize = "true"
  end

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   apt-get update
  #   apt-get install -y apache2
  # SHELL
end

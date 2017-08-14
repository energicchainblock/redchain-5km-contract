package com.utsoft.chain.km5.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {
	
	private String servers;

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}
}

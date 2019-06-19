package me.retrodaredevil.couchdb;

class ImmutableCouchProperties implements CouchProperties {
	private final String database;
	private final boolean createIfNotExist;
	private final String protocol, host, path;
	private final int port;
	private final String username, password;
	private final int socketTimeout, connectionTimeout, maxConnections;
	private final String proxyHost;
	private final int proxyPort;
	
	ImmutableCouchProperties(String database, boolean createIfNotExist, String protocol, String host, String path, int port, String username, String password, int socketTimeout, int connectionTimeout, int maxConnections, String proxyHost, int proxyPort) {
		this.database = database;
		this.createIfNotExist = createIfNotExist;
		this.protocol = protocol;
		this.host = host;
		this.path = path;
		this.port = port;
		this.username = username;
		this.password = password;
		this.socketTimeout = socketTimeout;
		this.connectionTimeout = connectionTimeout;
		this.maxConnections = maxConnections;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	
	@Override public String getDatabase() { return database; }
	
	@Override public boolean isCreateIfNotExist() { return createIfNotExist; }
	
	@Override public String getProtocol() { return protocol; }
	
	@Override public String getHost() { return host; }
	
	@Override public String getPath() { return path; }
	
	@Override public int getPort() { return port; }
	
	@Override public String getUsername() { return username; }
	
	@Override public String getPassword() { return password; }
	
	@Override public int getSocketTimeout() { return socketTimeout; }
	
	@Override public int getConnectionTimeout() { return connectionTimeout; }
	
	@Override public int getMaxConnections() { return maxConnections; }
	
	@Override public String getProxyHost() { return proxyHost; }
	
	@Override public int getProxyPort() { return proxyPort; }
}

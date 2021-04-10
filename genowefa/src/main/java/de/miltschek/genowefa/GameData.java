package de.miltschek.genowefa;

/**
 * Describes a game.
 */
public class GameData {
	private final String address;
	private final int port;
	private String serverName;
	private String mapName;
	private int generationSeed;
	private int startingYear;
	private int mapSizeX;
	private int mapSizeY;
	
	private long startedTs;
	private long finishedTs;

	/**
	 * Creates a game descriptor.
	 * @param address network address of the server (e.g. IP or FQDN)
	 * @param port port number of the server (1..65535)
	 * @param serverName name of the server
	 * @param mapName name of the map
	 * @param generationSeed map's generation seed
	 * @param startingYear starting year
	 * @param mapSizeX size of the map (width)
	 * @param mapSizeY size of the map (height)
	 */
	public GameData(String address,
			int port,
			String serverName,
			String mapName,
			int generationSeed,
			int startingYear,
			int mapSizeX,
			int mapSizeY) {
		this.address = address;
		this.port = port;
		this.serverName = serverName;
		this.mapName = mapName;
		this.generationSeed = generationSeed;
		this.startingYear = startingYear;
		this.mapSizeX = mapSizeX;
		this.mapSizeY = mapSizeY;
	}
	
	/**
	 * Gets the network address of the server.
	 * @return network address of the server
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Gets the port number of the server.
	 * @return port number of the server
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Gets the server's name.
	 * @return serves's name
	 */
	public String getServerName() {
		return serverName;
	}
	
	/**
	 * Gets the map's name.
	 * @return map's name
	 */
	public String getMapName() {
		return mapName;
	}
	
	/**
	 * Gets the map's generation seed.
	 * @return map's generations seed
	 */
	public int getGenerationSeed() {
		return generationSeed;
	}
	
	/**
	 * Gets the starting year.
	 * @return starting year
	 */
	public int getStartingYear() {
		return startingYear;
	}
	
	/**
	 * Gets the size of the map (width).
	 * @return size of the map (width)
	 */
	public int getMapSizeX() {
		return mapSizeX;
	}
	
	/**
	 * Gets the size of the map (height).
	 * @return size of the map (height)
	 */
	public int getMapSizeY() {
		return mapSizeY;
	}
	
	/**
	 * Returns a real-time timestamp of when the game is considered to be started.
	 * @return a timestamp (milliseconds since Jan, 1st 1970).
	 */
	public long getStartedTs() {
		return startedTs;
	}
	
	/**
	 * Sets a real-time timestamp of when the game is considered to be started.
	 * @param startedTs a timestamp (milliseconds since Jan, 1st 1970).
	 */
	public void setStartedTs(long startedTs) {
		this.startedTs = startedTs;
	}
	
	/**
	 * Returns a real-time timestamp of when the game is considered to be finished.
	 * @return a timestamp (milliseconds since Jan, 1st 1970).
	 */
	public long getFinishedTs() {
		return finishedTs;
	}
	
	/**
	 * Sets a real-time timestamp of when the game is considered to be finished.
	 * @param finishedTs a timestamp (milliseconds since Jan, 1st 1970).
	 */
	public void setFinishedTs(long finishedTs) {
		this.finishedTs = finishedTs;
	}
}

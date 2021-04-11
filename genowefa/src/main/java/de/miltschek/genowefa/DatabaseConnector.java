package de.miltschek.genowefa;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.miltschek.openttdadmin.data.ClosureReason;
import de.miltschek.openttdadmin.data.CompanyEconomy;
import de.miltschek.openttdadmin.data.CompanyStatistics;
import de.miltschek.openttdadmin.data.Date;

public class DatabaseConnector implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnector.class);
	
	private static final String F_ID = "`id`",
			F_STARTED = "`started`",
			F_FINISHED = "`finished`",
			F_SERVER_NAME = "`server_name`",
			F_MAP_NAME = "`map_name`",
			F_GENERATION_SEED = "`generation_seed`",
			F_STARTING_YEAR = "`starting_year`",
			F_MAP_SIZE_X = "`map_size_x`",
			F_MAP_SIZE_Y = "`map_size_y`",
			F_GAME_ID = "`game_id`",
			F_FOUNDED = "`founded`",
			F_CLOSED = "`closed`",
			F_CLOSURE_REASON = "`closure_reason`",
			F_COLOR = "`color`",
			F_NAME = "`name`",
			F_MANAGER_NAME = "`manager_name`",
			F_PASSWORD_PROTECTED = "`password_protected`",
			F_TS = "`ts`",
			F_INCOME = "`income`",
			F_LOAN = "`loan`",
			F_MONEY = "`money`",
			F_VALUE = "`value`",
			F_PERFORMANCE = "`performance`",
			F_NTRAINS = "`num_trains`",
			F_NLORRIES = "`num_lorries`",
			F_NBUSSES = "`num_busses`",
			F_NPLANES = "`num_planes`",
			F_NSHIPS = "`num_ships`",
			F_NSTATIONS = "`num_stations`",
			F_NDEPOTS = "`num_depots`",
			F_NSTOPS = "`num_stops`",
			F_NAIRPORTS = "`num_airports`",
			F_NHARBOURS = "`num_harbours`",
			F_COMPANY_ID = "`company_id`",
			F_CLIENT_ID = "`client_id`",
			F_IP = "`ip`",
			F_COUNTRY = "`country`",
			F_CITY = "`city`",
			F_PROXY = "`proxy`",
			F_LEFT_TS = "`left_ts`",
			F_ADDRESS = "`address`",
			F_PORT = "`port`";
	
	private static final String TABLE_GAMES = "`genowefa_games`";
	private static final String CREATE_GAMES = "CREATE TABLE " + TABLE_GAMES + " (" + 
			F_ID + " BIGINT unsigned NOT NULL AUTO_INCREMENT," +
			F_ADDRESS + " VARCHAR(255) NOT NULL," +
			F_PORT + " INT NOT NULL," +
			F_STARTED + " DATETIME," +
			F_FINISHED + " DATETIME," +
			F_SERVER_NAME + " VARCHAR(255)," +
			F_MAP_NAME + " VARCHAR(255)," + 
			F_GENERATION_SEED + " INT," + 
			F_STARTING_YEAR + " INT," + 
			F_MAP_SIZE_X + " INT," + 
			F_MAP_SIZE_Y + " INT," + 
			"PRIMARY KEY (" + F_ID + ")" + 
			");";
	
	private static final String TABLE_COMPANIES = "`genowefa_companies`";
	private static final String CREATE_COMPANIES = "CREATE TABLE " + TABLE_COMPANIES + " (" + 
			F_ID + " BIGINT unsigned NOT NULL AUTO_INCREMENT," + 
			F_GAME_ID + " BIGINT unsigned NOT NULL," + 
			F_COMPANY_ID + " INT NOT NULL," + 
			F_FOUNDED + " INT," + 
			F_CLOSED + " DATE," + 
			F_CLOSURE_REASON + " VARCHAR(255)," + 
			F_COLOR + " INT," +
			F_NAME + " VARCHAR(255)," + 
			F_MANAGER_NAME + " VARCHAR(255)," + 
			F_PASSWORD_PROTECTED + " BOOLEAN," + 
			"PRIMARY KEY (" + F_ID + ")," +
			"CONSTRAINT `fk_company_game`\r\n" + 
			"FOREIGN KEY (" + F_GAME_ID + ") REFERENCES " + TABLE_GAMES + " (" + F_ID + ")" + 
			" ON DELETE CASCADE\r\n" + 
			" ON UPDATE CASCADE" +
			");";
	
	private static final String TABLE_ECONOMY = "`genowefa_economy`";
	private static final String CREATE_ECONOMY = "CREATE TABLE " + TABLE_ECONOMY + " (" + 
			F_COMPANY_ID + " BIGINT unsigned NOT NULL," + 
			F_TS + " DATETIME NOT NULL," + 
			F_INCOME + " BIGINT NOT NULL," + 
			F_LOAN + " BIGINT NOT NULL," + 
			F_MONEY + " BIGINT NOT NULL," + 
			F_VALUE + " BIGINT NOT NULL," + 
			F_PERFORMANCE + " INT NOT NULL," + 
			"KEY `economy_index` (" + F_COMPANY_ID + ") USING BTREE," + 
			"CONSTRAINT `fk_economy_company`\r\n" + 
			"FOREIGN KEY (" + F_COMPANY_ID + ") REFERENCES " + TABLE_COMPANIES + " (" + F_ID + ")" + 
			" ON DELETE CASCADE\r\n" + 
			" ON UPDATE CASCADE" +
			");";
	
	private static final String TABLE_STATISTICS = "`genowefa_statistics`";
	private static final String CREATE_STATISTICS = "CREATE TABLE " + TABLE_STATISTICS + " (" + 
			F_COMPANY_ID + " BIGINT unsigned NOT NULL," + 
			F_TS + " DATETIME NOT NULL," + 
			F_NTRAINS + " INT NOT NULL," + 
			F_NLORRIES + " INT NOT NULL," + 
			F_NBUSSES + " INT NOT NULL," + 
			F_NPLANES + " INT NOT NULL," + 
			F_NSHIPS + " INT NOT NULL," + 
			F_NSTATIONS + " INT NOT NULL," + 
			F_NDEPOTS + " INT NOT NULL," + 
			F_NSTOPS + " INT NOT NULL," + 
			F_NAIRPORTS + " INT NOT NULL," + 
			F_NHARBOURS + " INT NOT NULL," + 
			"KEY `statistics_index` (" + F_COMPANY_ID + ") USING BTREE," + 
			"CONSTRAINT `fk_stat_company`\r\n" + 
			"FOREIGN KEY (" + F_COMPANY_ID + ") REFERENCES " + TABLE_COMPANIES + " (" + F_ID + ")" + 
			" ON DELETE CASCADE\r\n" + 
			" ON UPDATE CASCADE" +
			");";
	
	private static final String TABLE_CLIENTS = "`genowefa_clients`";
	private static final String CREATE_CLIENTS = "CREATE TABLE " + TABLE_CLIENTS + " (" + 
			F_GAME_ID + " BIGINT unsigned NOT NULL," +
			F_CLIENT_ID + " INT NOT NULL," +
			F_NAME + " VARCHAR(255)," +
			F_IP + " VARCHAR(255)," +
			F_COUNTRY + " VARCHAR(255)," +
			F_CITY + " VARCHAR(255)," +
			F_PROXY + " BOOLEAN," +
			"PRIMARY KEY (" + F_GAME_ID + ", " + F_CLIENT_ID + ")" +
			");";

	private static final String TABLE_PLAYERS = "`genowefa_players`";
	private static final String CREATE_PLAYERS = "CREATE TABLE " + TABLE_PLAYERS + " (" +
			F_TS + " DATETIME NOT NULL," +
			F_GAME_ID + " BIGINT unsigned NOT NULL," +
			F_CLIENT_ID + " INT NOT NULL," +
			F_COMPANY_ID + " BIGINT unsigned NOT NULL," +
			F_LEFT_TS + " DATETIME," +
			"KEY `player_index` (" + F_GAME_ID + ", " + F_CLIENT_ID + ", " + F_COMPANY_ID + ") USING BTREE," + 
			"CONSTRAINT `fk_player_client`\r\n" + 
			"FOREIGN KEY (" + F_GAME_ID + ", " + F_CLIENT_ID + ") REFERENCES " + TABLE_CLIENTS + " (" + F_GAME_ID + ", " + F_CLIENT_ID + ")" + 
			" ON DELETE CASCADE\r\n" + 
			" ON UPDATE CASCADE," +
			"CONSTRAINT `fk_player_company`\r\n" + 
			"FOREIGN KEY (" + F_COMPANY_ID + ") REFERENCES " + TABLE_COMPANIES + " (" + F_ID + ")" + 
			" ON DELETE CASCADE\r\n" + 
			" ON UPDATE CASCADE" +
			");";

	private Connection connection;

	public DatabaseConnector(Configuration.Database config) throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:"
				+ config.getUrl()
				+ (config.getUrl().endsWith("/") ? "" : "/")
				+ config.getDbName(),
			config.getUsername(),
			config.getPassword());
		
		createTables(config.isDropTables());
	}
	
	private void createTables(boolean dropTables) throws SQLException {
		Statement statement = connection.createStatement();
		
		if (dropTables) {
			LOGGER.warn("Dropping tables on startup.");
			
			try {
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_ECONOMY);
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_COMPANIES);
				statement.executeUpdate("DROP TABLE IF EXISTS " + TABLE_GAMES);
			} catch (SQLException ex) {
				LOGGER.error("Failed to drop table(s).", ex);
				return;
			}
		}
		
		ResultSet resultSet;
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_GAMES + " WHERE " + F_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 11) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_GAMES, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_GAMES) == 0) {
				LOGGER.info("Created table {}.", TABLE_GAMES);
			}
		}
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_COMPANIES + " WHERE " + F_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 10) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_COMPANIES, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_COMPANIES) == 0) {
				LOGGER.info("Created table {}.", TABLE_COMPANIES);
			}
		}
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_ECONOMY + " WHERE " + F_COMPANY_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 7) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_ECONOMY, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_ECONOMY) == 0) {
				LOGGER.info("Created table {}.", TABLE_ECONOMY);
			}
		}
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_STATISTICS + " WHERE " + F_COMPANY_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 12) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_STATISTICS, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_STATISTICS) == 0) {
				LOGGER.info("Created table {}.", TABLE_STATISTICS);
			}
		}
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_CLIENTS + " WHERE " + F_GAME_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 7) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_CLIENTS, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_CLIENTS) == 0) {
				LOGGER.info("Created table {}.", TABLE_CLIENTS);
			}
		}
		
		try {
			resultSet = statement.executeQuery("SELECT * FROM " + TABLE_PLAYERS + " WHERE " + F_GAME_ID + " = 0");
			if (resultSet.getMetaData().getColumnCount() != 5) {
				LOGGER.warn("Table {} contains an unexpected number of columns {}.", TABLE_PLAYERS, resultSet.getMetaData().getColumnCount());
			}
			resultSet.close();
		} catch (SQLException ex) {
			if (statement.executeUpdate(CREATE_PLAYERS) == 0) {
				LOGGER.info("Created table {}.", TABLE_PLAYERS);
			}
		}
	}
	
	public Map<Long, GameData> getGames(boolean activeOnly) {
		Statement statement = null;
		try {
			HashMap<Long, GameData> result = new HashMap<>();

			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(
					"SELECT " + F_ID + ", " // 1
							+ F_ADDRESS + ", " // 2
							+ F_PORT + ", " // 3
							+ F_SERVER_NAME + ", " // 4
							+ F_MAP_NAME + ", " // 5
							+ F_GENERATION_SEED + ", " // 6
							+ F_STARTING_YEAR + ", " // 7
							+ F_MAP_SIZE_X + ", " // 8
							+ F_MAP_SIZE_Y + ", " // 9
							+ F_STARTED + ", " // 10
							+ F_FINISHED // 11
							+ " FROM " + TABLE_GAMES
							+ (activeOnly ? " WHERE " + F_FINISHED + " IS NULL" : ""));

			while (resultSet.next()) {
				int n = 1;
				long id = resultSet.getLong(n++);
				GameData gameData = new GameData(
						resultSet.getString(n++),
						resultSet.getInt(n++),
						resultSet.getString(n++),
						resultSet.getString(n++),
						resultSet.getInt(n++),
						resultSet.getInt(n++), // startingYear
						resultSet.getInt(n++),
						resultSet.getInt(n++));
				
				Timestamp ts = resultSet.getTimestamp(n++);
				gameData.setStartedTs(ts == null ? 0 : ts.getTime());
				ts = resultSet.getTimestamp(n++);
				gameData.setFinishedTs(ts == null ? 0 : ts.getTime());
				
				result.put(id, gameData);
			}
		
			return result;
		} catch (SQLException ex) {
			LOGGER.error("Failed to get a list of games, active only {}.", activeOnly, ex);
			return null;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean updateGame(long gameId, GameData gameData) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"UPDATE " + TABLE_GAMES + " SET "
							+ F_ADDRESS + " = ?, "
							+ F_PORT + " = ?, "
							+ F_SERVER_NAME + " = ?, "
							+ F_MAP_NAME + " = ?, "
							+ F_GENERATION_SEED + " = ?, "
							+ F_STARTING_YEAR + " = ?, "
							+ F_MAP_SIZE_X + " = ?, "
							+ F_MAP_SIZE_Y + " = ?"
								+ " WHERE " + F_ID + " = ?");
			
			int n = 1;
			statement.setString(n++, gameData.getAddress());
			statement.setInt(n++, gameData.getPort());
			statement.setString(n++, gameData.getServerName());
			statement.setString(n++, gameData.getMapName());
			statement.setInt(n++, gameData.getGenerationSeed());
			statement.setInt(n++, gameData.getStartingYear());
			statement.setInt(n++, gameData.getMapSizeX());
			statement.setInt(n++, gameData.getMapSizeY());
			
			statement.setLong(n++, gameId);
			
			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to update the game {}.", gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}	
	}
	
	public long createNewGame(GameData gameData) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"INSERT INTO " + TABLE_GAMES + " ("
							+ F_ADDRESS + ", "
							+ F_PORT + ", "
							+ F_STARTED + ", "
							+ F_SERVER_NAME + ", "
							+ F_MAP_NAME + ", "
							+ F_GENERATION_SEED + ", "
							+ F_STARTING_YEAR + ", "
							+ F_MAP_SIZE_X + ", "
							+ F_MAP_SIZE_Y + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			int n = 1;
			statement.setString(n++, gameData.getAddress());
			statement.setInt(n++, gameData.getPort());
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			statement.setString(n++, gameData.getServerName());
			statement.setString(n++, gameData.getMapName());
			statement.setInt(n++, gameData.getGenerationSeed());
			statement.setInt(n++, gameData.getStartingYear());
			statement.setInt(n++, gameData.getMapSizeX());
			statement.setInt(n++, gameData.getMapSizeY());

			statement.executeQuery();
			
			ResultSet key;
			if ((key = statement.getGeneratedKeys()).next()) {
				return key.getLong(1);
			} else {
				return -2;
			}
		} catch (SQLException ex) {
			LOGGER.error("Failed to create a new game for server {}:{}, name {}.", gameData.getAddress(), gameData.getPort(), gameData.getServerName(), ex);
			return -1;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean closeGame(long gameId) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"UPDATE " + TABLE_GAMES + " SET " + F_FINISHED + " = ? WHERE " + F_ID + " = ?");
			
			int n = 1;
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			statement.setLong(n++, gameId);

			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to close a game {}.", gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public long createOrUpdateCompany(long gameId, CompanyData companyData) {
		long companyId = 0;
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"SELECT " + F_ID + " FROM " + TABLE_COMPANIES + " WHERE " + F_GAME_ID + " = ? AND " + F_COMPANY_ID + " = ? AND " + F_CLOSED + " IS NULL"); 
			
			int n = 1;
			statement.setLong(n++, gameId);
			statement.setInt(n++, companyData.getCompanyId());

			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				companyId = resultSet.getLong(1);
			}
			
			resultSet.close();
		} catch (SQLException ex) {
			LOGGER.error("Failed to get the company {} of the game {}.", companyData.getCompanyId(), gameId, ex);
			return -1;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
		
		try {
			if (companyId == 0) {
				statement = connection.prepareStatement(
						"INSERT INTO " + TABLE_COMPANIES + " ("
								+ F_GAME_ID + ", "
								+ F_COMPANY_ID + ", "
								+ (companyData.getInauguratedYear() == 0 ? "" : (F_FOUNDED + ", "))
								+ F_COLOR + ", "
								+ F_NAME + ", "
								+ F_MANAGER_NAME + ", "
								+ F_PASSWORD_PROTECTED + ") VALUES (?, ?, "
								+ (companyData.getInauguratedYear() == 0 ? "" : "?, ")
								+ "?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
			} else {
				statement = connection.prepareStatement(
						"UPDATE " + TABLE_COMPANIES + " SET "
								+ (companyData.getInauguratedYear() == 0 ? "" : (F_FOUNDED + " = ?, "))
								+ F_COLOR + " = ?, "
								+ F_NAME + " = ?, "
								+ F_MANAGER_NAME + " = ?, "
								+ F_PASSWORD_PROTECTED + " = ? WHERE "
								+ F_ID + " = ?");
			}
			
			int n = 1;
			if (companyId == 0) {
				statement.setLong(n++, gameId);
				statement.setInt(n++, companyData.getCompanyId());
			}
			
			if (companyData.getInauguratedYear() != 0) {
				statement.setInt(n++, companyData.getInauguratedYear());
			}
			
			statement.setInt(n++, companyData.getColor() == null ? -1 : companyData.getColor().getValue());
			statement.setString(n++, companyData.getName());
			statement.setString(n++, companyData.getManagerName());
			statement.setBoolean(n++, companyData.isPasswordProtected());
			
			if (companyId != 0) {
				statement.setLong(n++, companyId);
			}

			if (companyId == 0) {
				statement.executeQuery();
				ResultSet key;
				if ((key = statement.getGeneratedKeys()).next()) {
					companyId = key.getLong(1);
				} else {
					return -2;
				}
			} else {
				if (statement.executeUpdate() != 1) {
					return -2;
				}
			}
			
			return companyId;
		} catch (SQLException ex) {
			LOGGER.error("Failed to create/update a company {} of the game {}.", companyData.getCompanyId(), gameId, ex);
			return -3;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean closeCompany(long gameId, byte companyId, Date closureDate, ClosureReason closureReason) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"UPDATE " + TABLE_COMPANIES + " SET " + F_CLOSED + " = ?, " + F_CLOSURE_REASON + " = ? WHERE "
							+ F_GAME_ID + " = ? AND " + F_COMPANY_ID + " = ? AND " + F_CLOSED + " IS NULL");
			
			int n = 1;
			statement.setString(n++,
					String.format("%04d-%02d-%02d",
							closureDate.getYear(),
							closureDate.getMonth(),
							closureDate.getDay()));
			statement.setString(n++, closureReason == null ? null : closureReason.toString());
			
			statement.setLong(n++, gameId);
			statement.setInt(n++, companyId);

			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to close a company {} of the game {}.", companyId, gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean storeEconomicData(long gameId, byte companyId, CompanyEconomy economy) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"INSERT INTO " + TABLE_ECONOMY + " ("
					+ F_COMPANY_ID + ", "
					+ F_TS + ", "
					+ F_INCOME + ", "
					+ F_LOAN + ", "
					+ F_MONEY + ", "
					+ F_VALUE + ", "
					+ F_PERFORMANCE + ") "
					+ "SELECT " + F_ID + ", ?, ?, ?, ?, ?, ? FROM " + TABLE_COMPANIES
						+ " WHERE " + F_GAME_ID + " = ? AND " + F_COMPANY_ID + " = ? AND " + F_CLOSED + " IS NULL");
			
			int n = 1;
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			statement.setLong(n++, economy.getIncome());
			statement.setLong(n++, economy.getLoan());
			statement.setLong(n++, economy.getMoney());
			statement.setLong(n++, economy.getPastCompanyValue()[0]);
			statement.setInt(n++, economy.getPastPerformance()[0]);

			statement.setLong(n++, gameId);
			statement.setInt(n++, companyId);
			
			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to store economic data of company {} of the game {}.", companyId, gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean storeStatisticalData(long gameId, byte companyId, CompanyStatistics stats) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"INSERT INTO " + TABLE_STATISTICS + " ("
					+ F_COMPANY_ID + ", "
					+ F_TS + ", "
					+ F_NTRAINS + ", "
					+ F_NLORRIES + ", "
					+ F_NBUSSES + ", "
					+ F_NPLANES + ", "
					+ F_NSHIPS + ", "
					+ F_NSTATIONS + ", "
					+ F_NDEPOTS + ", "
					+ F_NSTOPS + ", "
					+ F_NAIRPORTS + ", "
					+ F_NHARBOURS + ") "
					+ "SELECT " + F_ID + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? FROM " + TABLE_COMPANIES
						+ " WHERE " + F_GAME_ID + " = ? AND " + F_COMPANY_ID + " = ? AND " + F_CLOSED + " IS NULL");
			
			int n = 1;
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			statement.setInt(n++, stats.getNumberOfTrains());
			statement.setInt(n++, stats.getNumberOfLorries());
			statement.setInt(n++, stats.getNumberOfBusses());
			statement.setInt(n++, stats.getNumberOfPlanes());
			statement.setInt(n++, stats.getNumberOfShips());
			statement.setInt(n++, stats.getNumberOfTrainStations());
			statement.setInt(n++, stats.getNumberOfLorryDepots());
			statement.setInt(n++, stats.getNumberOfBusStops());
			statement.setInt(n++, stats.getNumberOfAirports());
			statement.setInt(n++, stats.getNumberOfHarbours());

			statement.setLong(n++, gameId);
			statement.setInt(n++, companyId);
			
			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to store statistical data of company {} of the game {}.", companyId, gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean createOrUpdatePlayer(long gameId, ClientData clientData) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"SELECT COUNT(*) FROM " + TABLE_CLIENTS + " WHERE " + F_GAME_ID + " = ? AND " + F_CLIENT_ID + " = ?"); 
			
			int n = 1;
			statement.setLong(n++, gameId);
			statement.setInt(n++, clientData.getClientId());

			int count = 0;
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				count = resultSet.getInt(1);
			}
			
			resultSet.close();
			statement.close();
			
			if (count == 0) {
				statement = connection.prepareStatement("INSERT INTO " + TABLE_CLIENTS + " ("
						+ F_NAME + ", "
						+ F_IP + ", "
						+ F_COUNTRY + ", "
						+ F_CITY + ", "
						+ F_PROXY + ", "
						+ F_GAME_ID + ", "
						+ F_CLIENT_ID
						+ ") VALUES (?, ?, ?, ?, ?, ?, ?)");
			} else if (count == 1) {
				statement = connection.prepareStatement("UPDATE " + TABLE_CLIENTS + " SET "
						+ F_NAME + " = ?, "
						+ F_IP + " = ?, "
						+ F_COUNTRY + " = ?, "
						+ F_CITY + " = ?, "
						+ F_PROXY + " = ? "
						+ "WHERE " + F_GAME_ID + " = ? AND " + F_CLIENT_ID + " = ?");
			} else {
				LOGGER.error("More than one player entries of the same key: game {}, client {}.", gameId, clientData.getClientId());
				return false;
			}
			
			n = 1;
			statement.setString(n++, clientData.getName());
			statement.setString(n++, clientData.getNetworkAddress());
			statement.setString(n++, clientData.getCountryCode());
			statement.setString(n++, clientData.getCity());
			statement.setBoolean(n++, clientData.isProxy());
			
			statement.setLong(n++, gameId);
			statement.setInt(n++, clientData.getClientId());

			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to create/update client data: game {}, client {}.", gameId, clientData.getClientId(), ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean storePlayer(long gameId, int playerId, byte companyId) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"INSERT INTO " + TABLE_PLAYERS + " ("
					+ F_TS + ", "
					+ F_GAME_ID + ", "
					+ F_CLIENT_ID + ", "
					+ F_COMPANY_ID + ") "
					+ "SELECT ?, " + F_GAME_ID + ", ?, " + F_ID + " FROM " + TABLE_COMPANIES
						+ " WHERE " + F_GAME_ID + " = ? AND " + F_COMPANY_ID + " = ? AND " + F_CLOSED + " IS NULL");
			
			int n = 1;
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			statement.setInt(n++, playerId);
			
			statement.setLong(n++, gameId);
			statement.setInt(n++, companyId);
			
			return statement.executeUpdate() == 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to store player {} joining company {} of the game {}.", playerId, companyId, gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public boolean playerQuit(long gameId, int playerId) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(
					"UPDATE " + TABLE_PLAYERS + " SET "
					+ F_LEFT_TS + " = ? "
						+ "WHERE " + F_GAME_ID + " = ? AND " + F_CLIENT_ID + " = ?");
			
			int n = 1;
			statement.setTimestamp(n++, new Timestamp(System.currentTimeMillis()));
			
			statement.setLong(n++, gameId);
			statement.setInt(n++, playerId);
			
			return statement.executeUpdate() >= 1;
		} catch (SQLException ex) {
			LOGGER.error("Failed to log player {} quitting game {}.", playerId, gameId, ex);
			return false;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	public List<TopPlayer> getTopList(long gameId, int limit) {
		PreparedStatement statement = null;
		try {
			ArrayList<TopPlayer> result = new ArrayList<>(limit);
			
			statement = connection.prepareStatement(
					"SELECT c.name, "
							+ " MAX(e.income), MAX(e.loan), MAX(e.money), MAX(e.`value`) AS here, MAX(e.performance),"
							+ " g.started, g.finished"
							+ " FROM genowefa_economy AS e"
							+ " LEFT JOIN genowefa_companies AS c ON (e.company_id = c.id)"
							+ " LEFT JOIN genowefa_games AS g ON (c.game_id = g.id)"
							+ " WHERE g.server_name = (SELECT server_name FROM genowefa_games WHERE id = ?)"
							+ " GROUP BY e.company_id"
							+ " ORDER BY here DESC"
							+ " LIMIT ?");
			
			int n = 1;
			statement.setLong(n++, gameId);
			statement.setInt(n++, limit);
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				Timestamp ts;
				
				n = 1;
				TopPlayer topPlayer = new TopPlayer(
						resultSet.getString(n++),
						resultSet.getLong(n++),
						resultSet.getLong(n++),
						resultSet.getLong(n++),
						resultSet.getLong(n++),
						resultSet.getInt(n++),
						(ts = resultSet.getTimestamp(n++)) == null ? 0 : ts.getTime(),
						(ts = resultSet.getTimestamp(n++)) == null ? 0 : ts.getTime());
				
				result.add(topPlayer);
			}
		
			return result;
			
		} catch (SQLException ex) {
			LOGGER.error("Failed to get a top list of the game {}.", gameId, ex);
			return null;
		} finally {
			try {
				statement.close();
			} catch (Exception e) {}
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			this.connection.close();
		} catch (SQLException e) {
			throw new IOException("Failed to close the SQL connection.", e);
		}
	}
}
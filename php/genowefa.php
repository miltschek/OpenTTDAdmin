<?php

function getColorName($color) {
	switch ($color) {
	case 0: return "Dark Blue";
	case 1: return "Pale Green";
	case 2: return "Pink";
	case 3: return "Yellow";
	case 4: return "Red";
	case 5: return "Light Blue";
	case 6: return "Green";
	case 7: return "Dark Green";
	case 8: return "Blue";
	case 9: return "Cream";
	case 10: return "Mauve";
	case 11: return "Purple";
	case 12: return "Orange";
	case 13: return "Brown";
	case 14: return "Grey";
	case 15: return "White";
	default: return strval($color);
	}
}

function getColorCode($color, $light = false) {
	switch ($color) {
	case 0: return $light ? "4898d8" : "#1c448c";
	case 1: return $light ? "98c0a8" : "#4c7458";
	case 2: return $light ? "ec9ca4" : "#bc546c";
	case 3: return $light ? "fcf880" : "#d49c20";
	case 4: return $light ? "fc6458" : "#c40000";
	case 5: return $light ? "9cccdc" : "#347084";
	case 6: return $light ? "7cc84c" : "#548414";
	case 7: return $light ? "98b06c" : "#50683c";
	case 8: return $light ? "80c4fc" : "#1878dc";
	case 9: return $light ? "e0a880" : "#b87050";
	case 10: return $light ? "acacc0" : "#505074";
	case 11: return $light ? "a088fc" : "#684cc4";
	case 12: return $light ? "fcd898" : "#fc9c00";
	case 13: return $light ? "d4bc94" : "#7c6848";
	case 14: return $light ? "a8a8a8" : "#737573";
	case 15: return $light ? "e8e8e8" : "#b8b8b8";
	default: return "#ffffff";
	}
}

class MyDao {
	private const SERVERNAME = 'localhost';
	private const USERNAME = 'USERNAME';
	private const PASSWORD = 'PASSWORD';
	private const DBNAME = 'DATABASE_NAME';
	private $connection;

	function __construct() {
		$this->connection = new mysqli(self::SERVERNAME, self::USERNAME, self::PASSWORD, self::DBNAME);
		$this->connection->query('SET time_zone="+0:00";');
	}

	function getGameNames() {
		echo '<!-- getGameNames -->';
		$sql = $this->connection->prepare('select distinct game_name from genowefa_games order by 1');
		$sql->bind_result($gameName);
		$sql->execute();
		$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'gameName' => $gameName);
		}

		return $result;
	}

	function getGames() {
		echo '<!-- getGames -->';
		$sql = $this->connection->prepare('select id, game_name, address, port, ts_started, ts_finished, server_name, generation_seed, starting_year, map_size_x, map_size_y, game_date, performance from genowefa_games order by ts_started desc');
		$sql->bind_result($id, $gameName, $address, $port, $started, $finished, $serverName, $generationSeed, $startingYear, $mapSizeX, $mapSizeY, $gameDate, $performance);
		$sql->execute();
		$sql->store_result();
		$result = array();

		while ($sql->fetch()) {
			$result[] = array('id' => $id,
				'gameName' => $gameName,
				'address' => $address,
				'port' => $port,
				'started' => $started,
				'finished' => $finished,
				'serverName' => $serverName,
				'generationSeed' => $generationSeed,
				'startingYear' => $startingYear,
				'mapSizeX' => $mapSizeX,
				'mapSizeY' => $mapSizeY,
				'gameDate' => $gameDate,
				'performance' => $performance);
		}

		return $result;
	}

	function getClientsCount($gameId) {
		$sql = $this->connection->prepare('select count(*) from genowefa_players where game_id = ? and ts_left = 0');
		$sql->bind_param('i', $gameId);
		$sql->bind_result($count);
		$sql->execute();
		$sql->store_result();

		if ($sql->fetch()) {
			return $count;
		}

		return -1;
	}

	function getClients($companyId) {
		echo '<!-- getClients -->';
		$sql = $this->connection->prepare('select p.ts_joined, p.ts_left, c.client_id, c.name, c.ip, c.country, c.city, c.proxy from genowefa_players as p left join genowefa_clients as c on p.game_id = c.game_id and p.client_id = c.client_id where p.company_id = ?');
		$sql->bind_param('i', $companyId);

		$sql->bind_result($ts, $leftTs, $clientId, $name, $ip, $country, $city, $proxy);
		$sql->execute();
		$sql->store_result();
		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'ts' => $ts,
				'leftTs' => $leftTs,
				'clientId' => $clientId,
				'name' => $name,
				'ip' => $ip,
				'country' => $country,
				'city' => $city,
				'proxy' => $proxy);
		}

		return $result;
	}

	function getEconomy($companyId) {
		echo '<!-- getEconomy -->';
		$sql = $this->connection->prepare('select income, loan, money, value, performance from genowefa_economy where company_id = ? order by ts desc limit 1');
		$sql->bind_param('i', $companyId);
		$sql->bind_result($income, $loan, $money, $value, $performance);
		$sql->execute();
		$sql->store_result();

		if ($sql->fetch()) {
			return array(
				'income' => $income,
				'loan' => $loan,
				'money' => $money,
				'value' => $value,
				'performance' => $performance);
		}

		return null;
	}

	function getInfrastructure($companyId) {
		echo '<!-- getInfrastructure -->';

		$sql = $this->connection->prepare('select num_busses, num_lorries, num_trains, num_ships, num_planes, '
			. 'num_stops, num_depots, num_stations, num_harbours, num_airports '
			. 'from genowefa_statistics where company_id = ? order by ts desc limit 1');
		$sql->bind_param('i', $companyId);
		$sql->bind_result($busses, $lorries, $trains, $ships, $planes,
			 $busStops, $lorryDepots, $trainStations, $harbours, $airports);
		$sql->execute();
		$sql->store_result();

		if ($sql->fetch()) {
			return array(
				'busses' => $busses,
				'lorries' => $lorries,
				'trains' => $trains,
				'ships' => $ships,
				'planes' => $planes,
				'busStops' => $busStops,
				'lorryDepots' => $lorryDepots,
				'trainStations' => $trainStations,
				'harbours' => $harbours,
				'airports' => $airports);
		}

		return null;
	}

	function getCompanies($gameId, $activeOnly = true) {
		$sql = $this->connection->prepare('SELECT c.id, c.company_id, c.name, c.color, c.founded, c.closed, c.closure_reason, c.manager_name, c.password_protected, '
			. 'e.value, e.performance '
			. 'FROM genowefa_companies c '
			. 'LEFT JOIN genowefa_economics e ON (e.company_id = c.id) '
			. 'WHERE game_id = ? '
			. ($activeOnly ? 'AND closed IS NULL ' : '')
			. 'ORDER BY e.value DESC');
		$sql->bind_param('i', $gameId);
		$sql->bind_result($id, $companyId, $name, $color, $founded, $closed, $closureReason, $managerName, $passwordProtected,
			$value, $performance);
		$sql->execute();
		$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'id' => $id,
				'companyId' => $companyId,
				'name' => $name,
				'color' => $color,
				'founded' => $founded,
				'closed' => $closed,
				'closureReason' => $closureReason,
				'managerName' => $managerName,
				'passwordProtected' => $passwordProtected,
				'value' => $value,
				'performance' => $performance);
		}

		return $result;
	}

	function getTopTen($limit = 10, $gameName) {
		$filter = $gameName !== null;

		$sql = $this->connection->prepare(
			'SELECT c.name, c.color, '
			. 'e.income, e.loan, e.money, e.value, e.performance, '
			. 'i.num_busses, i.num_lorries, i.num_trains, i.num_ships, i.num_planes, '
			. 'i.num_stops, i.num_depots, i.num_stations, i.num_harbours, i.num_airports, '
			. 'g.ts_started, g.ts_finished '
			. 'FROM '
			. 'genowefa_companies AS c '
			. 'LEFT JOIN genowefa_economics AS e ON (e.company_id = c.id) '
			. 'LEFT JOIN genowefa_infrastructure i ON (i.company_id = c.id) '
			. 'LEFT JOIN genowefa_games g ON (g.id = c.game_id) '
			. 'WHERE c.id IN '
				// 1 = server name
				. '(SELECT DISTINCT id FROM genowefa_companies WHERE game_id IN (SELECT DISTINCT id FROM genowefa_games WHERE game_name = ?)) '
			. 'ORDER BY e.value DESC '
			// 2 = limit
			. 'LIMIT ?;');

		if ($filter) {
			$sql->bind_param('si', $gameName, $limit);
		} else {
			$sql->bind_param('si', "*", $limit);
		}

		$sql->bind_result($companyName, $companyColor,
			$maxIncome, $maxLoan, $maxMoney, $maxValue, $maxPerformance,
			$maxBusses, $maxLorries, $maxTrains, $maxShips, $maxPlanes,
			$maxBusStops, $maxLorryDepots, $maxTrainStations, $maxHarbours, $maxAirports,
			$gameStarted, $gameFinished
			);
		$sql->execute();
		$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'companyName' => $companyName,
				'companyColor' => $companyColor,
				'maxIncome' => $maxIncome,
				'maxLoan' => $maxLoan,
				'maxMoney' => $maxMoney,
				'maxValue' => $maxValue,
				'maxPerformance' => $maxPerformance,
				'maxBusses' => $maxBusses,
				'maxLorries' => $maxLorries,
				'maxTrains' => $maxTrains,
				'maxShips' => $maxShips,
				'maxPlanes' => $maxPlanes,
				'maxBusStops' => $maxBusStops,
				'maxLorryDepots' => $maxLorryDepots,
				'maxTrainStations' => $maxTrainStations,
				'maxHarbours' => $maxHarbours,
				'maxAirports' => $maxAirports,
				'gameStarted' => $gameStarted,
				'gameFinished' => $gameFinished);
			}

		return $result;
	}

	function getTopCountries() {
		echo '<!-- getTopCountries -->';

		$sql = $this->connection->prepare('select country, count(*) from '
			. '(select country from genowefa_clients where proxy = 0 and country is not null group by country, ip) sub '
			. 'group by country '
			. 'order by 2 desc, 1');
		$sql->bind_result($country, $count);
		$sql->execute();
		$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'country' => $country,
				'count' => $count);
		}

		return $result;
	}
	
	function getPlayersData($gameId) {
		$sql = $this->connection->prepare('SELECT '
			. 'co.id, co.company_id, co.name, co.color, co.manager_name, co.founded, co.closed, co.closure_reason, co.password_protected, '
			. 'c.client_id, c.name, c.ip, c.country, c.city, c.proxy, c.ts_joined, c.ts_left, '
			. 'p.ts_joined, p.ts_left '
			. 'FROM genowefa_clients c '
			. 'LEFT JOIN genowefa_players p ON c.game_id = p.game_id AND c.client_id = p.client_id '
			. 'LEFT JOIN genowefa_companies co ON co.game_id = c.game_id AND co.id = p.company_id '
			. 'WHERE c.client_id != 1 AND (co.id IS NOT NULL OR c.ts_left = 0) AND c.game_id = ? '
			. 'ORDER BY co.company_id, co.founded');

		$sql->bind_param('i', $gameId);

		$sql->bind_result($dbCompanyId, $companyId, $companyName, $companyColor, $companyManager, $companyFounded, $companyClosed, $companyClosureReason, $companyPasswordProtected,
			$clientId, $clientName, $clientIp, $clientCountry, $clientCity, $clientProxy, $clientTsJoined, $clientTsLeft,
			$playerTsJoined, $playerTsLeft);
			
		$sql->execute();
		$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
			$result[] = array(
				'dbCompanyId' => $dbCompanyId,
				'companyId' => $companyId,
				'companyName' => $companyName,
				'companyColor' => $companyColor,
				'companyManager' => $companyManager,
				'companyFounded' => $companyFounded,
				'companyClosed' => $companyClosed,
				'companyClosureReason' => $companyClosureReason,
				'companyPasswordProtected' => $companyPasswordProtected,
				'clientId' => $clientId,
				'clientName' => $clientName,
				'clientIp' => $clientIp,
				'clientCountry' => $clientCountry,
				'clientCity' => $clientCity,
				'clientProxy' => $clientProxy,
				'clientTsJoined' => $clientTsJoined,
				'clientTsLeft' => $clientTsLeft,
				'playerTsJoined' => $playerTsJoined,
				'playerTsLeft' => $playerTsLeft);
		}

		return $result;
	}

	function __destruct() {
		$this->connection->close();
	}
}

?>
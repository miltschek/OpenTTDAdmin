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
	}

	function getServers() {
		echo '<!-- getServers -->';
		$sql = $this->connection->prepare('select address, port, server_name from genowefa_games group by address, port, server_name order by 1, 2, 3');
				$sql->bind_result($address, $port, $serverName);
				$sql->execute();
				$sql->store_result();

		$result = array();

		while ($sql->fetch()) {
						$result[] = array(
								'address' => $address,
								'port' => $port,
								'serverName' => $serverName);
				}

				return $result;
	}

	function getGames() {
		echo '<!-- getGames -->';
		$sql = $this->connection->prepare('select id, address, port, started, finished, server_name, generation_seed, starting_year, map_size_x, map_size_y from genowefa_games order by started desc');
		$sql->bind_result($id, $address, $port, $started, $finished, $serverName, $generationSeed, $startingYear, $mapSizeX, $mapSizeY);
		$sql->execute();
		$sql->store_result();
		$result = array();

				while ($sql->fetch()) {
						$result[] = array('id' => $id,
				'address' => $address,
				'port' => $port,
				'started' => $started,
				'finished' => $finished,
				'serverName' => $serverName,
				'generationSeed' => $generationSeed,
				'startingYear' => $startingYear,
				'mapSizeX' => $mapSizeX,
				'mapSizeY' => $mapSizeY);
				}

				return $result;
	}

	function getCompanies($gameId) {
		echo '<!-- getCompanies -->';
		$sql = $this->connection->prepare('select id, company_id, founded, closed, closure_reason, color, name, manager_name, password_protected from genowefa_companies where game_id = ? order by id');
		$sql->bind_param('i', $gameId);
		$sql->bind_result($id, $companyId, $founded, $closed, $closureReason, $color, $name, $managerName, $passwordProtected);
		$sql->execute();
		$sql->store_result();
				$result = array();

				while ($sql->fetch()) {
						$result[] = array('id' => $id,
				'companyId' => $companyId,
								'founded' => $founded,
								'closed' => $closed,
								'closureReason' => $closureReason,
								'color' => $color,
								'name' => $name,
								'managerName' => $managerName,
								'passwordProtected' => $passwordProtected);
				}

				return $result;
	}

	function getClients($companyId) {
		echo '<!-- getClients -->';
		$sql = $this->connection->prepare('select p.ts, p.left_ts, c.client_id, c.name, c.ip, c.country, c.city, c.proxy from genowefa_players as p left join genowefa_clients as c on p.game_id = c.game_id and p.client_id = c.client_id where p.company_id = ?');
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

	function getTopTen($limit = 10, $serverName = null) {
		$filter = $serverName !== null;
		$sql = $this->connection->prepare('SELECT c.name, c.color,'
			. ' MAX(e.income), MAX(e.loan), MAX(e.money), MAX(e.`value`) AS here, MAX(e.performance),'
			. ' MAX(s.num_busses), MAX(s.num_lorries), MAX(s.num_trains), MAX(s.num_ships), MAX(s.num_planes),'
			. ' MAX(s.num_stops), MAX(s.num_depots), MAX(s.num_stations), MAX(s.num_harbours), MAX(s.num_airports),'
			. ' g.server_name, g.started, g.finished'
			. ' FROM genowefa_economy AS e'
			. ' LEFT JOIN genowefa_statistics AS s ON (e.company_id = s.company_id)'
			. ' LEFT JOIN genowefa_companies AS c ON (e.company_id = c.id)'
			. ' LEFT JOIN genowefa_games AS g ON (c.game_id = g.id)'
			. ($filter ? ' WHERE g.server_name = ?' : '')
			. ' GROUP BY e.company_id'
			. ' ORDER BY here DESC'
			. ' LIMIT ?;');

		if ($filter) {
			$sql->bind_param('si', $serverName, $limit);
		} else {
			$sql->bind_param('i', $limit);
		}

		$sql->bind_result($companyName, $companyColor,
			$maxIncome, $maxLoan, $maxMoney, $maxValue, $maxPerformance,
			$maxBusses, $maxLorries, $maxTrains, $maxShips, $maxPlanes,
			$maxBusStops, $maxLorryDepots, $maxTrainStations, $maxHarbours, $maxAirports,
			$serverName, $gameStarted, $gameFinished
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
				'serverName' => $serverName,
				'gameStarted' => $gameStarted,
				'gameFinished' => $gameFinished);
				}

				return $result;

	}

	function __destruct() {
		$this->connection->close();
	}
}

?>

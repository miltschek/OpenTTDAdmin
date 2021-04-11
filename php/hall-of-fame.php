<?php

require '_genowefa.php';
$dao = new MyDao();

echo '<html>';
echo '<style>';
echo 'tr.toptable:nth-child(even) { background: #ccc } ';
echo 'tr.toptable:nth-child(odd) { background: #eee } ';
echo 'td { text-align: center; padding: 5px; } ';
echo 'th { padding: 5px; } ';
echo 'td {  } ';
echo 'th.marked { border: 3px solid #d4af37; background: #ffdf00; } ';
echo 'td.marked { border: 3px solid #d4af37; background: #ffdf00; } ';
echo 'h1 { font-size: 200%; } ';
echo 'p {  } ';
echo '</style>';
echo '<body>';

$servers = $dao->getServers();

foreach ($servers as $server) {

	echo '<h1>' . htmlspecialchars($server['serverName']) . '</h1>';

	echo '<table class="toptable">';
	echo '<tr class="toptable">';
	echo '<th>Place</th>';
	echo '<th>Company Name</td>';
	echo '<th>Top Income</th>';
	echo '<th>Maximum Loan</th>';
	echo '<th>Top Cash</th>';
	echo '<th class="marked">Top Company Value</th>';
	echo '<th>Top Performance</th>';
	echo '<th>Top&nbsp;No.&nbsp;of Busses / Bus&nbsp;Stops</th>';
	echo '<th>Top&nbsp;No.&nbsp;of Trucks / Truck&nbsp;Depots</th>';
	echo '<th>Top&nbsp;No.&nbsp;of Trains / Train&nbsp;Stations</th>';
	echo '<th>Top&nbsp;No.&nbsp;of Ships / Harbors</th>';
	echo '<th>Top&nbsp;No.&nbsp;of Airplanes / Airports</th>';
	echo '<th>Game Started</th>';
	echo '<th>Game Finished</th>';
	echo '</tr>';
	echo "\r\n";

	$topTen = $dao->getTopTen(10, $server['serverName']);

	$n = 1;

	foreach ($topTen as $entry) {
		echo '<tr class="toptable">'
		. '<th>' . $n++ . '</th>'
		. '<td style="background-color: ' . getColorCode($entry['companyColor'], true) . '; border: 3px solid ' . getColorCode($entry['companyColor']) . '">'
			. '<b>' . htmlspecialchars($entry['companyName']) . '</b>'
			. '<br/>' . getColorName($entry['companyColor'])
			. '</td>'
		. '<td>' . number_format($entry['maxIncome']) . '&nbsp;&#163;</td>'
		. '<td>' . number_format($entry['maxLoan']) . '&nbsp;&#163;</td>'
		. '<td>' . number_format($entry['maxMoney']) . '&nbsp;&#163;</td>'
		. '<td class="marked">' . number_format($entry['maxValue']) . '&nbsp;&#163;</td>'
		. '<td>' . $entry['maxPerformance'] . '</td>'
		. '<td>' . $entry['maxBusses'] . ' / ' . $entry['maxBusStops'] . '</td>'
		. '<td>' . $entry['maxLorries'] . ' / ' . $entry['maxLorryDepots'] . '</td>'
		. '<td>' . $entry['maxTrains'] . ' / ' . $entry['maxTrainStations'] . '</td>'
		. '<td>' . $entry['maxShips'] . ' / ' . $entry['maxHarbours'] . '</td>'
		. '<td>' . $entry['maxPlanes'] . ' / ' . $entry['maxAirports'] . '</td>'
		. '<td>' . $entry['gameStarted'] . '</td>'
		. '<td>' . $entry['gameFinished'] . '</td>'
		. '</tr>' . "\r\n";
	}

	echo '</table>';
	echo "\r\n";

}

echo '</body>';
echo '</html>';

?>

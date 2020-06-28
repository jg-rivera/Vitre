<?php

if(!empty($_GET['id'])) {
	require_once "../php/SqlUtils.php";
	require_once "../php/TicketManager.php";
	
	date_default_timezone_set('Asia/Manila');
	
	$id = $_GET['id'];
	$conn = SqlUtils::connect();
	$req = "SELECT TicketID, Owner, State, Subject, Description, TimeStamp FROM Tickets ";
	$req .= "WHERE TicketID=$id";
	
	$query = mysqli_query($conn, $req);
	
	if (!$query) echo mysqli_error();
	
	$row = $query -> fetch_assoc();
	$date = date('M d, Y; h:i:s A', $row['TimeStamp']);
	$state = $row['State'];
	
	echo '<h5>'. $row['Subject'];
	echo '<span class="text-muted tid">(#' . $row['TicketID'] . ')</span> </h5>';
	
	echo '<hr>';

	echo '<table class="table table-striped"> <tbody>';
	echo '<tr><td> Submitted by </td> <td>' . $row['Owner'] .'</td></tr>';
	echo '<tr><td> Date submitted </td> <td>'. $date  .'</td></tr>';
	echo '</tbody> </table>';
	
	echo '<div class="form-group"><label for="desc">Description</label>';
	echo '<textarea readonly class="form-control" rows="5" id="desc">'. $row['Description'] . '</textarea></div>';
	
	echo '<div class="form-group"><label for="remarks">Resolving Remarks</label>';
	echo '<textarea placeholder="Update this ticket holder about the progress in resolving the issue..." class="form-control" rows="3" id="remarks"></textarea></div>';
	
	function sel($state, $index) {
		if ($state == $index) return "selected";
	}
	
	echo '<div class="form-group state-dropdown"><label for="sel1">Update state</label><select class="form-control" id="sel1">';
	echo '<option ' . sel($state, TicketManager::UNRESOLVED) . '>Unresolved</option>';
	echo '<option ' . sel($state, TicketManager::RESOLVED)  . '>Resolved</option>';
	echo '<option ' . sel($state, TicketManager::CHECKED) . '>Checked</option>';
	echo '</select></div>';

	
	
	$conn -> close();
}

?>
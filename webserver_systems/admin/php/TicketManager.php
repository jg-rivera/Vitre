<?php

	require_once ("SqlUtils.php");

	class TicketManager {
		
		// Ticket States
		const UNRESOLVED = 0;
		const RESOLVED = 1;
		const CHECKED = 2;
		
		// Ticket label strings
		const UNRESOLVED_STR = "Unresolved";
		const RESOLVED_STR = "Resolved";
		const CHECKED_STR = "Checked";
		
		public static function submit($owner, $subject, $desc) {
			$conn = SqlUtils::connect();
			$state = TicketManager::UNRESOLVED;
			$stamp = time();
			
			$req = "INSERT INTO Tickets(Owner, State, Subject, Description, TimeStamp) ";
			$req .= "VALUES ('$owner', $state, '$subject', '$desc', $stamp);";
			
			$query = mysqli_query($conn, $req);
			
			if ($query) {
				echo "Ticket submitted. \n";
			} else {
				echo mysqli_error();
			}
			
			$conn -> close();
		}
		
		public static function update($ticket_id, $stateStr) {
			$conn = SqlUtils::connect();
			$state = TicketManager::getStateFromString($stateStr);
			
			$req = "UPDATE Tickets SET State=$state;";
			$query = mysqli_query($conn, $req);
			
			if ($query) {
				echo "Ticket updated. \n";
			} else {
				echo mysqli_error();
			}
			$conn -> close();
		}
		
		public static function delete($ticket_id) {
			$conn = SqlUtils::connect();
			$req = "DELETE FROM Tickets WHERE TicketID=$ticket_id;";
			$query = mysqli_query($conn, $req);
			
			if ($query) {
				echo "Ticket deleted. \n";
			} else {
				echo mysqli_error();
			}
			$conn -> close();
		}
		
		public static function printTickets() {
			$conn = SqlUtils::connect();
			$req = "SELECT * FROM Tickets;";
			$query = mysqli_query($conn, $req);
			
			$tickets = array();
			date_default_timezone_set('Asia/Manila');
			
			while ($row = $query -> fetch_assoc()) {
				$tix = array();
				$tix['id'] = $row['TicketID'];
				$tix['owner'] = $row['Owner'];
				$tix['state'] = $row['State'];
				$tix['subject'] = $row['Subject'];
				$tix['desc'] = $row['Description'];
				$tix['stamp'] = date('M d, Y; h:i:s A', $row['TimeStamp']);
				array_push($tickets, $tix);
			}
			

			foreach($tickets as $tix) {
				echo '<tr>';
				echo '<td class="ticket_id">#' . $tix['id'] . '  ';
				echo TicketManager::getPillFromState($tix['state']) .  '</td>';
				echo '<td>' . $tix['subject'] . '</td>';
				echo '<td>' . $tix['owner'] . '</td>';
				echo '<td>' . $tix['stamp'] . '</td>';
				echo '<td> ';
				echo '<button class="btn btn-danger act-delete"> <i class="far fa-trash-alt fa-fw"> </i> </button> ';
				echo '<button class="btn btn-info act-view"> <i class="fas fa-info fa-fw"> </i> </button> ';
				echo '</td>';
				echo '</tr>';
			}
			
			$conn -> close();
		}
		
		public static function getPillFromState($state) {
			switch($state) {
				case 0: // Unresolved
					echo '<span class="badge badge-danger">' . TicketManager::UNRESOLVED_STR . '</span>';
					break;
				case 1: // Resolved 
					echo '<span class="badge badge-success">' . TicketManager::RESOLVED_STR . '</span>';
					break;
				case 2: // Checked
					echo '<span class="badge badge-warning">' . TicketManager::CHECKED_STR . '</span>';
					break;
			}
		}
		
		public static function getStateFromString($str) {
			switch($str) {
				case TicketManager::UNRESOLVED_STR:
					return TicketManager::UNRESOLVED;
				case TicketManager::RESOLVED_STR:
					return TicketManager::RESOLVED;
				case TicketManager::CHECKED_STR:
					return TicketManager::CHECKED;
			}
		}
		
		public static function printEmpty() {
			$conn = SqlUtils::connect();
			$req = "SELECT COUNT(*) as total FROM Tickets;";
			$query = mysqli_query($conn, $req);
			
			$data = mysqli_fetch_assoc($query);
			$total = $data['total'];
			
			if ($total < 1) {
				echo '<p class="small text-center text-muted my-5"><em> No tickets found.</em></p>';
			}
			
			$conn -> close();
		}
	}


?>
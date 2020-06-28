<?php

include "../php/TicketManager.php";

if (!isset($_POST['ticket_id'])) return;

$ticket = $_POST['ticket_id'];
TicketManager::delete($ticket);


?>
<?php

include "../php/TicketManager.php";

if (!isset($_POST['ticket_id']) or !isset($_POST['ticket_state'])) return;

$id = $_POST['ticket_id'];
$state = $_POST['ticket_state'];

TicketManager::update($id, $state);


?>
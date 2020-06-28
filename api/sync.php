<?php

include "../scripts/utility/SqlUtils.php";

$mysqli = SqlUtils::connect();

// Check connection.
if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

// Supply the appropriate signatures to each variable.
$chunk_class = $mysqli->real_escape_string($_POST['c']); // class
$chunk_subject = $mysqli->real_escape_string($_POST['s']); //subject
$chunk_teacher = $mysqli->real_escape_string($_POST['t']); // teacher

$data = array();

$result = $mysqli->query("SELECT ClassID FROM Classes WHERE ClassChunkKey = '$chunk_class';");
if (!$result) {
	echo 'Could not run query ' . $mysqli -> error;
	die();
}
$row = mysqli_fetch_row($result);

$data['classID'] = (int) $row[0];

$c_id = $data['classID'];

// If this class is registered.
if ($c_id > 0) {
	$req1 = 'SELECT 1 FROM Students WHERE ClassID=' . $c_id . ';';
	$query = mysqli_query($mysqli, $req1);
	// Empty checking
	if (mysqli_num_rows($query) < 1) {
		$data['emptyClass'] = true;
		$data['classCount'] = 0;
	} else {
		$req2 = mysqli_query($mysqli, "SELECT FirstName, LastName, StudentIndex FROM Students WHERE ClassID=" . $c_id . ";");
		
		$names = array();
		
		while($row = $req2 -> fetch_assoc()) {
			$nm = $row['StudentIndex'] . '. ' . $row['LastName'] . ', ' . $row['FirstName'];
			array_push($names, $nm);
		}
		$data['classCount'] = sizeof($names);
		$data['emptyClass'] = false;
		$data['registry'] = $names;
	}
}

$result = $mysqli->query("SELECT SubjectID FROM Subjects WHERE SubjectChunkKey = '$chunk_subject';");
if (!$result) {
	echo 'Could not run query ' . $mysqli -> error;
	die();
}
$row = mysqli_fetch_row($result);
$data['subjectID'] = (int) $row[0];

$result = $mysqli->query("SELECT TeacherID FROM Teachers WHERE TeacherChunkKey = '$chunk_teacher';");
if (!$result) {
	echo 'Could not run query ' . $mysqli -> error;
	die();
}
$row = mysqli_fetch_row($result);
$data['teacherID'] = (int) $row[0];

echo json_encode($data);


?>
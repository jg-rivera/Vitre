<?php

if(!empty($_GET['id'])) {
	require_once "../php/SqlUtils.php";
	require_once "../php/TeacherUtils.php";
	
	$id = $_GET['id'];

	$conn = SqlUtils::connect();
	$req = "SELECT TeacherID, TeacherName, TeacherChunkKey, Honorific ";
	$req .= "FROM Teachers WHERE TeacherID=$id ";
	
	$query = mysqli_query($conn, $req);
	
	$row = $query -> fetch_assoc();
	
	echo '<h5>'. $row['TeacherName'];
	echo '<span class="text-muted"> (#' . $row['TeacherID'] . ') </span> </h5>';
	echo '<hr>';
	echo '<table class="table table-striped"> <tbody>';
	echo '<tr><td> Honorific </td> <td>'. $row['Honorific'] .'</td></tr>';
	echo '<tr><td> Digital Signature </td> <td><code>'. $row['TeacherChunkKey'] .'</code></td></tr>';
		
	// Build advisory class list
	echo '<tr><td> Advisory Class </td><td>';
	foreach(TeacherUtils::getAdvisoryClass($id) as $c) {
		echo "<ul style='list-style-type:none; padding: 0; margin: 0;'>";
		echo '<li>' . $c['name'] . "<span class='text-muted'>" . ' (#' . $c['id'] . ')' . '</span></li>';
		echo '</ul>';
	}
	echo '</td></tr>';
	
	// Build subjects list
	echo '<tr><td> Subjects </td><td>';
	foreach(TeacherUtils::getSubjects($id) as $c) {
		echo "<ul style='list-style-type:none; padding: 0; margin: 0;'>";
		echo '<li>' . $c['name'] . "<span class='text-muted'>" . ' (#' . $c['id'] . ')' . '</span></li>';
		echo '</ul>';
	}
	echo '</td></tr>';
	
	echo '</tbody> </table>';
	
	$conn -> close();
}

?>
<?php

if(!empty($_GET['id'])) {
	require_once "../php/SqlUtils.php";
	
	$id = $_GET['id'];
	$conn = SqlUtils::connect();
	$req = "SELECT SubjectID, SubjectName, SubjectChunkKey, IFNULL(TeacherName, 'None') AS TeacherName, Semester, TrackName ";
	$req .= "FROM Subjects LEFT JOIN Teachers on Subjects.InstructorID = Teachers.TeacherID ";
	$req .= "INNER JOIN Tracks on Subjects.TrackID = Tracks.TrackID ";
	$req .= "WHERE SubjectID=$id";
	
	$query = mysqli_query($conn, $req);
	
	$row = $query -> fetch_assoc();
	
	echo '<h5>'. $row['SubjectName'];
	echo '<span class="text-muted"> (#' . $row['SubjectID'] . ') </span> </h5>';
	echo '<hr>';
	echo '<table class="table table-striped"> <tbody>';
	echo '<tr><td> Instructor </td> <td>'. $row['TeacherName'] .'</td></tr>';
	echo '<tr><td> Semester </td> <td>'. $row['Semester'] .'</td></tr>';
	echo '<tr><td> Track </td> <td>'. $row['TrackName'] .'</td></tr>';
	echo '<tr><td> Subject Signature </td> <td><code>'. $row['SubjectChunkKey'] .'</code></td></tr>';
	echo '</tbody> </table>';
	$conn -> close();
}

?>
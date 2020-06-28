<?php

if(!empty($_GET['id'])) {
	require_once "../php/SqlUtils.php";
	
	$id = $_GET['id'];
	$conn = SqlUtils::connect();
	$req = "SELECT StudentID, LastName, FirstName, StudentIndex, Gender, UserName, ClassName, TeacherName FROM Students ";
	$req .= "INNER JOIN Classes on Students.ClassID = Classes.ClassID ";
	$req .= "INNER JOIN Teachers on Classes.AdviserID = Teachers.TeacherID ";
	$req .= "WHERE StudentID=$id";
	
	$query = mysqli_query($conn, $req);
	
	$row = $query -> fetch_assoc();
	
	echo '<h5>'. $row['FirstName'] . ' ' . $row['LastName'];
	echo '<span class="text-muted"> (#' . $row['StudentID'] . ') </span> </h5>';
	echo '<hr>';
	echo '<table class="table table-striped"> <tbody>';
	echo '<tr><td> Student Index </td> <td>' . $row['StudentIndex'] .'</td></tr>';
	echo '<tr><td> Gender </td> <td>'. $row['Gender'] .'</td></tr>';
	echo '<tr><td> Username </td> <td>'. $row['UserName'] .'</td></tr>';
	echo '<tr><td> Section </td> <td>'. $row['ClassName'] .'</td></tr>';
	echo '<tr><td> Adviser </td> <td>'. $row['TeacherName'] .'</td></tr>';
	echo '</tbody> </table>';
	$conn -> close();
}

?>
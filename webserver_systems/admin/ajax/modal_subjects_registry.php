<?php

	require_once "../php/SqlUtils.php";
	require_once "../php/SubjectUtils.php";

	$conn = SqlUtils::connect();
	$req = "SELECT SubjectID, SubjectName, SubjectChunkKey, TeacherName, Semester, TrackName ";
	$req .= "FROM Subjects INNER JOIN Teachers on Subjects.InstructorID = Teachers.TeacherID ";
	$req .= "INNER JOIN Tracks on Subjects.TrackID = Tracks.TrackID ";
	$req .= "WHERE SubjectID=1";
	
	$query = mysqli_query($conn, $req);
	
	$row = $query -> fetch_assoc();

	echo '<div class="form-group">
		  <label for="name">Name</label>
		  <input type="text" class="form-control" id="name">
		</div>';
	
	echo '<div class="form-group">
		  <label for="instructor">Instructor</label>
		  <select class="form-control" id="instructor">';
	foreach(SubjectUtils::getInstructors() as $t)
		echo "<option>$t</option>";
	echo '</select></div>';
	
	echo '<div class="form-group">
		  <label for="track">Track</label>
		  <select class="form-control" id="track">';
	foreach(SubjectUtils::getTracks() as $t)
		echo "<option>$t</option>";
	echo '</select></div>';
	
	echo '<div class="form-group">
		  <label for="semester">Semester</label>
		  <select class="form-control" id="semester">
		  <option>1st</option>
		  <option>2nd</option>
		  </select></div>';
	
	echo '<div class="form-group">
		  <label for="icon_name">Icon Name</label>
		  <input type="text" class="form-control" id="icon_name">
		</div>';
	$conn -> close();


?>
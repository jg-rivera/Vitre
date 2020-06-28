<?php 

include 'utility/TokenUtils.php';
include_once 'utility/SqlUtils.php';


// Proofread entries 

if (!isset($_POST['username']) || !isset($_POST['password'])) header("Location: /portal.php");;
if(empty($_POST['username']) || empty($_POST['password'])) header("Location: /portal.php");;


$username = $_POST['username'];
$password = $_POST['password'];

$conn = SqlUtils::connect();

echo "called";

$stmt = mysqli_prepare($conn, 'SELECT * FROM Students WHERE UserName = ?');
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

// If username exists
if($result->num_rows > 0) {
	while($row = $result->fetch_assoc()) {
  		$firstnames[] = $row['FirstName'];
		$lastnames[] = $row['LastName'];
  		$passwords[] = $row['UserKey'];
		$class_ids[] = $row['ClassID'];
		$student_ids[] = $row['StudentID'];
	}
}

$stmt->close();
$conn->close();

if (verifyPassword($_POST['password'], $passwords[0])) {
	generateToken();
	header("Location: /index.php");
} else {
	header("Location: /portal.php?s=f");
}

?>
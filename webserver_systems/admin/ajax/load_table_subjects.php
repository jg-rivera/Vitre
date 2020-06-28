<?php
require('../php/SqlUtils.php');

$conn = SqlUtils::connect();

$requestData = $_REQUEST;

$columns = array( 
// datatable column index  => database column name
	0 =>'SubjectID', 
	1 => 'SubjectName',
	2 => 'SubjectChunkKey',
	3 => 'TeacherName',
	4 => 'Semester',
	5 => 'TrackName'
);
// IFNULL(TeacherName, 'None') AS 
$selectReq = "SELECT SubjectID, SubjectName, SubjectChunkKey, IFNULL(TeacherName, 'None') AS TeacherName, Semester, TrackName ";
$originReq = " FROM Subjects LEFT JOIN Teachers on Subjects.InstructorID = Teachers.TeacherID ";
$originReq .= "INNER JOIN Tracks on Subjects.TrackID = Tracks.TrackID";

$req = $selectReq . $originReq;

$query = mysqli_query($conn, $req);
$totalData = mysqli_num_rows($query);
$totalFiltered = $totalData;

// Search function.
if( !empty($requestData['search']['value']) ) {
	$searchReq = $req;
	$searchReq .= " WHERE SubjectID LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR SubjectName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR SubjectChunkKey LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR TeacherName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR TrackName LIKE '" . $requestData['search']['value']."%' ";
	$query = mysqli_query($conn, $searchReq);
	$totalFiltered = mysqli_num_rows($query);
	$searchReq .= " ORDER BY ". $columns[$requestData['order'][0]['column']]." ". $requestData['order'][0]['dir'] . " LIMIT ".$requestData['start']. " ," . $requestData['length']. " ";
	$query = mysqli_query($conn, $searchReq);
} else {
	$searchReq = $req;
	$searchReq .= " ORDER BY ". $columns[$requestData['order'][0]['column']]." ". $requestData['order'][0]['dir'] . " LIMIT ".$requestData['start']. " ," . $requestData['length']. " ";
	$query = mysqli_query($conn, $searchReq);
}

$dataArray = array();
while ($row = $query->fetch_assoc()){
	$dataRow = array();
	$dataRow[0] = '#' . $row['SubjectID'];
	$dataRow[1] = $row['SubjectName'];
	$dataRow[2] = $row['SubjectChunkKey'];
	$dataRow[3] = $row['TeacherName'];
	$dataRow[4] = $row['Semester'];
	$dataRow[5] = $row['TrackName'];
	array_push($dataArray, $dataRow);
}

// Encode json
echo json_encode(
	array(
		'draw' => intval( $requestData['draw'] ),
		'recordsTotal'    => intval( $totalData ),
		'recordsFiltered' => intval( $totalFiltered ), 
		'data' => $dataArray
	)
);

$conn -> close();
?>

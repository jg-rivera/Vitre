<?php
require('../php/SqlUtils.php');

$conn = SqlUtils::connect();

$requestData = $_REQUEST;

$columns = array( 
// datatable column index  => database column name
	0 =>'StudentID', 
	1 => 'FirstName',
	2 => 'LastName',
	3 => 'ClassName'
);

$selectReq = "SELECT StudentID, LastName, FirstName, ClassName ";
$originReq = " FROM Students INNER JOIN Classes on Students.ClassID = Classes.ClassID ";
$req = $selectReq . $originReq;

$query = mysqli_query($conn, $req);
$totalData = mysqli_num_rows($query);
$totalFiltered = $totalData;

// Search function.
if( !empty($requestData['search']['value']) ) {
	$searchReq = $req;
	$searchReq .= " WHERE LastName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR FirstName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR ClassName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR StudentID LIKE '" . $requestData['search']['value']."%' ";
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
	$dataRow[0] = '#' . $row['StudentID'];
	$dataRow[1] = $row['FirstName'];
	$dataRow[2] = $row['LastName'];
	$dataRow[3] = $row['ClassName'];
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

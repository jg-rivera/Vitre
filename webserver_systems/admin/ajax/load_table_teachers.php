<?php
require('../php/SqlUtils.php');

$conn = SqlUtils::connect();

$requestData = $_REQUEST;

$columns = array( 
// datatable column index  => database column name
	0 =>'TeacherID', 
	1 => 'TeacherName',
	2 => 'TeacherChunkKey'
);

$selectReq = "SELECT TeacherID, TeacherName, TeacherChunkKey";
$originReq = " FROM Teachers ";

$req = $selectReq . $originReq;

$query = mysqli_query($conn, $req);
$totalData = mysqli_num_rows($query);
$totalFiltered = $totalData;

// Search function.
if( !empty($requestData['search']['value']) ) {
	$searchReq = $req;
	$searchReq .= " WHERE TeacherID LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR TeacherName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR TeacherChunkKey LIKE '" . $requestData['search']['value']."%' ";
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
	$dataRow[0] = '#' . $row['TeacherID'];
	$dataRow[1] = $row['TeacherName'];
	$dataRow[2] = $row['TeacherChunkKey'];
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

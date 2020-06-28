<?php
require_once('../php/SqlUtils.php');

$conn = SqlUtils::connect();

$requestData = $_REQUEST;

$columns = array( 
// datatable column index  => database column name
	0 =>'LastName', 
	1 => 'FirstName',
	2 => 'TimeIn',
	3 => 'TimeOut',
	4 => 'SessionTime'
);

$selectReq = "SELECT LastName, FirstName, TimeIn, TimeOut, SessionTime";
$originReq = " FROM Analytics INNER JOIN Students on Analytics.StudentID = Students.StudentID ";
$req = $selectReq . $originReq;

$query = mysqli_query($conn, $req);
$totalData = mysqli_num_rows($query);
$totalFiltered = $totalData;

// Search function.
if( !empty($requestData['search']['value']) ) {
	$searchReq = $req;
	$searchReq .= " WHERE LastName LIKE '" . $requestData['search']['value']."%' ";
	$searchReq .= " OR FirstName LIKE '" . $requestData['search']['value']."%' ";
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
	$dataRow[0] = $row['LastName'];
	$dataRow[1] = $row['FirstName'];
	
	$time_format = 'M d, Y h:i:s A';
	date_default_timezone_set("Asia/Manila");
	
	$timeIn = $row['TimeIn'];
	$timeOut = $row['TimeOut'];
	$sessionTime = $row['SessionTime'];
	
	$noData = 'No data';
	
	// Handle time in
	if (is_null($timeIn))
		$dataRow[2] = $noData;
	else
		$dataRow[2] = date($time_format, $timeIn);
	
	// Handle time out
	if (is_null($timeOut)) 
		$dataRow[3] = $noData;
	else
		$dataRow[3] = date($time_format, $timeOut);
	
	
	// Handle session time
	if (is_null($sessionTime)) 
		$dataRow[4] = $noData;
	else
		$dataRow[4] = gmdate("H:i:s", $sessionTime);
	
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

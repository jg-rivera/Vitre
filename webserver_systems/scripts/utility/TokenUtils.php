<?php

include_once 'JWT.php';
include_once 'SubjectManager.php';
include_once 'SqlUtils.php';

function verifyPassword($claimPassword, $realPasswordHash) {
	$claimHash =  hash('sha256', $claimPassword);
	return $claimHash == $realPasswordHash;
}

function generateToken($remember) {
	$secret_key = '5Unc3t_1uC1en@3$!1%4';
	
	// 1 minute -> 60 secs
	// 30 minutes
	$valid_for = '1800';
	$time = time();
	
	// Extend to 1 hour, if remember me is checked
	if ($remember)
		$valid_for = '3600';
	
	// Store session information
	$token = array();
	$token['username'] = $GLOBALS['username'];
	$token['firstname'] = $GLOBALS['firstnames'][0];
	$token['lastname'] = $GLOBALS['lastnames'][0];
	$token['class_id'] = $GLOBALS['class_ids'][0];
	$token['student_id'] = $GLOBALS['student_ids'][0];
	$token['time_in'] = $time;
	$token['exp'] = $time + $valid_for;
	$token['curriculum_id'] = SubjectManager::getCurriculumID($token['student_id']);
	$token['subject_count'] = SubjectManager::getSubjectsCount($token['student_id'], $token['curriculum_id']);
	
	$jwt = json_encode(array('token' => JWT::encode($token, $secret_key)));
	
	// Store token into session
	session_start();
	$_SESSION['token'] = $jwt;

	// Insert analytics entry	
	generateAnalytics($time);
}

function generateAnalytics($time) {
	$conn = SqlUtils::connect();
	
	$s_id = getToken() -> student_id;
	$stmt = mysqli_prepare($conn, 'INSERT INTO Analytics(StudentID, TimeIn) VALUES (?, ?);');
	$stmt -> bind_param("ss", $s_id, $time);
	$stmt -> execute();
	
	$conn -> close();
}

/**
	TODO: Admin function. Put in /admins/
**/
function loginCount($s_id) {
	$conn = SqlUtils::connect();

	$stmt = mysqli_prepare($conn, 'SELECT * FROM Analytics WHERE StudentID = ?);');
	$stmt = bind_param("s", $s_id);
	$stmt -> execute();
	
	$result = $stmt -> get_result();
	$num = mysqli_num_rows($result);
	$conn -> close();
	
	return $num;
}	

function updateLogout() {
	$conn = SqlUtils::connect();
	$logout_time = time();
	$login_time = getToken() -> time_in;
	$s_id = getToken() -> student_id;
	$session_time = $logout_time - $login_time;
	
	$stmt = mysqli_prepare($conn, 'UPDATE Analytics SET TimeOut = ?, SessionTime = ? WHERE StudentID = ? ORDER BY TimeIn DESC LIMIT 1');
	$stmt -> bind_param("sss", $logout_time, $session_time, $s_id);
	$stmt -> execute();
	
	$conn -> close();
}

function verifyToken() {
	session_start();
	if (isset($_SESSION['token'])) {
		$secret_key = '5Unc3t_1uC1en@3$!1%4';
		$sessionToken = $_SESSION['token'];
		$jwt = json_decode($sessionToken, true);

		try {
			$token = JWT::decode($jwt['token'], $secret_key);
		} catch (Exception $e) {
			echo 'Exception in decoding JWT: ' . $e;
			exit;
		}

		// If token is not expired
		if ($token -> exp >= time()) {
			return true;
		} else {
			return false;
		}
	}
	return false;
}

function getToken() {
	if (isset($_SESSION['token'])) {
		$secret_key = '5Unc3t_1uC1en@3$!1%4';
		$sessionToken = $_SESSION['token'];
		$jwt = json_decode($sessionToken, true);

		try {
			$token = JWT::decode($jwt['token'], $secret_key);
		} catch (Exception $e) {
			echo 'Exception in decoding JWT: ' . $e;
			exit;
		}
		
		return $token;
	}
	return NULL;
}

function checkToken() {
	if (!verifyToken()) {
		// Logout event
		session_start();
		updateLogout();
		
		$_SESSION = array();

		if (ini_get("session.use_cookies")) {
			$params = session_get_cookie_params();
			setcookie(session_name(), '', time() - 42000,
				$params["path"], $params["domain"],
				$params["secure"], $params["httponly"]
			);
		}

		session_destroy();
		header ('Location: /portal.php');
	}
}


function portalCheck() {
	if (verifyToken())
		header('Location: /index.php');
}
?>
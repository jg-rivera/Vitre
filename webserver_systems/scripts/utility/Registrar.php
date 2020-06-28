<?php

	class Registrar {
		
		public static function writeCache($signature, $payload) {
			$fileRoot = $_SERVER['DOCUMENT_ROOT'] . '/files/grade11/';
			$filePath = $fileRoot . $signature . '.cache';
			$fp = fopen($filePath, 'w');
			fwrite($fp, $payload);
			fclose($fp);
		}
		
		public static function updateStudent($password) {
			$password = hash('sha256', $password);
			$conn = SqlUtils::connect();
			$query = "UPDATE Students SET UserKey='$password' ";
			
			//TODO update password
		}
		
		/*
			Main registry function.
		*/
		public static function process($payloadRaw) {
			$json = json_decode($payloadRaw, true);
			$studentTree = $json['data'];
			
			$crown = $json['crown'];
			
			$studentCount = $crown['count'];
			$classID = $crown['id'];
			$className = $crown['name'];
			$classSig = $crown['sig'];
			
			$conn = SqlUtils::connect();
			$valueArray = array();
			
			for ($studentIndex = 1; $studentIndex <= $studentCount; $studentIndex++) {
				$student = $studentTree[$studentIndex];
				$ln = $student['ln'];
				$fn = $student['fn'];
				$username = $student['username'];
				$userkey = $student['userkey'];
				$gender = $student['gender'];
				
				$query = Registrar::builder($fn, $ln, $username, $userkey, $gender) . ',' . $studentIndex . ',' . $classID . ')';
				array_push($valueArray, $query);
			}
			 
			$values = implode(',', $valueArray);
			$query = 'INSERT INTO Students (FirstName, LastName, UserName, UserKey, Gender, StudentIndex, ClassID) VALUES ' . $values . ';';
			
			if (mysqli_query($conn, $query)) {
				echo "\nNew record created successfully";
			} else {
				echo "Error: " . $sql . "<br>" . mysqli_error($conn);
				return false;
			}
			
			$conn->close();
			return true;
		}
		
		private static function builder() {
			$query = '(';
			$args = func_get_args();
			$last = count($args) - 1;
			
			foreach ($args as $index => $value) {
				$query .= '\'' . $value . '\'';
				if ($index != $last) {
					$query .= ',';
				}
			}
			return $query;
		}
	}

?>
<?php

include 'JsonPatch.php';
include 'SqlUtils.php';

	class Patcher {
	
		static $replyInst = array(
			'added' => 0,
			'updated' => 0,
			'removed' => 0,
			'failure' => 0
		);
		
		/*
			Main function for processing payload and instruction data.
		*/
		public static function process($payloadRaw, $instructionSetRaw) {
			$delim_break = ';';
			$delim = ',';
			
			$instSet = array_filter(explode($delim_break, trim($instructionSetRaw)));
			$conn = SqlUtils::connect();
			
			// Process and retrieve contexts
			$payloadJson = json_decode ($payloadRaw, true);
			$crowns = $payloadJson['crown']['id'];
			$subjectID = $crowns['subject'];
			$classID = $crowns['class'];

			// Return context data with payload and Subject ID
			$contexts = Patcher::contexts($conn, $payloadJson, $subjectID);
			
			// Return student db ids with Class ID
			$studentDatabaseIDs = Patcher::studentIDs($conn, $classID);
			
			// Process instructions! 
			foreach ($instSet as $inst) {
				// Split instruction elements
				$instArr = explode($delim, $inst);
				
				$operation = $instArr[0];
				$componentIndex = $instArr[1];
				$itemName = $instArr[2];
				
				$itemTargetIndex = $instArr[4];
				$value = $instArr[5];
				
				// Acquire real student ID
				$studentTargetIndex = $instArr[3];
				$studentID = $studentDatabaseIDs[$studentTargetIndex];

				if (!array_key_exists($componentIndex, $contexts)) 
					break;
				
				$ctx = $contexts[$componentIndex][$itemTargetIndex];
				
				if ($ctx == NULL) {
					echo 'No patch context for ' . $componentIndex;
					break;
				}
				
				// Registers the student grade to the MySQL database.
				Patcher::grade($conn, $operation, $studentID, $ctx, $value);
			}
			
			//Patcher::addGradeReply('added', 1);
			
			$reply = array('grades' => Patcher::$replyInst);
			$replyJSON = json_encode($reply);
			
			
			echo $replyJSON;
			
			$conn->close();
			return true;
		}
		
		// Reply functions
		private static function addGradeReply($property, $value) {
			Patcher::$replyInst[$property] += $value;
		}
		
		private static function subGradeReply($property, $value) {
			Patcher::$replyInst[$property] -= $value;
		}
		
		private static function resetGradeReply($property) {
			Patcher::$replyInst[$property] = 0;
		}
		
		private static function contexts($conn, $payloadJson, $subjectID) {
			$contextTree = $payloadJson['metadata']['context'];
			$hashes = array_keys($contextTree);
			$results = array(array());
			
			// Verification of contexts
			foreach($hashes as $hash) {
				$context = $contextTree[$hash];
				$componentName = $context['component'];
				$itemIndex = $context['itemIndex'];
				$componentIndex = $context['componentIndex'];
				$stamp = $context['stamp'];
				$description = $context['description'];
				$hps = $context['cap'];
				
				// Check existence of context in DB
				$req = "SELECT 1 FROM GradesMetadata WHERE GradeMetaID='$hash';";
				$query = mysqli_query($conn, $req);
				
				// Empty guy
				if (mysqli_num_rows($query) < 1) {
					Patcher::addContext($conn, $hash, $subjectID, $componentName, $itemIndex, $componentIndex, $stamp, $description, $hps);
				} else { 
				// exists. Update context description.
					Patcher::updateContext($conn, $hash, $stamp, $description);
				}
				$results[$componentIndex][$itemIndex] = $hash;
			}
			
			return $results;
		}
		
		private static function addContext($conn, $hash, $subjectID, $componentName, $itemIndex, $componentIndex, $stamp, $description, $hps) {
			$req = "INSERT INTO GradesMetadata(GradeMetaID, SubjectID, Component, ComponentIndex, ItemIndex, UploadDate, Description, HighestPossibleScore) ";
			$req .= "VALUES ('$hash' , $subjectID , '$componentName', $componentIndex, $itemIndex, $stamp, '$description', $hps);";
			
			$query = mysqli_query($conn, $req);
			
			if ($query) {
				echo "Added PatchContext[$hash]: $description\n";
			} else {
				echo "Error: <br>" . mysqli_error($conn);
			}
		}
		
		private static function updateContext($conn, $hash, $stamp, $description) {
			$req = "UPDATE GradesMetadata SET Description='$description', UploadDate=$stamp WHERE GradeMetaID='$hash';";
			$query = mysqli_query($conn, $req);
			
			if ($query) {
				echo "Updated PatchContext[$hash]: $description\n";
			} else {
				echo "Error: <br>" . mysqli_error($conn);
			}
		}
		/**
			Main grading function.
		*/
		private static function grade($conn, $operation, $studentID, $ctx, $value) {
			$operation = strtoupper(trim($operation));
			$writtenWork = 0;
			$performanceTask = 1;
			$quarterlyAssessment = 2;
			$all = 3;
			
			switch($operation) {
				case "ADD":
					$query = Patcher::queryAdd($conn, $studentID, $ctx, $value);
					break;
				case "REM":
					// Removing grade data is rather controversial.
					break;
				case "SET":
					$query = Patcher::querySet($conn, $studentID, $ctx, $value);
					break;
				case "NONE":
					break;
			}
		}
		
		private static function studentIDS($conn, $classID) {
			$req = "SELECT StudentID, StudentIndex FROM Students WHERE ClassID=$classID;";
			$query = mysqli_query($conn, $req);
			$results = array();
			
			while ($row = $query->fetch_assoc()) {
				$results[$row['StudentIndex']] = $row['StudentID'];
            }			
			return $results;
		}
		
		private static function queryAdd($conn, $studentID, $contextHash, $value) {
			$query = "INSERT INTO Grades(StudentID, GradeMetaID, Grade) VALUES " . "($studentID, '$contextHash', $value);";
			if (mysqli_query($conn, $query)) {
				echo "ADD -> Added grade for $contextHash\n";
				Patcher::addGradeReply('added', 1);
				
				//flush();
			} else {
				echo "Error: <br>" . mysqli_error($conn);
				Patcher::addGradeReply('failure', 1);
			}
			return $query;
		}
		
		private static function querySet($conn, $studentID, $contextHash, $value) {
			$query = "UPDATE Grades SET Grade=$value WHERE StudentID=$studentID and GradeMetaID='$contextHash';";
			
			if (mysqli_query($conn, $query)) {
				echo "SET -> Updated grade for $contextHash\n";
				Patcher::addGradeReply('updated', 1);
			} else {
				echo "Error: <br>" . mysqli_error($conn);
				Patcher::addGradeReply('failure', 1);
			}
			
			return $query;
		}
		
		public static function quote($str) {
			return "'" . $str . "'";
		}
	}
?>
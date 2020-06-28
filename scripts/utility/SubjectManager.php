<?php
	include_once 'ItemFactory.php';
	include_once 'SqlUtils.php';
	include_once 'DateUtils.php';
	
	date_default_timezone_set('Asia/Manila');
	
	class SubjectManager {
		
		public static function constructSubjects() {
			$conn = SqlUtils::connect();
			
			$subjects = SubjectManager::getSubjectIDs($conn);
			
			foreach ($subjects as $s_id) {
				$sub = SubjectManager::processSubject($conn, $s_id);
				createSubjectCard(
					$s_id,
					$sub['name'], 
					$sub['teacher'],
					$sub['track'],
					$sub['semester'],
					$sub['icon']
				);
			}
			
			$conn->close();
		}
		
		public static function constructFeed() {
			$conn = SqlUtils::connect();
			
			$feed = SubjectManager::retrieveFeed($conn);
			
			if (!empty($feed)) {
				foreach($feed as $fd) {
					createFeed(
						$fd['s_id'],
						$fd['subject'],
						$fd['teacher'],
						$fd['comp'],
						$fd['desc'],
						time_elapsed_string('@' . (int)($fd['stamp'] / 1000)),
						$fd['grade'],
						$fd['hps']
					);
				}
			} else {
				createNoAct();
			}
			
			$conn -> close();
		}
		
		private static function retrieveFeed($conn) {
			$s_id = getToken() -> student_id;
			$req = "SELECT GradesMetadata.SubjectID, SubjectName, Honorific, TeacherName, Component, Grade, Description, HighestPossibleScore, UploadDate FROM Grades ";
			$req .= "INNER JOIN GradesMetadata on GradesMetadata.GradeMetaID = Grades.GradeMetaID ";
			$req .= "INNER JOIN Subjects on Subjects.SubjectID = GradesMetadata.SubjectID ";
			$req .= "INNER JOIN Teachers on Teachers.TeacherID = Subjects.InstructorID ";
			$req .= "WHERE StudentID=$s_id ORDER BY UploadDate DESC;";

			$query = mysqli_query($conn, $req);
			$feed = array();
			
			while ($row = $query->fetch_assoc()) {
				$fd = array();
				$fd['teacher'] = $row['Honorific'] . ' ' . $row['TeacherName'];
				$fd['s_id'] = (int) $row['SubjectID'];
				$fd['subject'] = $row['SubjectName'];
				$fd['grade'] = $row['Grade'];
				$fd['desc'] = $row['Description'];
				$fd['comp'] = $row['Component'];
				$fd['stamp'] = $row['UploadDate'];
				$fd['hps'] = $row['HighestPossibleScore'];
				array_push($feed, $fd);
			}
			
			return $feed;
		}
		
		public static function constructGrades($c_index, $s_id) {
			$conn = SqlUtils::connect();
			$grades = SubjectManager::retrieveGrades($conn, $c_index, $s_id);

			if (!empty($grades)) {
				foreach($grades as $gra) {
					$date = date("F d, Y", $gra['stamp'] / 1000);
					createContent(
						$gra['desc'],
						$date,
						$gra['grade'],
						$gra['hps']
					);
				}
			} else {
				createNoAct();
			}

			$conn -> close();
		}
		
		private static function retrieveGrades($conn, $c_index, $s_id) {
			$id = getToken() -> student_id;
			
			$req = "SELECT Grade, GradeID, StudentID, SubjectID, ComponentIndex, ItemIndex, ";
			$req .= "Component, Description, HighestPossibleScore, UploadDate ";
			$req .= "FROM Grades INNER JOIN GradesMetadata on Grades.GradeMetaID = GradesMetadata.GradeMetaID ";
			$req .= "WHERE ComponentIndex = $c_index and SubjectID = $s_id and StudentID = $id ORDER BY ItemIndex;";
			
			$query = mysqli_query($conn, $req);
			$grades = array();
			
			while ($row = $query->fetch_assoc()) {
				$gra = array();
				$gra['grade'] = $row['Grade'];
				$gra['g_id'] = $row['GradeID'];
				$gra['s_id'] = $row['SubjectID'];
				$gra['c_index'] = $row['ComponentIndex'];
				$gra['i_index'] = $row['ItemIndex'];
				$gra['c_name'] = $row['Component'];
				$gra['desc'] = $row['Description'];
				$gra['hps'] = $row['HighestPossibleScore'];
				$gra['stamp'] = $row['UploadDate'];
				array_push($grades, $gra);
			}
			
			return $grades;
		}
		
		public static function processSubject($conn, $s_id) {
			$req = "SELECT SubjectName, Honorific, TeacherName, TrackName, Semester, IconName, WWWeight, PTWeight, QAWeight FROM Subjects ";
			$req .= "INNER JOIN Teachers on Subjects.InstructorID = Teachers.TeacherID ";
			$req .= "INNER JOIN Tracks on Subjects.TrackID = Tracks.TrackID WHERE Subjects.SubjectID = $s_id;";
			
			$query = mysqli_query($conn, $req);
			$results = array();
			$icon_path = 'images/subjects/';
			
			if (!$query) exit;
			
			while ($row = $query->fetch_assoc()) {	
				$results['name'] = $row['SubjectName'];
				$results['teacher'] = $row['Honorific'] . ' ' . $row['TeacherName'];
				$results['track'] = $row['TrackName'];
				$results['semester'] = $row['Semester'] . ' Semester';
				$results['icon'] = $icon_path . $row['IconName'] . '.png';
				$results['ww_w'] = (int) $row['WWWeight'];
				$results['pt_w'] = (int) $row['PTWeight'];
				$results['qa_w'] = (int) $row['QAWeight'];
			}
			
			return $results;
		}

		public static function getSubjectIDs($conn) {
			$id = getToken() -> student_id;
			$curriculum_id = getToken() -> curriculum_id;
			
			$req = "SELECT SubjectID FROM Curricula WHERE CurriculumID = $curriculum_id;";
			
			$subjects = array();
			$query = mysqli_query($conn, $req);
			
			while ($row = $query->fetch_assoc()) {
				array_push($subjects, (int) $row['SubjectID']);
			}
			
			return $subjects;
		}
		
		public static function getTokenFriendlySubjectIDs($conn, $id, $curriculum_id) {
			$req = "SELECT SubjectID FROM Curricula WHERE CurriculumID = $curriculum_id;";
			
			$subjects = array();
			$query = mysqli_query($conn, $req);
			
			while ($row = $query->fetch_assoc()) {
				array_push($subjects, (int) $row['SubjectID']);
			}
			
			return $subjects;
		}
		
		public static function getCurriculumID($id) {
			$conn = SqlUtils::connect();
			
			$req = "SELECT Classes.CurriculumID FROM Students INNER JOIN Classes on Students.ClassID = Classes.ClassID ";
			$req .= "WHERE Students.StudentID = $id;";

			$query = mysqli_query($conn, $req);
			$curriculum_id = 0;
			
			while ($row = $query->fetch_assoc()) {
				$curriculum_id = $row['CurriculumID'];
            }
			
			$conn -> close();
			
			return $curriculum_id;
		}
		
		public static function getSubjectsCount($id, $curriculum_id) {
			$conn = SqlUtils::connect();
			$subjects = SubjectManager::getTokenFriendlySubjectIDs($conn, $id, $curriculum_id);
			$conn -> close();
			return count($subjects);
		}
	}
?>
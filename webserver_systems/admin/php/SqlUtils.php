<?php

class SqlUtils {
	
	public static function compoundInsert($prequery, $values) {
		$query = $prequery . ' VALUES ' . $values . ';';
		$conn = SqlUtils::connect();
		
		if (mysqli_query($conn, $query)) {
			echo "New record created successfully";
		} else {
			echo "Error: " . $sql . "<br>" . mysqli_error($conn);
		}
	}
	
	public static function connect() {
		$conn = mysqli_connect("localhost", "root", "usbw", "grade11");
		if (mysqli_connect_errno()) {
			die("Failed to connect to MySQL: " . mysqli_connect_error());
		}
		return $conn;
	}
	
	public static function implodeQuery() {
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

<?php
	class Cypher {
		
		 public static function payload($data) {
			$headerData = strtok($data, '.');
			$bodyData = substr($data, strlen($headerData) + 1);
			$key = $headerData[strlen($headerData) - 1];
			$contents = substr($headerData, 0, -1);
			$handshake = Cypher::decrypt($contents,  Cypher::truncate($key));
			$patch =  Cypher::decrypt($bodyData,  Cypher::truncate($handshake));
			return $patch;
		}
		
		public static function truncate($key) {
			if (strlen($key) < 16) {
				$pad = 16 - strlen($key);
				
				for ($i = 0; $i < $pad; $i++) {
					$key .= "0";
				}
				return $key;
			}
			
			if (strlen($key) > 16) {
				return substr($key, 0, 16);
			}
			return $key;
		}
		
		public static function encrypt($contents, $encryptKey){
			$iv = $encryptKey;
			$re = openssl_encrypt($contents, 'aes-128-ecb', $encryptKey, OPENSSL_RAW_DATA, $iv);
			$re = base64_encode($re);
			return $re;
		}
	
		public static function decrypt($contents, $encryptKey){
			$iv = $encryptKey;
			$contents = base64_decode($contents);
			$re = openssl_decrypt($contents, 'aes-128-ecb', $encryptKey, OPENSSL_RAW_DATA, $iv);
			return $re;
		}
	}
?>
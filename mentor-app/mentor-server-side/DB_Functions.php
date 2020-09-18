<?php
 


class DB_Functions {
	
	private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
	
	$utfstm = $this->conn->prepare("SET NAMES UTF8");
	$utfresult = $utfstm->execute();
	$utfstm->close();
    }
 
    // destructor
   
function __destruct() {

}

/**
* Storing new user
* returns user details
*/
public function storeUser($name, $email, $password) {
$uuid = uniqid('', true);
$hash = $this->hashSSHA($password);
$encrypted_password = $hash["encrypted"]; // encrypted password
$salt = $hash["salt"]; // salt

$stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
$stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
$result = $stmt->execute();
$stmt->close();

// check for successful store
if ($result) {
	$stmt = $this->conn->prepare("SELECT id
					,unique_id
					,name
					,email
					,encrypted_password
					,salt
					,created_at
					,updated_at
                                     FROM users WHERE email = ?");
	$stmt->bind_param("s", $email);
	$stmt->execute();
	
	$stmt->bind_result($id, $unique_id, $name, $email, $encrypted_password, $salt, $created_at, $updated_at);
	
	while ($stmt->fetch()) {
	$user["id"] = $id;
	$user["unique_id"] = $unique_id;
	$user["name"] = $name;
	$user["email"] = $email;
	$user["encrypted_password"] = $encrypted_password;
	$user["salt"] = $salt;
	$user["created_at"] = $created_at;
	$user["updated_at"] = $updated_at;
	}
	
	$stmt->close();
	
	return $user;
	} else {
	return false;
	}
}

/**
* Get user by email and password
*/
public function getUserByEmailAndPassword($email, $password) {

$stmt = $this->conn->prepare("SELECT us.id
									, us.unique_id
									, us.name
									, us.email
									, us.encrypted_password
									, us.salt
									, us.created_at
									, us.updated_at
									, us.seen_intro
									, COALESCE(pic.dbid_user_pic, -1) as dbid_user_pic
									, COALESCE(inf.user_type, 'guest') as user_type
									, COALESCE(inf.user_year, -1) as user_year
									, COALESCE(inf.user_degree_1, 0) as user_degree_1
									, COALESCE(inf.user_degree_2, 0) as user_degree_2
									, COALESCE(inf.user_degree_3, 0) as user_degree_3
								FROM users us
								LEFT JOIN users_pic pic
								ON (pic.user_unique_id = us.unique_id)
								LEFT JOIN users_info inf
								ON (inf.user_unique_id = us.unique_id)
								WHERE email = ?");
	
	$stmt->bind_param("s", $email);
	
	if ($stmt->execute()) {
	$stmt->bind_result($id
						, $unique_id
						, $name
						, $email
						, $encrypted_password
						, $salt
						, $created_at
						, $updated_at
						, $seen_intro
						, $dbid_user_pic
						, $user_type
						, $user_year
						, $user_degree_1
						, $user_degree_2
						, $user_degree_3);
	
	while ($stmt->fetch()) {
	$user["id"] = $id;
	$user["unique_id"] = $unique_id;
	$user["name"] = $name;
	$user["email"] = $email;
	$user["encrypted_password"] = $encrypted_password;
	$user["salt"] = $salt;
	$user["created_at"] = $created_at;
	$user["updated_at"] = $updated_at;
	$user["seen_intro"] = $seen_intro;
	$user["dbid_user_pic"] = $dbid_user_pic;
	$user["user_type"] = $user_type;
	$user["user_year"] = $user_year;
	$user["user_degree_1"] = $user_degree_1;
	$user["user_degree_2"] = $user_degree_2;
	$user["user_degree_3"] = $user_degree_3;
	}
	
	$stmt->close();
	return $user;
	} else {
	return NULL;
	}
}

/**
* Check user is existed or not
*/
public function isUserExisted($email) {
$stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");

$stmt->bind_param("s", $email);

$stmt->execute();

$stmt->store_result();

if ($stmt->num_rows > 0) {
// user existed 
$stmt->close();
return true;
} else {
// user not existed
$stmt->close();
return false;
}
}

/**
* Encrypting password
* @param password
* returns salt and encrypted password
*/
public function hashSSHA($password) {

$salt = sha1(rand());
$salt = substr($salt, 0, 10);
$encrypted = base64_encode(sha1($password . $salt, true) . $salt);
$hash = array("salt" => $salt, "encrypted" => $encrypted);
return $hash;
}

/**
* Decrypting password
* @param salt, password
* returns hash string
*/
public function checkhashSSHA($salt, $password) {

$hash = base64_encode(sha1($password . $salt, true) . $salt);

return $hash;
}

}

?>
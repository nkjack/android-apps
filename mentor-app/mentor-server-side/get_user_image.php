<?php
	require_once 'db_config.php';
	
	if($_SERVER['REQUEST_METHOD']=='GET'){
		$id = $_GET['id'];
		$sql = "SELECT * FROM users_pic WHERE dbid_user_pic = '$id'";
		

		// Connecting to mysql database
		$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE) or die(mysql_error());
		 
		$r = mysqli_query($con,$sql);
		 
		$result = mysqli_fetch_array($r);
		 
		header('Content-Type: image/jpeg');
		 
		echo base64_decode($result['profile_pic']);

		 
		mysqli_close($con);
		 
	}else{
		echo "Error";
	}

?>
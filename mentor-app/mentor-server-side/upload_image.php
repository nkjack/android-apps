<?php
	if($_SERVER['REQUEST_METHOD']=='POST'){
	 
		$image = $_POST['image'];
		$user_unique_id = $_POST['user_unique_id'];	 
		require_once 'db_config.php';

		// Connecting to mysql database
		$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE) or die(mysql_error());
		
		$query_check_image = "SELECT user_unique_id, profile_pic FROM users_pic 
								WHERE user_unique_id = '$user_unique_id'";
		$result_check_image = mysqli_query($con,$query_check_image) or die('Error quering users_pic');

		if (mysqli_num_rows($result_check_image ) < 1 )
		{
			$sql = "INSERT INTO users_pic (user_unique_id ,profile_pic) VALUES ('$user_unique_id', '$image')";
		}
		else{
			$sql = "UPDATE users_pic SET profile_pic = '$image' WHERE user_unique_id = '$user_unique_id'";
		}
	
		
		#$sql = "INSERT INTO users_pic (user_unique_id ,profile_pic) VALUES ('$user_unique_id', '$image')";
	 
		$stmt = mysqli_prepare($con,$sql);
	 
		#mysqli_stmt_bind_param($stmt,"s",$image);
		mysqli_stmt_execute($stmt);
	 
		$check = mysqli_stmt_affected_rows($stmt);
	 
		if($check == 1){
			echo "Image Uploaded Successfully";
		}else{
			echo "Error Uploading Image";
		}
		mysqli_close($con);
	}else{
		echo "Error";
	}
?>
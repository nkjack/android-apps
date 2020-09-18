<?php
 
require_once  'db_config.php';

// Connecting to mysql database
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE) or die(mysql_error());
 
// array for JSON response
$response = array();
 
if($con->connect_error){
 
	$response_con ="could not connect to the database";
 
die(print(json_encode($response_con)));}

// decoding the json array
$post = json_decode(file_get_contents("php://input"), true);

mysqli_query($con,"SET NAMES UTF8")or die('Error quering votes_table');
		
   
if (isset($post['uid_liked_degree']) && isset($post['user_uid'])&& isset($post['user_name'])) {
 
    // receiving the post params
    $uid_degree = $post['uid_liked_degree'];
    $user_uid = $post['user_uid'];	
	$user_name = $post['user_name'];	
	
	$query_check_for_likes ="SELECT * 
					FROM tb_degrees_user_likes 
					WHERE uid_liked_degree = '$uid_degree' 
							AND user_uid = '$user_uid'";
	
	$query_get_total_likes = "SELECT total_likes FROM tb_degrees_likes WHERE uid_liked_degree = '$uid_degree'";
	
	$result_get_total = mysqli_query($con,$query_get_total_likes) or die('Error quering uid_liked_degree');
	if (mysqli_num_rows($result_get_total) ==1)
	{
		$row = mysqli_fetch_array($result_get_total);
		$total_likes = $row["total_likes"];
		
		$result_check_likes = mysqli_query($con,$query_check_for_likes) or die('Error quering votes_table');
		
		if (mysqli_num_rows($result_check_likes) >= 1 ){
			// delete
			$query_delete_user_like = "DELETE FROM tb_degrees_user_likes 
										WHERE uid_liked_degree = '$uid_degree' AND user_uid = '$user_uid'";
			
			$result_delete_user_like = mysqli_query($con,$query_delete_user_like) or die('Error quering tb_degrees_user_likes');
			// check if row inserted or not
			if ($result_delete_user_like) {
				// successfully user like deleted
				$total_likes = $total_likes - 1;
				$query_update_like = "UPDATE tb_degrees_likes 
											SET total_likes = '$total_likes' 
											WHERE uid_liked_degree = '$uid_degree'";
				
				$result_update_total_likes = mysqli_query($con, $query_update_like) or die('Error quering tb_degrees_likes');
				
				if ($result_update_total_likes){
					$response["success"] = 1;
					$response["message"] = "User like successfully deleted.";
				}
				else{
					$response["success"] = 0;
					$response["message"] = "User like deleted but total_likes didn't update.";
				}
				// echoing JSON response
				echo json_encode($response);
			}
			else{
				//delete user like problem
				$response["success"] = 0;
				$response["message"] = "Delete user like problem";
 
				// echoing JSON response
				echo json_encode($response);
			}
		}
		else{
			//case numrows = 0
			//insert
			$query_insert_user_like = "INSERT INTO tb_degrees_user_likes(user_uid, user_name, uid_liked_degree) 
										VALUES('$user_uid', '$user_name','$uid_degree')";
			
			$result_insert_user_like = mysqli_query($con,$query_insert_user_like) or die('Error quering tb_degrees_user_likes');
			// check if row inserted or not
			if ($result_insert_user_like) {
				// successfully user like deleted
				$total_likes = $total_likes + 1;
				$query_update_like = "UPDATE tb_degrees_likes 
											SET total_likes = '$total_likes' 
											WHERE uid_liked_degree = '$uid_degree'";
				
				$result_update_total_likes = mysqli_query($con, $query_update_like) or die('Error quering tb_degrees_likes');
				
				if ($result_update_total_likes){
					$response["success"] = 1;
					$response["message"] = "User like successfully inserted.";
				}
				else{
					$response["success"] = 0;
					$response["message"] = "User like insert but total_likes didn't update.";
				}
				// echoing JSON response
				echo json_encode($response);
			}
			else{
				//user like problem inserted
				$response["success"] = 0;
				$response["message"] = "Insert user like problem";
 
				// echoing JSON response
				echo json_encode($response);
			}
		}	
	}
	else{
		//To many rows in tb_degrees_likes cause duplicate
		$response["success"] = 0;
		$response["message"] = "To many rows in tb_degrees_likes cause duplicate";
	 
		// echoing JSON response
		echo json_encode($response);
	}
}
else 
{
     // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}

 
mysqli_close($con);
 
  
 
?>
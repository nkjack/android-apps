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
		
   
if (isset($post['uid_followed_degree']) && isset($post['user_uid'])&& isset($post['user_name'])) {
 
    // receiving the post params
    $uid_degree = $post['uid_followed_degree'];
    $user_uid = $post['user_uid'];	
	$user_name = $post['user_name'];	
	
	$query_check_for_follows ="SELECT * 
					FROM tb_degrees_user_follows 
					WHERE uid_followed_degree = '$uid_degree' 
							AND user_uid = '$user_uid'";
	
	$query_get_total_follows = "SELECT total_follows FROM tb_degrees_follows WHERE uid_followed_degree = '$uid_degree'";
	
	$result_get_total = mysqli_query($con,$query_get_total_follows) or die('Error quering uid_liked_degree');
	if (mysqli_num_rows($result_get_total) ==1)
	{
		$row = mysqli_fetch_array($result_get_total);
		$total_follows = $row["total_follows"];
		
		$result_check_follows = mysqli_query($con,$query_check_for_follows) or die('Error quering votes_table');
		
		if (mysqli_num_rows($result_check_follows) >= 1 ){
			// delete
			$query_delete_user_follow = "DELETE FROM tb_degrees_user_follows 
										WHERE uid_followed_degree = '$uid_degree' AND user_uid = '$user_uid'";
			
			$result_delete_user_follow = mysqli_query($con,$query_delete_user_follow) or die('Error quering tb_degrees_user_likes');
			// check if row inserted or not
			if ($result_delete_user_follow) {
				// successfully user like deleted
				$total_follows = $total_follows - 1;
				$query_update_follow = "UPDATE tb_degrees_follows 
											SET total_follows = '$total_follows' 
											WHERE uid_followed_degree = '$uid_degree'";
				
				$result_update_total_follows = mysqli_query($con, $query_update_follow) or die('Error quering tb_degrees_likes');
				
				if ($result_update_total_follows){
					$response["success"] = 1;
					$response["message"] = "User follow successfully deleted.";
				}
				else{
					$response["success"] = 0;
					$response["message"] = "User follow deleted but total_follows didn't update.";
				}
				// echoing JSON response
				echo json_encode($response);
			}
			else{
				//delete user follow problem
				$response["success"] = 0;
				$response["message"] = "Delete user follow problem";
 
				// echoing JSON response
				echo json_encode($response);
			}
		}
		else{
			//case numrows = 0
			//insert
			$query_insert_user_follow = "INSERT INTO tb_degrees_user_follows(user_uid, user_name, uid_followed_degree) 
										VALUES('$user_uid', '$user_name','$uid_degree')";
			
			$result_insert_user_follow = mysqli_query($con,$query_insert_user_follow) or die('Error quering tb_degrees_user_likes');
			// check if row inserted or not
			if ($result_insert_user_follow) {
				// successfully user follow deleted
				$total_follows = $total_follows + 1;
				$query_update_follow = "UPDATE tb_degrees_follows 
											SET total_follows = '$total_follows' 
											WHERE uid_followed_degree = '$uid_degree'";
				
				$result_update_total_follows = mysqli_query($con, $query_update_follow) or die('Error quering tb_degrees_likes');
				
				if ($result_update_total_follows){
					$response["success"] = 1;
					$response["message"] = "User follow successfully inserted.";
				}
				else{
					$response["success"] = 0;
					$response["message"] = "User follow insert but total_likes didn't update.";
				}
				// echoing JSON response
				echo json_encode($response);
			}
			else{
				//user follow problem inserted
				$response["success"] = 0;
				$response["message"] = "Insert user follow problem";
 
				// echoing JSON response
				echo json_encode($response);
			}
		}	
	}
	else{
		//To many rows in tb_degrees_follows cause duplicate
		$response["success"] = 0;
		$response["message"] = "To many rows in tb_degrees_follows cause duplicate";
	 
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
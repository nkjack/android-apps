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
		
   
if (isset($post['user_unique_id']) && isset($post['user_type'])&& isset($post['user_year'])
	&& isset($post['user_degree_1'])&& isset($post['user_degree_2'])&& isset($post['user_degree_3'])) {
 
    // receiving the post params
    $user_unique_id = $post['user_unique_id'];
    $user_type = $post['user_type'];	
	$user_year = $post['user_year'];	
	$user_degree_1 = $post['user_degree_1'];
	$user_degree_2 = $post['user_degree_2'];
	$user_degree_3 = $post['user_degree_3'];

	

	$query_check_for_user = "SELECT * FROM users_info 
							WHERE user_unique_id = '$user_unique_id'";
	
	$result_check_user = mysqli_query($con,$query_check_for_user) or die('Error quering uid_liked_degree');
	if (mysqli_num_rows($result_check_user) < 1 )
	{
		
		$query_insert_user = "INSERT INTO users_info(user_unique_id, user_type, user_year, user_degree_1, user_degree_2, user_degree_3 ) 
							VALUES('$user_unique_id', '$user_type', '$user_year', '$user_degree_1', '$user_degree_2', '$user_degree_3')";
		
		$query_seen_intro = "UPDATE users SET seen_intro = 'yes' WHERE unique_id = '$user_unique_id'";
		
		$result_insert_user = mysqli_query($con,$query_insert_user) or die('Error quering votes_table');
		$result_seen_intro = mysqli_query($con,$query_seen_intro) or die('Error quering votes_table');
		
		if ($result_insert_user && $result_seen_intro){
			//success insert comment
			
			$response["success"] = 1;
			$response["message"] = "User Extra successfully inserted.";
			
		}
		else{
			//not success insert vote
			$response["success"] = 0;
			$response["message"] = "User Extra unsuccessfully inserted.";
			
		}	
		 
		// echoing JSON response
		echo json_encode($response);	
	}
	else{
		$query_insert_user = "UPDATE users_info SET user_type = '$user_type',
													user_year = '$user_year',
													user_degree_1 = '$user_degree_1',
													user_degree_2 = '$user_degree_2',
													user_degree_3 = $user_degree_3 
							WHERE user_unique_id = '$user_unique_id'";
		
		$query_seen_intro = "UPDATE users SET seen_intro = 'yes' WHERE unique_id = '$user_unique_id'";
		
		$result_insert_user = mysqli_query($con,$query_insert_user) or die('Error quering votes_table');
		$result_seen_intro = mysqli_query($con,$query_seen_intro) or die('Error quering votes_table');
		
		if ($result_insert_user && $result_seen_intro){
			//success insert comment
			
			$response["success"] = 1;
			$response["message"] = "User Extra successfully inserted.";
			
		}
		else{
			//not success insert vote
			$response["success"] = 0;
			$response["message"] = "User Extra unsuccessfully inserted.";
			
		}	
		 
		// echoing JSON response
		echo json_encode($response);
		
	}
	
}
else{
	 // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);	
}
	
 
mysqli_close($con);
 
  
 
?>
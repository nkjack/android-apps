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
		
   
if (isset($post['uid_post']) && isset($post['user_id'])&& isset($post['user_name'])
	&& isset($post['body_content'])) {
 
    // receiving the post params
    $uid_post = $post['uid_post'];
    $user_id = $post['user_id'];	
	$user_name = $post['user_name'];	
	$body_content = $post['body_content'];
	

	$query_check_for_post = "SELECT * FROM tb_posts 
							WHERE dbid_post = '$uid_post'";
	
	$result_check_post = mysqli_query($con,$query_check_for_post) or die('Error quering uid_liked_degree');
	if (mysqli_num_rows($result_check_post) < 1 )
	{
		//Error -- Post does not exist
		$response["success"] = 0;
		$response["message"] = "No Such Post";
 
		// echoing JSON response
		echo json_encode($response);	
	}
	else{
		$tz_object = new DateTimeZone('Asia/Jerusalem');
		//date_default_timezone_set('Asia/Jerusalem');

		$datetime = new DateTime();
		$datetime->setTimezone($tz_object);
		$time_now = $datetime->format('Y\-m\-d\ H:i:s');
		//update
		$query_insert_comment = "INSERT INTO tb_comments_posts(uid_post, user_id, user_name, body_content, created_at) 
							VALUES('$uid_post', '$user_id', '$user_name', '$body_content', '$time_now')";
		
		$result_insert_comment = mysqli_query($con,$query_insert_comment) or die('Error quering votes_table');
		if ($result_insert_comment){
			//success insert comment
			
			$response["success"] = 1;
			$response["message"] = "User comment successfully inserted.";
			
		}
		else{
			//not success insert vote
			$response["success"] = 0;
			$response["message"] = "User comment unsuccessfully inserted.";
			
		}	
		
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
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

// getting the information from the array
// in the android example I've defined only one KEY. You can add more KEYS to your app
if (isset($post['dbid_post']))
{
	$dbid_post = $post['dbid_post'];

	//echo json_encode($fb_user_id);
	//echo json_encode($amount_to_fetch);
	//echo json_encode($from_row);

	// the "params1" is from the map.put("param1", "example"); in the android code
	// if you make a "echo $my_value;" it will return a STRING value "example"
	   
	$query_all = "SELECT cp.dbid_comment
						, cp.uid_post
						, cp.user_id
						, cp.user_name
						, cp.body_content
						, cp.created_at
						, COALESCE(usp.dbid_user_pic, -1) as dbid_user_pic 
					FROM tb_comments_posts cp
					LEFT JOIN users_pic usp		ON (usp.user_unique_id = cp.user_id)
					WHERE uid_post = '$dbid_post'
					ORDER BY created_at asc";


			
	$result = mysqli_query($con,$query_all) or die('Error quering votes_table');
	// check for empty result
	if (mysqli_num_rows($result) > 0) {
		// looping through all results
		// votes node
		$response["comments"] = array();
		while($row = mysqli_fetch_array($result)){
			// temp user array
			$comment_card = array();
			$comment_card["dbid_comment"] = $row["dbid_comment"];
			$comment_card["uid_post"] = $row["uid_post"];
			$comment_card["user_id"] = $row["user_id"];
			$comment_card["user_name"] = $row["user_name"];
			$comment_card["body_content"] = $row["body_content"];
			$comment_card["created_at"] = $row["created_at"];
			$comment_card["dbid_user_pic"] = $row["dbid_user_pic"];

			
			// push single product into final response array
			array_push($response["comments"], $comment_card);
		}
		// success
		$response["success"] = 1;
	 
		// echoing JSON response
		echo json_encode($response);
	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No products found";
	 
		// echo no users JSON
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
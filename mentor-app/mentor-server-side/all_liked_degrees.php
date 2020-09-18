
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
if (isset($post['unique_id']))
{

	$unique_id = $post['unique_id'];

	   
	$query_all = "SELECT DISTINCT uk.user_uid, uk.uid_liked_degree, lk.total_likes
				FROM tb_degrees_user_likes uk
				JOIN tb_degrees_likes lk
				ON uk.uid_liked_degree = lk.uid_liked_degree
				WHERE uk.user_uid = '$unique_id'
				ORDER BY lk.total_likes DESC";


			
	$result = mysqli_query($con,$query_all) or die('Error quering votes_table');
	// check for empty result
	if (mysqli_num_rows($result) > 0) {
		// looping through all results
		// votes node
		$response["liked_degrees"] = array();
		while($row = mysqli_fetch_array($result)){
			// temp user array
			$follow_card = array();
			$follow_card["uid_liked_degree"] = $row["uid_liked_degree"];
			$follow_card["total_likes"] = $row["total_likes"];
			

			// push single product into final response array
			array_push($response["liked_degrees"], $follow_card);
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
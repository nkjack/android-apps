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

// getting the information from the array
// in the android example I've defined only one KEY. You can add more KEYS to your app


//$fb_user_id= $post['fb_user_id'];
//$amount_to_fetch= intval($post['amount_to_fetch']);
//$from_row= intval($post['from_row']);


//echo json_encode($fb_user_id);
//echo json_encode($amount_to_fetch);
//echo json_encode($from_row);

// the "params1" is from the map.put("param1", "example"); in the android code
// if you make a "echo $my_value;" it will return a STRING value "example"
   


$query_all ="Select * 
		from tb_subjects
		order by dbid_subject ASC";

mysqli_query($con,"SET NAMES UTF8")or die('Error quering votes_table');
		
$result = mysqli_query($con,$query_all) or die('Error quering votes_table');
// check for empty result
if (mysqli_num_rows($result) > 0) {
	// looping through all results
    // degrees node
    $response["subjects"] = array();
	while($row = mysqli_fetch_array($result)){
		// temp user array
        $vote = array();
        $vote["dbid_subject"] = $row["dbid_subject"];
        $vote["subject_name"] = $row["subject_name"];
		

        // push single product into final response array
        array_push($response["subjects"], $vote);
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

 
mysqli_close($con);
 
  
 
?>
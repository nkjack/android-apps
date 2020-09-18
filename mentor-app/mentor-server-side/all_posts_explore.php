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
if (isset($post['amount_to_fetch'])&&isset($post['from_row'])&&isset($post['unique_id']))
{

	$amount_to_fetch= intval($post['amount_to_fetch']);
	$from_row= intval($post['from_row']);
	$unique_id = $post['unique_id'];

	//echo json_encode($fb_user_id);
	//echo json_encode($amount_to_fetch);
	//echo json_encode($from_row);

	// the "params1" is from the map.put("param1", "example"); in the android code
	// if you make a "echo $my_value;" it will return a STRING value "example"
	   
	$query_all = "SELECT DISTINCT sub3.dbid_post,
						sub3.degree_id,
						sub3.degree_name,
						sub3.faculty_hebrew as faculty_name,
						sub3.name_hebrew as school_name,
						sub3.user_create_id,
						COALESCE(sub3.dbid_user_pic, -1) as dbid_user_pic,
						sub3.created_at,
						sub3.body_content,
						sub3.rate,
						sub3.total_score,
						sub3.name,
						sub3.type_post,
						sub3.vote_choice,
						COALESCE(count(distinct sub3.dbid_comment),0) as counter_comments
						FROM
						(
							select sub2.*
									, ps.dbid_comment 
									, coalesce(vs.vote_decision, 0) as vote_choice
									,usp.dbid_user_pic
							FROM
							(
								SELECT ps.dbid_post,
										ps.degree_id, 
										dg.degree_name, 
										fc.faculty_hebrew, 
										sc.name_hebrew, 
										ps.user_create_id, 
										ps.created_at, 
										ps.body_content, 
										ps.rate, 
										COALESCE( pv.total_score, 0 ) AS total_score, 
										us.name, 
										ps.type_post
								FROM tb_posts ps
								LEFT JOIN tb_posts_votes pv ON ps.dbid_post = pv.uid_post_voted
								LEFT JOIN users us ON us.unique_id = ps.user_create_id
								JOIN tb_degrees dg ON dg.dbid_degree = ps.degree_id
								JOIN tb_faculties fc ON fc.dbid_faculty = dg.faculty_id
								JOIN tb_schools sc ON sc.dbid_school = fc.school_id

							) sub2
							LEFT JOIN tb_comments_posts ps
							ON ps.uid_post = sub2.dbid_post
							LEFT JOIN tb_posts_votes_user vs
							ON (vs.uid_user = '$unique_id' AND
								vs.uid_post = sub2.dbid_post)
							LEFT JOIN users_pic usp				ON (usp.user_unique_id = sub2.user_create_id)
						) sub3
						group by sub3.dbid_post,
						sub3.degree_id,
						sub3.degree_name,
						sub3.faculty_hebrew,
						sub3.name_hebrew,
						sub3.user_create_id,
						sub3.dbid_user_pic,
						sub3.created_at,
						sub3.body_content,
						sub3.rate,
						sub3.total_score,
						sub3.name,
						sub3.type_post,
						sub3.vote_choice
						order by created_at desc
					LIMIT $amount_to_fetch OFFSET $from_row";


			
	$result = mysqli_query($con,$query_all) or die('Error quering votes_table');
	// check for empty result
	if (mysqli_num_rows($result) > 0) {
		// looping through all results
		// votes node
		$response["posts"] = array();
		while($row = mysqli_fetch_array($result)){
			// temp user array
			$post_card = array();
			$post_card["dbid_post"] = $row["dbid_post"];
			$post_card["degree_id"] = $row["degree_id"];
			$post_card["user_create_id"] = $row["user_create_id"];
			$post_card["dbid_user_pic"] = $row["dbid_user_pic"];
			$post_card["created_at"] = $row["created_at"];
			$post_card["body_content"] = $row["body_content"];
			$post_card["rate"] = $row["rate"];
			$post_card["total_score"] = $row["total_score"];
			$post_card["name"] = $row["name"];
			$post_card["counter_comments"] = $row["counter_comments"];
			$post_card["type_post"] = $row["type_post"];
			$post_card["vote_choice"] = $row["vote_choice"];
			
			$post_card["degree_name"] = $row["degree_name"];
			$post_card["faculty_name"] = $row["faculty_name"];
			$post_card["school_name"] = $row["school_name"];


			// push single product into final response array
			array_push($response["posts"], $post_card);
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
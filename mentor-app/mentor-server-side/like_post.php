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
		
   
if (isset($post['uid_user']) && isset($post['user_name'])&& isset($post['uid_post'])&& 
	isset($post['vote_decision'])) {
 
    // receiving the post params
    $uid_user = $post['uid_user'];
    $user_name = $post['user_name'];	
	$uid_post = $post['uid_post'];	
	$vote_decision = intval($post['vote_decision']);	

	$query_check_for_likes ="SELECT * 
					FROM tb_posts_votes_user 
					WHERE uid_user = '$uid_user' 
							AND uid_post = '$uid_post'";
	
	$query_get_total_score = "SELECT total_score FROM tb_posts_votes WHERE uid_post_voted = '$uid_post'";
	
	$result_get_total = mysqli_query($con,$query_get_total_score) or die('Error quering uid_liked_degree');
	if (mysqli_num_rows($result_get_total) == 1)
	{
		$row = mysqli_fetch_array($result_get_total);
		$total_score = $row["total_score"];
		
		$result_check_likes = mysqli_query($con,$query_check_for_likes) or die('Error quering votes_table');
		
		if (mysqli_num_rows($result_check_likes) >= 1 ){
			
			$row_decision = mysqli_fetch_array($result_check_likes);
			$prior_decision = $row_decision["vote_decision"];
			
			if ($prior_decision == $vote_decision){
				// delete
				$query_delete_user_like = "DELETE FROM tb_posts_votes_user 
											WHERE uid_user = '$uid_user' 
													AND uid_post = '$uid_post'";
				
				$result_delete_user_like = mysqli_query($con,$query_delete_user_like) or die('Error quering tb_posts_votes_user');
				// check if row inserted or not
				if ($result_delete_user_like) {
					// successfully user like deleted
					$total_score = $total_score - $vote_decision;
					$query_update_like = "UPDATE tb_posts_votes 
												SET total_score = '$total_score' 
												WHERE uid_post_voted = '$uid_post'";
					
					$result_update_total_likes = mysqli_query($con, $query_update_like) or die('Error quering tb_degrees_likes');
					
					if ($result_update_total_likes){
						$response["success"] = 1;
						$response["message"] = "User voted successfully.";
					}
					else{
						$response["success"] = 0;
						$response["message"] = "User vote deleted but total_score didn't update.";
					}
					// echoing JSON response
					echo json_encode($response);
				}
				else{
					//delete user like problem
					$response["success"] = 0;
					$response["message"] = "Delete user vote, problem";
	 
					// echoing JSON response
					echo json_encode($response);
				}
			}
			else{
				// INSERT
				$query_delete_user_like = "DELETE FROM tb_posts_votes_user 
											WHERE uid_user = '$uid_user' 
													AND uid_post = '$uid_post'";
				
				$result_delete_user_like = mysqli_query($con,$query_delete_user_like) or die('Error quering tb_posts_votes_user');
				// check if row inserted or not
				if ($result_delete_user_like) {
					// successfully user vote deleted
					//insert
					$query_insert_user_vote = "INSERT INTO tb_posts_votes_user(uid_user, user_name, uid_post, vote_decision) 
										VALUES('$uid_user', '$user_name','$uid_post', '$vote_decision')";
										
					$result_insert_user_vote = mysqli_query($con,$query_insert_user_vote) or die('Error inserting tb_posts_votes_user');
					
					if ($result_insert_user_vote){
						//decision successfully inserted
						$total_score = $total_score + $vote_decision - $prior_decision;
						$query_update_votes = "UPDATE tb_posts_votes 
													SET total_score = '$total_score' 
													WHERE uid_post_voted = '$uid_post'";
						
						$result_update_total_votes = mysqli_query($con, $query_update_votes) or die('Error quering tb_posts_votes');
						
						if ($result_update_total_votes){
							$response["success"] = 1;
							$response["message"] = "User voted successfully.";
						}
						else{
							$response["success"] = 0;
							$response["message"] = "User vote inserted but total_score didn't update.";
						}
						// echoing JSON response
						echo json_encode($response);
					}
					else{
						//decision successfully inserted
						$response["success"] = 0;
						$response["message"] = "User vote deleted, but unsuccessfully inserted";
		 
						// echoing JSON response
						echo json_encode($response);
					}		
				}
				else{
					//delete user like problem
					$response["success"] = 0;
					$response["message"] = "Delete user vote, problem";
	 
					// echoing JSON response
					echo json_encode($response);
				}
				
			}
			
			
		}
		else{
			//case numrows = 0
			//insert
			$query_insert_user_vote = "INSERT INTO tb_posts_votes_user(uid_user, user_name, uid_post, vote_decision) 
										VALUES('$uid_user', '$user_name','$uid_post', '$vote_decision')";
			
			$result_insert_user_vote = mysqli_query($con,$query_insert_user_vote) or die('Error quering tb_degrees_user_likes');
			// check if row inserted or not
			if ($result_insert_user_vote) {
				//decision successfully inserted
				$total_score = $total_score + $vote_decision;
				$query_update_votes = "UPDATE tb_posts_votes 
											SET total_score = '$total_score' 
											WHERE uid_post_voted = '$uid_post'";
				
				$result_update_total_votes = mysqli_query($con, $query_update_votes) or die('Error quering tb_posts_votes');
				
				if ($result_update_total_votes){
					$response["success"] = 1;
					$response["message"] = "User voted successfully.";
				}
				else{
					$response["success"] = 0;
					$response["message"] = "User vote inserted but total_score didn't update. result_check_likes == 0";
				}
				// echoing JSON response
				echo json_encode($response);
			}
			else{
				//decision successfully inserted
				$response["success"] = 0;
				$response["message"] = "Unsuccessfully inserted user vote at all";
 
				// echoing JSON response
				echo json_encode($response);
			}	
		}	

	}
	else{
		//To many rows in tb_posts_votes cause duplicate
		$response["success"] = 0;
		$response["message"] = "To many rows in tb_posts_votes cause duplicate";
	 
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
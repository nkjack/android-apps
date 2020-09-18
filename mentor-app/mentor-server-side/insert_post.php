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
		
   
if (isset($post['degree_id']) && isset($post['user_create_id'])&& isset($post['body_content'])&& isset($post['rate'])&& isset($post['type_post'])) {
 
    // receiving the post params
    $degree_id = $post['degree_id'];
    $user_create_id = $post['user_create_id'];	
	$body_content = $post['body_content'];	
	$rate = $post['rate'];
	$type_post = $post['type_post'];
	
	if($type_post == 'rate' || $type_post == 'qa')
	{
		$query_check_for_rate = "SELECT * FROM tb_posts 
								WHERE degree_id = '$degree_id' 
										AND user_create_id = '$user_create_id'
										AND type_post = '$type_post'";
		
		$result_check_rate = mysqli_query($con,$query_check_for_rate) or die('Error quering uid_liked_degree');
		if (mysqli_num_rows($result_check_rate) > 0 && $type_post == 'rate')
		{
			//Error -- Already rated need to update
			$response["success"] = 0;
			$response["message"] = "Already rated need to update";
							
		}
		else{
			$tz_object = new DateTimeZone('Asia/Jerusalem');
			//date_default_timezone_set('Asia/Jerusalem');

			$datetime = new DateTime();
			$datetime->setTimezone($tz_object);
			$time_now = $datetime->format('Y\-m\-d\ H:i:s');
			
			//update
			$query_insert_post = "INSERT INTO tb_posts(degree_id, user_create_id, created_at, body_content, rate, type_post) 
								VALUES('$degree_id', '$user_create_id', '$time_now', '$body_content', '$rate', '$type_post')";
			
			$result_insert_post = mysqli_query($con,$query_insert_post) or die('Error quering votes_table');
			if ($result_insert_post){
				//success insert comment
				$query_get_post_id = "SELECT dbid_post 
									FROM tb_posts 
									WHERE degree_id = '$degree_id' AND user_create_id = '$user_create_id' AND type_post = '$type_post'
									ORDER BY created_at DESC
									LIMIT 1";
				
				$result_get_post_id = mysqli_query($con,$query_get_post_id) or die('Error quering votes_table');
				if(mysqli_num_rows($result_get_post_id) == 1){
					$row = mysqli_fetch_array($result_get_post_id); //result_get_total
					$dbid_post = $row["dbid_post"];
					
					$query_insert_post_vote = "INSERT INTO tb_posts_votes(uid_post_voted, total_score, post_rate, post_type) 
										VALUES('$dbid_post', 0, '$rate', '$type_post')";
					
					$query_get_total_posts = " SELECT total_posts 
												FROM tb_degree_posts_summer
												WHERE degree_id = '$degree_id'";
					
										
					$result_insert_post_vote = mysqli_query($con,$query_insert_post_vote) or die('Error quering votes_table');
					if ($result_insert_post_vote){
						
						$result_get_total_posts = mysqli_query($con,$query_get_total_posts) or die('Error quering votes_table');
						if (mysqli_num_rows($result_get_total_posts) ==1){
							
							$row = mysqli_fetch_array($result_get_total_posts);
							$total_posts = $row["total_posts"];
							$total_posts = $total_posts + 1;
							
							$query_insert_total_posts = "UPDATE tb_degree_posts_summer 
												SET total_posts = '$total_posts'
												WHERE degree_id = '$degree_id'";
												
							$result_insert_total_posts = mysqli_query($con,$query_insert_total_posts) or die('Error quering votes_table');
							
							if ($result_insert_total_posts){
								// success insert
								$response["success"] = 1;
								$response["message"] = "User Post successfully inserted, include user vote, and total_posts.";
						
							}
							else{
								// success insert, error total_posts
								$response["success"] = 0;
								$response["message"] = "User Post successfully inserted, include user vote, error total_posts.";
						
							}

						}
						
						else{
							// success insert
							$response["success"] = 0;
							$response["message"] = "User Post successfully inserted, include user vote and can't total posts.";
							
						}
					}
					else{
						//not success insert vote
						$response["success"] = 0;
						$response["message"] = "User Post successfully inserted, exclude user vote.";
						
					}
					
				}
				else{
					//too many - error
					// not success insert vote
					$response["success"] = 0;
					$response["message"] = "User Post successfully inserted but couldn't fetch post id, means no user vote inserted and total posts";
					
				}
				
			}	
			else
			{
				//too many - error
				// not success insert vote
				$response["success"] = 0;
				$response["message"] = "User post not inserted, means nothing worked";
					
				
			}
		}	
		echo json_encode($response);
			
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Error, not qa or rate post";
				
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
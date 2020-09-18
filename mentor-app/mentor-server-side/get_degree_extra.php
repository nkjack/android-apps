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


if (isset($post['dbid_degree'])&&isset($post['unique_id']))
{
	$dbid_degree = $post['dbid_degree'];
	$unique_id = $post['unique_id'];
	
	$query_score_rate = "SELECT tp.degree_id ,COALESCE(SUM(CASE WHEN tpv.total_score <= 0 THEN 1 ELSE tpv.total_score END * tpv.post_rate)/SUM(CASE WHEN tpv.total_score <= 0 THEN 1 ELSE tpv.total_score END),0) AS score_rate
						FROM  tb_posts_votes tpv
						JOIN  tb_posts tp
						ON tp.dbid_post = tpv.uid_post_voted
						WHERE tp.type_post =  'rate'
						and tp.degree_id = '$dbid_degree'
						group by tp.degree_id";
	
	$query_num_rated ="select count(dbid_post) as counter
						from tb_posts
						where degree_id = '$dbid_degree'
							and type_post= 'rate'";
	
	$query_num_lk_ps = "SELECT lk.total_likes, sm.total_posts FROM tb_degrees_likes lk LEFT JOIN tb_degree_posts_summer sm ON sm.degree_id = lk.uid_liked_degree
						WHERE lk.uid_liked_degree = '$dbid_degree'
						UNION ALL
						SELECT lk.total_likes, sm.total_posts FROM tb_degrees_likes lk RIGHT JOIN tb_degree_posts_summer sm ON sm.degree_id = lk.uid_liked_degree
						WHERE lk.dbid_degree_likes IS NULL
							AND lk.uid_liked_degree = '$dbid_degree'";
	
	$query_if_followed = "select *
						from tb_degrees_user_follows
						where uid_followed_degree = '$dbid_degree'
							and user_uid = '$unique_id'";
							
	$query_num_flwd_ps = "SELECT fw.total_follows 
						FROM tb_degrees_follows fw 
						WHERE fw.uid_followed_degree = '$dbid_degree'";
	
	$query_if_liked = "select *
						from tb_degrees_user_likes
						where uid_liked_degree = '$dbid_degree'
							and user_uid = '$unique_id'";
	
	$query_if_post_rate = "select distinct *
						from tb_posts
						where degree_id = '$dbid_degree'
							and user_create_id = '$unique_id'
							and type_post = 'rate'";
							
	$query_popular_rate = "SELECT sub3.dbid_post,
							sub3.degree_id,
							sub3.user_create_id,
							sub3.created_at,
							sub3.body_content,
							sub3.rate,
							sub3.total_score,
							sub3.name,
							sub3.max_score,
							sub3.type_post,
							COALESCE(count(distinct sub3.dbid_comment),0) as counter_comments,
							COALESCE(sub3.dbid_user_pic, -1) as dbid_user_pic
							FROM
							(
								select sub2.*, ps.dbid_comment ,usp.dbid_user_pic
								FROM
								(
									SELECT sub.* 
									FROM
									(
										select 	ps.dbid_post
										,ps.degree_id
										,ps.user_create_id
										,ps.created_at
										,ps.body_content
										,ps.rate
										,coalesce(pv.total_score, 0) as total_score
										,us.name
										,coalesce(max(total_score),0) as max_score
										,ps.type_post
										from tb_posts ps
										left join tb_posts_votes pv
										on ps.dbid_post = pv.uid_post_voted
										join users us
										on us.unique_id = ps.user_create_id
										where ps.degree_id = '$dbid_degree'
										and ps.type_post ='rate'
									)  sub
									where total_score = max_score
								) sub2
								LEFT JOIN tb_comments_posts ps
								ON ps.uid_post = sub2.dbid_post
								LEFT JOIN users_pic usp				ON (usp.user_unique_id = sub2.user_create_id)
							) sub3
							group by sub3.dbid_post,
									sub3.degree_id,
									sub3.user_create_id,
									sub3.created_at,
									sub3.body_content,
									sub3.rate,
									sub3.total_score,
									sub3.name,
									sub3.max_score,
									sub3.type_post
							order by created_at
							LIMIT 1  ";
	
	mysqli_query($con,"SET NAMES UTF8")or die('Error quering votes_table');
			
	$result_num_rated = mysqli_query($con,$query_num_rated) or die('Error quering votes_table');
	$result_num_lk_ps = mysqli_query($con,$query_num_lk_ps) or die('Error quering votes_table');
	$result_if_liked = mysqli_query($con,$query_if_liked) or die('Error quering votes_table');
	$result_if_post_rate = mysqli_query($con,$query_if_post_rate) or die('Error quering votes_table');
	$result_popular_rate = mysqli_query($con,$query_popular_rate) or die('Error quering votes_table');
	$result_if_followed = mysqli_query($con,$query_if_followed) or die('Error quering votes_table');
	$result_num_flwd_ps = mysqli_query($con,$query_num_flwd_ps) or die('Error quering votes_table');
	$result_score_rate = mysqli_query($con,$query_score_rate) or die('Error quering votes_table');


	if ($result_num_rated && $result_num_lk_ps && $result_if_liked && $result_if_post_rate && $result_popular_rate
		&& $result_if_followed && $result_num_flwd_ps)
	{
		//$response["extra_degree"] = array();
		//$extra = array();
		
		if (mysqli_num_rows($result_score_rate) > 0){
			$row = mysqli_fetch_array($result_score_rate);
			$response["score_rate"] = $row["score_rate"];
		}
		else{
			$response["score_rate"] = 0;
		}
		
		// check for empty result
		if (mysqli_num_rows($result_num_rated) > 0) {
			
			$row = mysqli_fetch_array($result_num_rated);
			$response["num_rated"] = $row["counter"];
		}
		
		if (mysqli_num_rows($result_num_lk_ps) > 0) {
			
			$row = mysqli_fetch_array($result_num_lk_ps);
			$response["num_posts"] = $row["total_posts"];
			$response["num_likes"] = $row["total_likes"];
		}
				
		if (mysqli_num_rows($result_if_liked) > 0) {
			$response["if_liked"] = 1;
		}
		else{
			$response["if_liked"] = 0;
		}
		
		if (mysqli_num_rows($result_num_flwd_ps) > 0) {
			
			$row = mysqli_fetch_array($result_num_flwd_ps);
			$response["num_follows"] = $row["total_follows"];
		}
				
		if (mysqli_num_rows($result_if_followed) > 0) {
			$response["if_followed"] = 1;
		}
		else{
			$response["if_followed"] = 0;
		}
		
		$response["post_rate_extra"] = array();
		$rate_extra = array();
		if (mysqli_num_rows($result_if_post_rate) > 0) {
			$rate_extra["dbid_post"] = $row["dbid_post"];
			$rate_extra["degree_id"] = $row["degree_id"];
			$rate_extra["user_create_id"] = $row["user_create_id"];
			$rate_extra["created_at"] = $row["created_at"];
			$rate_extra["body_content"] = $row["body_content"];
			$rate_extra["rate"] = $row["rate"];
			$rate_extra["type_post"] = $row["type_post"];
			
			$rate_extra["if_post_rate"] = 1;
		}
		else{
			$rate_extra["if_post_rate"] = 0;
		}
		array_push($response["post_rate_extra"], $rate_extra);
		
		if (mysqli_num_rows($result_popular_rate) > 0) {
			
			$row = mysqli_fetch_array($result_popular_rate);
			$response["dbid_post"] = $row["dbid_post"];
			$response["degree_id"] = $row["degree_id"];
			$response["user_create_id"] = $row["user_create_id"];
			$response["created_at"] = $row["created_at"];
			$response["body_content"] = $row["body_content"];
			$response["rate"] = $row["rate"];
			$response["total_score"] = $row["total_score"];
			$response["name"] = $row["name"];
			$response["counter_comments"] = $row["counter_comments"];
			$response["type_post"] = $row["type_post"];
			$response["dbid_user_pic"] = $row["dbid_user_pic"];

		}
		
		// push single product into final response array
		//array_push($response["extra_degree"], $extra);
		//echo json_encode($degree);
			
		// success
		$response["success"] = 1;
	 
		// echoing JSON response
		echo json_encode($response);
		
	} 
	else 
	{
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
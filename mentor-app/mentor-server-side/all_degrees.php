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
   


$query_all ="Select dg.dbid_degree
					,fc.faculty_hebrew
					,sc.name_hebrew as school_name_he
					,sc.name_english as school_name_en
					,sc.school_url_website
					,dg.degree_name
					,sb1.subject_name as subject_name_1
					,sb2.subject_name as subject_name_2
					,sb3.subject_name as subject_name_3
					,sb4.subject_name as subject_name_4
					,lk.total_likes as likes
					,sc.logo_pic
					,sc.header_pic
					,flw.total_follows
			from tb_degrees dg
			LEFT JOIN tb_subjects sb1
			ON	 dg.subject_1 = sb1.dbid_subject
			LEFT JOIN tb_subjects sb2
			ON	 dg.subject_2 = sb2.dbid_subject
			LEFT JOIN tb_subjects sb3
			ON	 dg.subject_3 = sb3.dbid_subject
			LEFT JOIN tb_subjects sb4
			ON	 dg.subject_4 = sb4.dbid_subject
			JOIN tb_faculties fc
			ON fc.dbid_faculty = dg.faculty_id
			JOIN tb_schools sc
			ON	fc.school_id = sc.dbid_school
			JOIN tb_degrees_likes lk
			ON	lk.uid_liked_degree = dg.dbid_degree
			JOIN tb_degrees_follows flw
			ON  flw.uid_followed_degree = dg.dbid_degree
			order by dg.dbid_degree ASC";

mysqli_query($con,"SET NAMES UTF8")or die('Error quering votes_table');
		
$result = mysqli_query($con,$query_all) or die('Error quering votes_table');
// check for empty result
if (mysqli_num_rows($result) > 0) {
	// looping through all results
    // degrees node
    $response["degrees"] = array();
	while($row = mysqli_fetch_array($result)){
		// temp user array
        $degree = array();
        $degree["dbid_degree"] = $row["dbid_degree"];
		$degree["faculty_hebrew"] = $row["faculty_hebrew"];
		$degree["school_name_he"] = $row["school_name_he"];
		$degree["school_name_en"] = $row["school_name_en"];
		$degree["school_url_website"] = $row["school_url_website"];
        $degree["degree_name"] = $row["degree_name"];
		$degree["subject_1"] = $row["subject_name_1"];
		$degree["subject_2"] = $row["subject_name_2"];
        $degree["subject_3"] = $row["subject_name_3"];
        $degree["subject_4"] = $row["subject_name_4"];
        $degree["likes"] = $row["likes"];
		$degree["logo_pic"] = $row["logo_pic"];
		$degree["header_pic"] = $row["header_pic"];
		$degree["total_follows"] = $row["total_follows"];
		

        // push single product into final response array
        array_push($response["degrees"], $degree);
        //echo json_encode($degree);
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
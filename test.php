<?php 
if(isset($_GET['X']) AND isset($_GET['Y']) AND isset($_GET['ID']) )
{

$x = $_GET['X']; 
$y = $_GET['Y'];
$id= $_GET['ID']; 

$conn = new mysqli(localhost, root,SamastShubham,Users);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 



$sql = "UPDATE Location SET X=$x,Y=$y WHERE ID='$id'";
if ($conn->query($sql) === TRUE) {
    
} else {
    echo "Error";
}

    
$myObj->Alert="0";
$myObj->Alert1="0";
$sql = "SELECT * FROM Location Where ID !=$id ";
$result = mysqli_query($conn, $sql);
if (mysqli_num_rows($result) > 0) {
    
   
    while($row = mysqli_fetch_assoc($result)) {
   $ch = curl_init();
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_URL, 
    'https://maps.googleapis.com/maps/api/distancematrix/json?origins='.$row["X"].','.$row["Y"].'&destinations='.$x.','.$y.'&key=AIzaSyDRi6HQMIdSVKwX3K8HQOlGRELqkYObNvw'
);
$content = curl_exec($ch);
$array = json_decode($content);
$obj = json_decode($content, TRUE);

$distance = $obj['rows'][0]['elements'][0]['distance']['text'];
$t=floatval($distance);
if($t!=0 && $t<1)
{
$myObj->Alert = "1";



}

 }
$arr=array();
$row = -1;
if (($handle = fopen("test.csv", "r")) !== FALSE) {
    while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
        $num = count($data);

        $row++;
        for ($c = 0; $c < $num; $c++) {
            $arr[$row][$c]= $data[$c];
        }
    }
    fclose($handle);
}
$url='http://epsg.io/trans?x='.$x.'&y='.$y.'&s_srs=4326&t_srs=3067';
$json = file_get_contents($url);
$data = json_decode($json);
$tx= $data->x;   
$ty=$data->y;

for ($c = 1; $c < 45; $c++) {
          if(($arr[$c][2]<8)&&($arr[$c][3]>14)||($arr[$c][2]>8)&&($arr[$c][4]<202))
{


if(abs(round(floatval($tx),2)-round(floatval($arr[$c][0])),2))
{
$myObj->Alert1 = "1";
}
}
        }
$myJSON = json_encode($myObj);

echo $myJSON;

} else {

echo "lol";
   
}


// relese memory 
unset($conn);
unset($sql);
unset($myObj);
unset($sql);
unset($result);
unset($row);
unset($ch);
unset($content);
unset($array);
unset($obj);
unset($myObj);
unset($arr);
unset($handle);
unset($data);
unset($num);
$conn->close();
}
else {

echo "lol";
   
}
?>

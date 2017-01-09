<?php

$data_url = 'http://www.nita.ac.in';
$req_url = 'http://android.googleapis.com/gcm/send';
//$time_file = 'last_update_time.txt';
//$data_file = 'last_update_notices.txt';
$time_file = 'gs://#default#/last_update_time.txt';
$time_file_debug = 'gs://#default#/last_update_time_debug.txt';
$data_file = 'gs://#default#/last_update_notices.txt';
$msg = '';
$change = false;
$new_notices = [];
$old_notices = [];
$key1 = 'AIzaSyATpu6HrtBbz61wgxCzue9nxXtd_AQbNsk______';
//$key1 = 'AIzaSyATpu6HrtBbz61wgxCzue9nxXtd_AQbNsk';
$key_old = 'AIzaSyBWadID4BBABPNdBTAebYgIXZ_zQDmmyOo';


$h = get_headers($data_url, TRUE);
$time_new = $h["Last-Modified"];
$time_old =file_get_contents($time_file);


//sometimes the website returns empty last modified time
if( ($time_new == "") || ($time_new == $time_old) ) {
   $msg = 'Not updated after ' . $time_new . '.';
}
else {
   file_put_contents($time_file, $time_new);
   file_put_contents($time_file_debug, $time_old);


   $doc = new DomDocument();
   libxml_use_internal_errors(true);
   $data = file_get_contents($data_url);
   $doc->loadHTML(mb_convert_encoding($data, 'HTML-ENTITIES', 'UTF-8'));
   libxml_clear_errors();
   $div = $doc->getElementById('vmarquee');
   
   //create the array of new notices
   //foreach($div->getElementsByTagName('a') as $link) {
   foreach($div->getElementsByTagName('p') as $link) {
        $new_notices[] = $link->nodeValue ;
   }

   //read old notices from file
   $string_data = file_get_contents($data_file);
   $old_notices = unserialize($string_data);

   //check which notices are actually new
   foreach($new_notices as $newVal) {

    //reset flag
    $match = false;

    //compare each new notice with every old notice
    foreach($old_notices as $oldVal) {
      if($newVal==$oldVal) {
        //set flag if matched and stop checking
        $match = true;
        break;
      }
    }
    
    //if none matched, set change flag, add to msg
    if(!$match){
      $change = true;
      if($msg!="")
        $msg = $msg . "\n";
      $msg = $msg . "* " . $newVal;
    }

  }

  //write to file if actually new
  if($change){
    file_put_contents($data_file, serialize($new_notices));
  }
  else {
    $msg = "NITA Website updated on " . $time_new . ". No new notice." ;
  }

  //for release version users
  $fields = array(
       'to'  => '/topics/release',
       'data' => array( "message" => $msg ),
       );

  $context = [
    'http' => [
      'method' => 'POST',
      'header' => "Authorization: key=" .$key1. "\r\n" .
                  "Content-Type: application/json\r\n",
      'content' => json_encode( $fields )
    ]
  ];
  $context = stream_context_create($context);
  $result = file_get_contents($req_url, false, $context);
  echo "Release build ::  ". $result . "\n" . $msg . "\n\n";

//coz I messed up, send to test also
  $fields = array(
      'to'  => '/topics/test',
      'data' => array( "message" => $msg ),
              );

  $context = [
      'http' => [
      'method' => 'POST',
      'header' => "Authorization: key=" .$key1. "\r\n" .
                "Content-Type: application/json\r\n",
      'content' => json_encode( $fields )
  ]
];
$context = stream_context_create($context);
$result = file_get_contents($req_url, false, $context);
echo "Test Users ::  " . $result . "\n" . $msg . "\n\n";
}


//for test version users
/*
$fields = array(
     'to'  => '/topics/test',
     'data' => array( "message" => $msg ),
                );

$context = [
  'http' => [
    'method' => 'POST',
    'header' => "Authorization: key=" .$key1. "\r\n" .
                "Content-Type: application/json\r\n",
    'content' => json_encode( $fields )
  ]
];
$context = stream_context_create($context);
$result = file_get_contents($req_url, false, $context);
echo "Test Users ::  " . $result . "\n" . $msg . "\n\n";
*/

?>

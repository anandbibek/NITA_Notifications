<?php


/**
 * To Run locally in XAMPP - comment out the code in between [gae_storage_customization]
 * In that case, uncomment the standard file names
 */

namespace ananda\nita\notifications\cron\Storage;
use DOMDocument;
use DOMXPath;

$data_url = 'https://nita.ac.in';
$req_url = 'https://fcm.googleapis.com/fcm/send';


# [START gae_storage_customization]
use Google\Cloud\Storage\StorageClient;
require_once __DIR__ . '/vendor/autoload.php';

$defaultBucketName = sprintf('%s.appspot.com', getenv('GOOGLE_CLOUD_PROJECT'));
$storage = new StorageClient();
$storage->registerStreamWrapper();

$time_file = "gs://${defaultBucketName}/last_update_time.txt";
$data_file = "gs://${defaultBucketName}/last_update_notices.txt";
$time_file_debug = "gs://${defaultBucketName}/last_update_time_debug.txt";
# [END gae_storage_customization]

// standard file names for local run
// $time_file = 'last_update_time.txt';
// $data_file = 'last_update_notices.txt';
// $time_file_debug = 'last_update_time_debug.txt';

$msg = '';
$change = false;
$new_notices = [];
$old_notices = [];
$key1 = 'AIzaSyATpu6HrtBbz61wgxCzue9nxXtd_AQbNsk';


$h = get_headers($data_url, true);
$time_new = $h["Date"];
$time_old = file_get_contents($time_file);


//sometimes the website returns empty last modified time
if (($time_new == "") || ($time_new == $time_old)) {
    $msg = 'Not updated after ' . $time_new . '.';
    echo $msg;
} else {
    file_put_contents($time_file, $time_new);
    file_put_contents($time_file_debug, $time_old);


    $doc = new DomDocument();
    libxml_use_internal_errors(true);

    $optx = array('http'=>array('header' => "User-Agent:NITA_APP_CRON/1.0\r\n"));
    $ctx = stream_context_create($optx);
    $data = file_get_contents($data_url, false, $ctx );

    $doc->loadHTML(mb_convert_encoding($data, 'HTML-ENTITIES', 'UTF-8'));
    libxml_clear_errors();

    $xpath = new DOMXPath($doc);
    $elements = $xpath->query("//*[contains(concat(' ', normalize-space(@class), ' '), ' notice_board_overflow ')]");


    foreach ($elements as $element) {
        $links = $element->getElementsByTagName("a");
        foreach ($links as $link) {
            $new_notices[] = $link->nodeValue;
            
        }
    }

    //read old notices from file
    $string_data = file_get_contents($data_file);
    $old_notices = unserialize($string_data);

    //check which notices are actually new
    foreach ($new_notices as $newVal) {

        //reset flag
        $match = false;

        //compare each new notice with every old notice
        foreach ($old_notices as $oldVal) {
            if ($newVal == $oldVal) {
                //set flag if matched and stop checking
                $match = true;
                break;
            }
        }

        //if none matched, set change flag, add to msg
        if (!$match) {
            $change = true;
            if ($msg != "") {
                $msg = $msg . "\n";
            }
            $msg = $msg . "* " . $newVal;
        }

    }

    //write to file if actually new
    if ($change) {
        file_put_contents($data_file, serialize($new_notices));

        //for release version users
        $fields = array(
            'to' => '/topics/release',
            'data' => array("message" => $msg),
        );

        $context = [
            'http' => [
                'method' => 'POST',
                'header' => "Authorization: key=" . $key1 . "\r\n" .
                    "Content-Type: application/json\r\n",
                'content' => json_encode($fields)
            ]
        ];

        $context = stream_context_create($context);
        $result = file_get_contents($req_url, false, $context);
        echo "Response ::  " . $result;

    } else {
        date_default_timezone_set('Asia/Kolkata'); // your user's timezone
        $adjusted_time = date('Y-m-d H:i',strtotime("$time_new UTC"));
        $msg = "NITA Website updated on " . $adjusted_time . ". No new notice.";
    }

    echo "Payload :: " . $msg;

}

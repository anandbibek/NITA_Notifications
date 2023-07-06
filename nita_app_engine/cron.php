<?php


/**
 * To Run locally in XAMPP - comment out the code in between [gae_storage_customization]
 * In that case, uncomment the standard file names
 */

namespace ananda\nita\notifications\cron\Storage;
use DOMDocument;
use DOMXPath;


require_once 'vendor/autoload.php';
use Google\Client;
use Google\Service\FirebaseCloudMessaging;
use Google\Exception;

# [START gae_storage_customization]
use Google\Cloud\Storage\StorageClient;

$defaultBucketName = sprintf('%s.appspot.com', getenv('GOOGLE_CLOUD_PROJECT'));
$storage = new StorageClient();
$storage->registerStreamWrapper();

$time_file = "gs://{$defaultBucketName}/last_update_time.txt";
$data_file = "gs://{$defaultBucketName}/last_update_notices.txt";
$token_file = "gs://{$defaultBucketName}/token.cache";
$topic = "release";
# [END gae_storage_customization]

// standard file names for local run
// $time_file = 'last_update_time.txt';
// $data_file = 'last_update_notices.txt';
// $token_file = 'token.cache';
// $topic = "test";


$data_url = 'https://nita.ac.in';
$requrl = 'https://fcm.googleapis.com/v1/projects/nita-notifications/messages:send';
$msg = '';
$change = false;
$new_notices = [];
$old_notices = [];


$h = get_headers($data_url, true);
$time_new = $h["Date"];
$time_old = file_get_contents($time_file);


//sometimes the website returns empty last modified time
if (($time_new == "") || ($time_new == $time_old)) {
    $msg = 'Not updated after ' . $time_new . '.';
    echo $msg;
} else {
    file_put_contents($time_file, $time_new);


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
            $msg = $msg . "• " . $newVal;
        }

    }

    //write to file if actually new
    if ($change) {
        file_put_contents($data_file, serialize($new_notices));

        $token = getOAUTHToken();
        sendNotification($topic, "NITA Updates", $msg, array("message" => $msg), $token, $requrl);

    } else {
        //date_default_timezone_set('Asia/Kolkata'); // your user's timezone
        //$adjusted_time = date('Y-m-d H:i',strtotime("$time_new UTC"));
        $msg = "NITA Website updated on " . $time_new . ". No new notice.";
    }

    log(LOG_INFO, $msg);

}


function getOAUTHToken()
{
    $client = new Client();
    try {
        $client->setAuthConfig("secrets/something.json");
        $client->addScope(FirebaseCloudMessaging::FIREBASE_MESSAGING);

        $savedTokenJson = readSavedToken();

        if ($savedTokenJson) {
            // the token exists, set it to the client and check if it's still valid
            $client->setAccessToken($savedTokenJson);
            $accessToken = $savedTokenJson;
            if ($client->isAccessTokenExpired()) {
                // the token is expired, generate a new token and set it to the client
                $accessToken = generateToken($client);
                $client->setAccessToken($accessToken);
            }
        } else {
            // the token doesn't exist, generate a new token and set it to the client
            $accessToken = generateToken($client);
            $client->setAccessToken($accessToken);
        }
        

        return $accessToken["access_token"];
        
    } catch (Exception $e) {
        log(LOG_ERR, $e);
    }
   return false;
}

//Using a simple file to cache and read the toke, can store it in a databse also
function readSavedToken() {
    $tk = @file_get_contents('token.cache');
    if ($tk) {
        log(LOG_INFO, 'Reusing access token');
        return json_decode($tk, true);
    } else {
        return false;
    }
  }

function writeToken($tk) {
    file_put_contents("token.cache",$tk);
}

function generateToken($client) {
    $client->fetchAccessTokenWithAssertion();
    $accessToken = $client->getAccessToken();

    $tokenJson = json_encode($accessToken);
    writeToken($tokenJson);
    log(LOG_INFO, 'Generated new access token');
    return $accessToken;
}

function sendNotification($topic, $title, $body, $data, $accessToken, $requrl) {

    $payload = [
        "message" => [
            "topic" => $topic,
            "notification"=>["title" => $title, "body"=> $body],
            "data" => $data,
            "android" => [
                "notification"=>["icon" => 'ic_school_white_48dp', "color"=> '#990000'],
                "priority" => "high"
                ]
            ]
        ];

    $postdata = json_encode($payload);
    $opts = array('http' =>
        array(
            'method'  => 'POST',
            'header'  => 'Content-Type: application/json' . "\r\nAuthorization: Bearer $accessToken",
            'content' => $postdata
        )
    );

    $context  = stream_context_create($opts);
    $result = file_get_contents($requrl, false, $context);

    log(LOG_INFO, "======RESPONSE======");
    log(LOG_INFO, $result);

}

function log($level, $msg) {
    echo $msg . '<br>';
    syslog($level, $msg);
}

<?php
define('SO_API', 'http://219.243.47.169:7676/api');

class SoClient {
    
    private $api_url = SO_API;

    public function __construct() {
    }

    public function complete($q, $limit = 10) {
        $q = rawurlencode($q);
        $limit = rawurlencode($limit);
        $body = file_get_contents($this->api_url . '/search/complete?' . 'q=' . $q . '&limit=' . $limit);
        $response = json_decode($body, TRUE);
        return $response;
    }

    public function search($q, $start = 0, $limit = 15) {
        $q = rawurlencode($q);
        $start = rawurlencode($start);
        $limit = rawurlencode($limit);
        $body = file_get_contents($this->api_url . '/search/query?' . 'q=' . $q . '&start=' . $start . '&limit=' . $limit);
        $response = json_decode($body, TRUE);
        return $response;
    }

}

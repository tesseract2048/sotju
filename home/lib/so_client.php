<?php
define('SO_API', 'http://219.243.47.169:7676/api');

class SoClient {
    
    private $api_url = SO_API;

    public function __construct() {
    }

    public function complete($u, $q, $limit = 10) {
        $u = rawurlencode($u);
        $q = rawurlencode($q);
        $limit = rawurlencode($limit);
        $body = file_get_contents($this->api_url . '/search/complete?' . 'u=' . $u . '&q=' . $q . '&limit=' . $limit);
        $response = json_decode($body, TRUE);
        return $response;
    }

    public function click($u, $q, $schemaId, $siteId, $id, $position) {
        $u = rawurlencode($u);
        $q = rawurlencode($q);
        $body = file_get_contents($this->api_url . '/search/click?' . 'u=' . $u . '&q=' . $q . '&schemaId=' . $schemaId . '&siteId=' . $siteId . '&id=' . $id . '&position=' . $position);
        $response = json_decode($body, TRUE);
        return $response;
    }

    public function search($u, $q, $start = 0, $limit = 15) {
        $u = rawurlencode($u);
        $q = rawurlencode($q);
        $start = rawurlencode($start);
        $limit = rawurlencode($limit);
        $body = file_get_contents($this->api_url . '/search/query?' . 'u=' . $u . '&q=' . $q . '&start=' . $start . '&limit=' . $limit);
        $response = json_decode($body, TRUE);
        return $response;
    }

}

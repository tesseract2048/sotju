<?php
include_once './lib/so_client.php';
$client = new SoClient();
echo json_encode($client->complete($_GET['q']));
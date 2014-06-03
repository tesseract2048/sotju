<?php
include_once './lib/so_client.php';
$u = $_SERVER['REMOTE_ADDR'];
$q = $_GET['q'];
$siteId = $_GET['siteId'];
$schemaId = $_GET['schemaId'];
$id = $_GET['id'];
$position = $_GET['position'];
$url = $_GET['url'];
$client = new SoClient();
$response = $client->click($u, $q, $schemaId, $siteId, $id, $position);
header('Location: ' . $url);
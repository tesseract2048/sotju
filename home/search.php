<?php
include_once './lib/so_client.php';
$start = 0;
if (isset($_GET['start'])) {
    $start = (int)$_GET['start'];
}
$q = $_GET['q'];
$client = new SoClient();
$response = $client->search($q, $start);
?>
<p>
    Found <?php echo $response['total'];?> result(s) in <?php echo $response['queryTook'];?> ms.
</p>
<table>
<?php
foreach ($response['result'] as $item) {?>
    <tr>
        <td>
            <strong><?php echo $start+$item['position']+1;?>. <a href="<?php echo $item['entity']['fieldValues']['url'];?>" target="_blank"><?php echo $item['entity']['fieldValues']['title'];?></a></strong>
        </td>
    </tr>
    <tr>
        <td>
            <span>Schema: <?php echo $item['entity']['schemaId'];?></span> | 
            <span>Site: <?php echo $item['entity']['siteId'];?></span>  | 
            <span>Id: <?php echo $item['entity']['id'];?></span> | 
            <span>Score: <?php echo $item['score'];?></span> | 
            <span>Boost: <?php echo $item['docBoost'];?></span>
        </td>
    </tr>
<?php
switch ($item['entity']['schemaId']) {
    case 'torrent':
?>
    <tr>
        <td>
            <span>Seeder: <?php echo $item['entity']['fieldValues']['seeder'];?></span> | 
            <span>Leecher: <?php echo $item['entity']['fieldValues']['leecher'];?></span> | 
            <span>Download: <?php echo $item['entity']['fieldValues']['download'];?></span> 
        </td>
    </tr>
    <tr>
        <td>
            <?php echo $item['entity']['fieldValues']['info'];?>
        </td>
    </tr>
<?php
    break;
    case 'movie':
?>
    <tr>
        <td>
            <span>View: <?php echo $item['entity']['fieldValues']['view'];?></span> | 
            <span>Comment: <?php echo $item['entity']['fieldValues']['comment'];?></span> 
        </td>
    </tr>
    <tr>
        <td>
            <?php echo substr($item['entity']['fieldValues']['description'],0,500);?>
        </td>
    </tr>
<?php
    break;
    case 'article':
?>
    <tr>
        <td>
            <span>Column: <?php echo $item['entity']['fieldValues']['column'];?></span> | 
            <span>Author: <?php echo $item['entity']['fieldValues']['author'];?></span> | 
            <span>Read: <?php echo $item['entity']['fieldValues']['read'];?></span>
        </td>
    </tr>
    <tr>
        <td>
            <?php echo substr($item['entity']['fieldValues']['content'],0,500);?>
        </td>
    </tr>
<?php
}
}?>
</table>
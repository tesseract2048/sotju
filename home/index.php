
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="//getbootstrap.com/assets/ico/favicon.ico">

    <title>SoTju Experiment Portal</title>

    <!-- Bootstrap core CSS -->
    <link href="//getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="//getbootstrap.com/examples/cover/cover.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy this line! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="site-wrapper">

      <div class="site-wrapper-inner">

        <div class="cover-container">

          <div class="masthead clearfix">
            <div class="inner">
              <h3 class="masthead-brand">Cover</h3>
              <ul class="nav masthead-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">Features</a></li>
                <li><a href="#">Contact</a></li>
              </ul>
            </div>
          </div>

          <div class="inner cover">
            <h1 class="cover-heading">SoTju Experiment Portal</h1>
            <p class="lead">
              <input type="text" id="q" name="q" style="color: #000000; width: 200px;"/> 
              <a href="#" class="btn btn-lg btn-default" id="btn-search" >Search</a>
            </p>
            <div id="completions" style="display: block; margin-left: -1px; margin-right: 1px; position: absolute; clear: both; width: auto"></div>
          </div>

          <div class="mastfoot">
            <div class="inner">
              <p>&copy; SoTju Project</p>
            </div>
          </div>

        </div>

      </div>

    </div>

    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="//getbootstrap.com/dist/js/bootstrap.min.js"></script>
    <script src="//getbootstrap.com/assets/js/docs.min.js"></script>

    <script type="text/javascript">
    function do_search() {
      window.location.href = 'search.php?q=' + encodeURIComponent($('#q').val());
    }
    function auto_complete() {
      $.get('completion_ajax.php?q=' + encodeURIComponent($('#q').val()), function(data) {
        var html = '';
        for (var i in data) {
          html += '<p><a href="search.php?q=' + encodeURIComponent(data[i]) + '" class="completion">' + data[i] + '</a></p>';
        }
        $('#completions').html(html);
      }, 'json');
    }
    $(document).ready(function() {
      $('#q').keyup(auto_complete).change(auto_complete);
      $('#btn-search').click(function() {
        do_search($('#q').val());
        return false;
      });
    });
    </script>
  </body>
</html>

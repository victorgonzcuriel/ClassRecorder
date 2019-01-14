<!-- <html>
<head>
	<title>Home</title>
	 <!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<div>
		<input type="button" id="newClassModalButton" class="btn btn-primary"
			value="Nueva Clase">
	</div>
	<div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-body">
          <p>Nombre del fichero: </p>
          <input type="text" id="FileNameBox"/>
        </div>
        <div class="modal-footer">
          <input type="button" class= "btn btn-primary" id="newClassButton" value="Aceptar"/>
        </div>
      </div>

    </div>
  </div>
	<script type="text/javascript">
		$('#newClassModalButton').click(function() {
			$('#myModal').modal()
		})
		$('#newClassButton').click(function(){
			let fileName = $('#FileNameBox').val()
			window.location.href = '/ClassRecorder/web/newclass?fileName=' + fileName;
		})
	</script>
<!--<h1>
	Hello world!
</h1>

<P>  The time on the server is ${serverTime}. </P>
</body> -->
</html>

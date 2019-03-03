<html>
	<head>
		<title>Home</title>
	
		<link rel="stylesheet" href="resources/css/bootstrap.min.css">
		<script src="resources/js/jquery-3.2.1.min.js"></script>
		<script src="resources/js/bootstrap.min.js"></script>
	</head>
	<body>
		<div style="position:fixed;left:50%;margin-left:-200px;margin-top:50px;font-family:'Comic Sans MS', cursive, sans-serif;
		color:CornflowerBlue;font-size:70px;">
			Class Recorder
		</div>
		<div class="container">
			<div class="row">
				<div style="position:absolute;top:50%;left:50%;margin-top:-50px;margin-left:-50px;width:100px;height:100px;">
					<input type="button" id="newClassModalButton" class="btn btn-primary" style = "margin-bottom:15px;"
							value="Nueva Clase"/>
					<br/>
					<input type="button" id="management" class="btn btn-primary" value="Gestión de clases"/>
				</div>
			</div>
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
 	</body>
	<script type="text/javascript">
		$('#newClassModalButton').click(function() {
			$('#myModal').modal()
		})
		$('#newClassButton').click(function(){
			let fileName = $('#FileNameBox').val()
			window.location.href = '/ClassRecorder/web/newclass?fileName=' + fileName;
		})
		$('#management').click(function(){
			window.location.href = '/ClassRecorder/web/management';
		})
	</script>
</html>

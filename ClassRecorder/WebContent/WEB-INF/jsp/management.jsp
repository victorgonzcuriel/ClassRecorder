<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<link rel="stylesheet" href="resources/css/bootstrap.min.css">
	
	<link rel="stylesheet" href="resources/css/datatables.min.css">
	
	<!-- jQuery library -->
	<script src="resources/js/jquery-3.2.1.min.js"></script>
	
	<!-- Latest compiled JavaScript -->
	<script src="resources/js/bootstrap.min.js"></script>
	<script src="resources/js/jquery.dataTables.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
	<script type="text/javascript" src="resources/js/stomp.js"></script>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Class Recorder - Gestión de clases</title>
	</head>
	<body>
	<div>
		<table id="tableId">
		</table>	
	</div>
	<!-- DIV CON EL LOADER -->
	<div class="modal fade" id="myModal" style="left:50%;position: absolute; top: 50%; margin-left:-150px; margin-top:-150px" role="dialog">
	    <div class="modal-dialog">
			<div class="modal-content" style="vertical-align:middle; display: inline-block">
				<img src="resources/misc/gear.gif">
			</div>
		</div>
	</div>
	</body>
	<script type="text/javascript">
		var data = '${items}';
		var finalData = JSON.parse(data);
				
		$('#tableId').DataTable({
			data: finalData.list,
			language: {
				 	"decimal": "",
			        "emptyTable": "No hay información",
			        "info": "Mostrando _START_ a _END_ de _TOTAL_ Entradas",
			        "infoEmpty": "Mostrando 0 to 0 of 0 Entradas",
			        "infoFiltered": "(Filtrado de _MAX_ total entradas)",
			        "infoPostFix": "",
			        "thousands": ",",
			        "lengthMenu": "Mostrar _MENU_ Entradas",
			        "loadingRecords": "Cargando...",
			        "processing": "Procesando...",
			        "search": "Buscar:",
			        "zeroRecords": "Sin resultados encontrados",
			        "paginate": {
			            "first": "Primero",
			            "last": "Ultimo",
			            "next": "Siguiente",
			            "previous": "Anterior"
			        }
			},
			columns: [
				{
					title: 'Nombre del fichero',
					data: 'fileName',
					targets: 0 
				},
				{
					title : "Borrar",
					data: null,
					targets: 1,
					render: function(data, type, full, meta){
						if(true)
							//return '<button class="btn btn.mini btn.primary" onclick="DeleteFile(\''+data.fileName+'\')">Borrar</button>'
							return '<input type="image" src="resources/img/delete.png" onclick="DeleteFile(\''+data.fileName+'\')"/>'
					}
				},
				{
					title : 'Estado',
					data: 'status',
					targets: 2
				},
				{
					title: 'Recoger',
					data: null,
					targets: 3,
					render: function(data, type, full, meta){
						if(true)
							//return '<button class="btn btn.mini btn.primary" onclick="GetFile(\''+data.fileName+'\')">Recoger</button>'
							return '<input type="image" src="resources/img/download.png" onclick="GetFile(\''+data.fileName+'\')"/>'
					}
				},
				{
					title: 'Mezclar',
					data: null,
					targets: 4,
					render: function(data, type, full, meta){
						if(true)
							//return '<button class="btn btn.mini btn.primary" onclick="MixFile(\''+data.fileName+'\')">Mezclar</button>'
							return '<input type="image" src="resources/img/mix.png" onclick="MixFile(\''+data.fileName+'\')"/>'
					}
				},
				{
					title: 'Cortar',
					data: null,
					targets: 5,
					render: function(data, type, full, meta){
						if(true)
							//return '<button class="btn btn.mini btn.primary" onclick="CutFile(\''+data.fileName+'\')">Cortar</button>'
							return '<input type="image" src="resources/img/cut.png" onclick="CutFile(\''+data.fileName+'\')"/>'
					}
				},
				{
					title: 'Procesar',
					data: null,
					targets: 6,
					render: function(data, type, full, meta){
						if(true)
							//return '<button class="btn btn.mini btn.primary" onclick="DoAll(\''+data.fileName+'\')">Procesar</button>'
							return '<input type="image" src="resources/img/process.png" onclick="DoAll(\''+data.fileName+'\')"/>'
					}
				}
			]
		});
		
		function GetFile(dataFileName){
		    stompClient = Stomp.client('ws://localhost:8080/ClassRecorder/ws/websocket');
		    stompClient.connect({}, function(frame) {
		    	stompClient.subscribe('/subscription/sub', function(message){
			    	
		    		let msg = JSON.parse(message.body);
		    		
		    		if(msg.action == 'RELOAD'){
		    			window.location.reload();
		    		}
			    });

				stompClient.send("/crws/msg", {},JSON.stringify({action: 'DOWNLOAD', file : {fileContent : 'fileContentPlaceHolder', fileName : dataFileName}}));
				$('#myModal').modal({backdrop: 'static', keyboard: false})
		    });
		}
		
		function MixFile(fileName){
			$('#myModal').modal({backdrop: 'static', keyboard: false});
			window.location.href = '/ClassRecorder/web/mix?fileName=' + fileName;
		}
		
		function CutFile(fileName){
			$('#myModal').modal({backdrop: 'static', keyboard: false});
			window.location.href = '/ClassRecorder/web/cut?fileName=' + fileName;
		}
		
		function DoAll(fileName){
			stompClient = Stomp.client('ws://localhost:8080/ClassRecorder/ws/websocket');
		    stompClient.connect({}, function(frame) {
		    	stompClient.subscribe('/subscription/sub', function(message){
			    	
		    		let msg = JSON.parse(message.body);
		    		
		    		if(msg.action == 'RELOAD'){
		    			window.location.href = '/ClassRecorder/web/DoAll?fileName=' + fileName;
		    		}
			    });
		    	
				stompClient.send("/crws/msg", {},JSON.stringify({action: 'DOWNLOAD', file : {fileContent : 'fileContentPlaceHolder', fileName : fileName}}));
				$('#myModal').modal({backdrop: 'static', keyboard: false})
				
		    });
		}
		
		function DeleteFile(fileName){
			$('#myModal').modal({backdrop: 'static', keyboard: false})
			
			stompClient = Stomp.client('ws://localhost:8080/ClassRecorder/ws/websocket');
		    stompClient.connect({}, function(frame) {
		    	stompClient.subscribe('/subscription/sub', function(message){
			    	
		    		let msg = JSON.parse(message.body);
		    		
		    		if(msg.action == 'DELETED'){
		    			window.location.href = '/ClassRecorder/web/DeleteFile?fileName=' + fileName;
		    		}
			    });
		    	
				stompClient.send("/crws/msg", {},JSON.stringify({action: 'DELETE', file : {fileContent : 'fileContentPlaceHolder', fileName : fileName}}));
				
		    });
		}
	</script>
</html>
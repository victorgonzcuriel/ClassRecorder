<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!-- <script src="//mozilla.github.io/pdf.js/build/pdf.js"></script>-->
<!-- <script type="text/javascript" src="${pageContext.request.contextPath }/js/pdf.js"></script>-->
<script type="text/javascript" src="resources/js/pdf.js"></script>
<!-- <script type="text/javascript" src="resources/js/sockjs.min.js"></script>-->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="resources/js/stomp.js"></script>
<link rel="stylesheet" href="resources/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="resources/js/jquery-3.2.1.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="resources/js/bootstrap.min.js"></script> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Clase: Presentación</title>
</head>
<style>
.glyphicon.glyphicon-red:before {
    content: "\25cf";
    font-size: 1.5em;
    color: red;
    display: block;
}

.glyphicon.glyphicon-green:before {
    content: "\25cf";
    font-size: 1.5em;
    color: green;
    display: block;
}

</style>
<body>
	<div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-body">
          <p>Clase terminada</p>
        </div>
        <div class="modal-footer">
          <input type="button" class= "btn btn-primary" id="btnEnd" value="Aceptar"/>
        </div>
     </div>

    </div>
  </div>
	<div>
	<div>
		<span id="icon-red" class="glyphicon glyphicon-red" style="margin-bottom:4px;"></span>
		<span id="icon-green" class="glyphicon glyphicon-green" style="margin-bottom:4px;"></span> 
	</div>
	<div id="fileDiv" style="margin-bottom:4px;position:absolute;left:50%;margin-left:-50px;width:100px;height:100px;">
		<input type="file" id="uploadId"/>
	</div>
	</div>

	<div style="margin-bottom:4px;" id="paginationId">
  		<span>
  			Página: <span id="page_num"></span> / <span id="page_count"></span>
  		</span>
	</div>
	<div >
		<canvas id="the-canvas" style="padding-left: 0;padding-right: 0;margin-left: auto;margin-right: auto;display: block;width: 800px;"></canvas>
	</div>
	<input type="hidden" id="modelId" value='${fileName}'/>
	<script type="text/javascript">
	
		$("#paginationId").hide();
	
		$('#btnEnd').click(function(){
			window.location.href = '/ClassRecorder/web/'
		})
	
		$('#icon-green').hide();
		
		//conexion a websocket para mandar el nombre del fichero
			
	    //var socket = new SockJS('ws://localhost:8080/ClassRecorder/ws/websocket');
	    stompClient = Stomp.client('ws://localhost:8080/ClassRecorder/ws/websocket');
	    stompClient.connect({}, function(frame) {

			stompClient.send("/crws/msg", {},JSON.stringify({action: 'NEW', file : {fileContent : 'newFile', fileName : '${fileName}'}}))
			stompClient.disconnect();
	    });

		//fin de la conexion a websocket para manda el nombre del fichero
		
		PDFJS.workerSrc = 'resources/js/pdf.worker.js';
	
		var pdfDoc = null,
		    pageNum = 1,
		    pageRendering = false,
		    pageNumPending = null,
		    scale = 1,
		    canvas = document.getElementById('the-canvas'),
		    ctx = canvas.getContext('2d');
		function renderPage(num) {
		  pageRendering = true;
		  pdfDoc.getPage(num).then(function(page) {
		    var viewport = page.getViewport(scale);
		    canvas.height = viewport.height;
		    canvas.width = viewport.width;
		    
		    var renderContext = {
		      canvasContext: ctx,
		      viewport: viewport
		    };
		    var renderTask = page.render(renderContext);
	
		    renderTask.promise.then(function() {
		      pageRendering = false;
		      if (pageNumPending !== null) {
		        renderPage(pageNumPending);
		        pageNumPending = null;
		      }
		    });
		  });
			
		  document.getElementById('page_num').textContent = pageNum;
		}
	
		function queueRenderPage(num) {
		  if (pageRendering) {
		    pageNumPending = num;
		  } else {
		    renderPage(num);
		  }
		}
	
		function onPrevPage() {
		  if (pageNum <= 1) {
		    return;
		  }
		  pageNum--;
		  queueRenderPage(pageNum);
		}
	
		function onNextPage() {
		  if (pageNum >= pdfDoc.numPages) {
		    return;
		  }
		  pageNum++;
		  queueRenderPage(pageNum);
		}
	
		 $('#uploadId').change(function(value) {
			var file = value.target.files[0];
			if (file.name.split('.').pop() == 'pdf'){
			$("#fileDiv").hide()
			$("#paginationId").show()
			var fileReader = new FileReader();
			fileReader.onload = function(){
				var typedarray = new Uint8Array(this.result);
				PDFJS.getDocument(typedarray).then(function(pdf) {
		    		document.getElementById('page_count').textContent = pdf.numPages;
		    		pdfDoc = pdf;
		    		renderPage(pageNum);
				});	  		
				}
			fileReader.readAsArrayBuffer(file);
			}else{
				alert("El fichero no tiene extensión pdf")
			}
		});
		 
		 let isRecording = false;
		 
		function StopRecord(){
			
			stompClient = Stomp.client('ws://localhost:8080/ClassRecorder/ws/websocket');
		    stompClient.connect({}, function(frame) {

				stompClient.send("/crws/msg", {},JSON.stringify({action: 'CLASSENDED', file : {fileContent : 'fileContentPlaceHolder', fileName : '${fileName}'}}));
		    });
		    
			$.ajax({
				url: '/ClassRecorder/web/StopRecord.html',
				cache: false,
				async: false,
				success: function(data){
					$('#myModal').modal({backdrop: 'static', keyboard: false})
				},
				error: function(data){
				}
			});
		}
		
		function Play(){
			$.ajax({
				url: '/ClassRecorder/web/Play.html',
				cache: false,
				async: false,
				success: function(data){
					isRecording = !isRecording;
					if(isRecording){
						$('#icon-red').hide();
						$('#icon-green').show();
					}else{
						$('#icon-red').show();
						$('#icon-green').hide();
					}
				},
				error: function(data){
				}
			});
		}
		
		$(document).keydown(function(key){
			switch(key.which){
			//Derecha
			case 39:
				onNextPage();
				break;
			//Izquierda
			case 37:
				onPrevPage();
				break;
			//R
			case 82:
				StopRecord();
				break;
			//P
			case 80:
				Play();
				break;
			}
			
		})
	</script>
</body>
</html>
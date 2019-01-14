<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<!-- <script src="//mozilla.github.io/pdf.js/build/pdf.js"></script>-->
<!-- <script type="text/javascript" src="${pageContext.request.contextPath }/js/pdf.js"></script>-->
<script type="text/javascript" src="resources/js/pdf.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script> 
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
	<!--<p>${actualRecord.GetFilename()}</p> -->
	<div>
	<div>
		<span id="icon-red" class="glyphicon glyphicon-red"></span>
		<span id="icon-green" class="glyphicon glyphicon-green"></span> 
	</div>
	<div id="fileDiv">
		<p th:text="${fileName}"></p>
		<input type="file" id="uploadId"/>
	</div>
	</div>

	<div>
  		<span>
  			Página: <span id="page_num"></span> / <span id="page_count"></span>
  		</span>
	</div>
	<canvas id="the-canvas"></canvas>
	
	<script type="text/javascript">
	
		$('#icon-green').hide();
	
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
			 $("#fileDiv").hide()
			var file = value.target.files[0];
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
		});
		 
		 let isRecording = false;
		 
		function Record(){
			$.ajax({
				url: '/Record.html',
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
			//Espacio
			case 32:
				Record();
				break;
			}
			
		})
	</script>
</body>
</html>
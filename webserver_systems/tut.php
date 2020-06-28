<!DOCTYPE HTML>
<html>
	
	<!-- Header -->
	<title>PHS Vitre</title>

	<head>
		<?php include 'resource.php';?>
	</head>
	
	<!-- Body -->
	<body>
		<main role="main" class="container">
			<!-- Slideshow logic -->
			
			<div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
			  <ol class="carousel-indicators">
				<li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
				<li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
				<li data-target="#carouselExampleIndicators" data-slide-to="2"></li>
			  </ol>
			  <div class="carousel-inner">
				<div class="carousel-item active">
				  <img class="d-block w-100" src="/images/tutorial/tutorial_page_page 01@4x.png" alt="First slide">
				</div>
				<div class="carousel-item">
				  <img class="d-block w-100" src="/images/tutorial/tutorial_page_page 02@4x.png" alt="Second slide">
				</div>
				<div class="carousel-item">
				  <img class="d-block w-100" src="/images/tutorial/tutorial_page_page 03@4x.png" alt="Third slide">
				</div>
			  </div>
			  <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
				<span class="carousel-control-prev-icon" aria-hidden="true"></span>
				<span class="sr-only">Previous</span>
			  </a>
			  <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
				<span class="carousel-control-next-icon" aria-hidden="true"></span>
				<span class="sr-only">Next</span>
			  </a>
			</div>
			
		</main>
	</body>
	
	<!-- Style -->
	<style>
		.tut_img {
			
		}
	</style>
	
	<?php include 'scripts/loader.php'; ?>
	
</html>
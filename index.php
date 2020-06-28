<!DOCTYPE HTML>
<html>
	
	<title>Vitre</title>
	
	<!-- Header -->
	<head>
		<?php 
			include 'resource.php';
			include 'navbar.php';
			include_once '/scripts/utility/SubjectManager.php';
		?>

	</head>
	
	<!-- Body -->
	
	<body>
		<main role="main" class="container">
			<div class="row justify-content-center align-items-center">
				<!-- Canvas -->
				<div class="content">
					
					<div class="card">
					  <h5 class="card-header text-center"><b>Recent Activities</b></h5>
					  <div class="card-body">
						<div class="recent-activities">
							<div class="list-group text-left">
								<?php SubjectManager::constructFeed(); ?>
							</div>
						</div>
					  </div>
					</div>
					<!--<span id = "semlabel"> Semester Progress </span>-->
					
					<!--<div id="sembar"> </div>-->
					
					
				</div>
			</div>
		</main>	
	</body>

	<!-- Styles -->
	<style>
		
		.card {
			border-radius: 15px 15px 15px 15px;
			border-color: #17566B;
			border-width: 3px;
		}
		.card .card-header {
			border-radius: 5px 5px 0px 0px;
			border-bottom: 4px solid #428095;
			background-color: #17566B;
			color: #FFFFFF;
		}
		.content {
			padding: 1em;	
			width: 600px;
			margin-top: 2em;
		}

		#query {
			position: fixed;
			bottom: 20px;
			z-index: 999999;
			right: 20px; 
			width: 100px;
			height: 100px;
			outline: none;
		}
		
		.list-group .list-group-item {
			border-radius: 15px 15px 15px 15px;
			border-color: #808080;
			border-width: 1px;
		}

		.recent-activities {
			margin: -10px -10px -10px -10px;
			height: 300px;
			cursor: pointer;
			overflow-y: scroll;
		}
		
		.no-act {
			text-align: center; 
			flex-direction: column;
			display: flex; 
			align-items:center; 
			justify-content: center;
		}
	</style>
	

	<!-- Scripts -->
	<?php include 'scripts/loader.php';?>
	<script src="scripts/navbar.js"> </script>
	<script src="libs/particles.min.js"> </script>
	<script src="scripts/pages/igrade.js"> </script>
	<script src="scripts/pages/myparticles.js"> </script>
	<script src="libs/progressbar.min.js"> </script>
	<script src="libs/notify.min.js"> </script>
	
	<script>
		/*var bar = new ProgressBar.Line(sembar, {
		  strokeWidth: 4,
		  easing: 'easeInOut',
		  duration: 1400,
		  color: '#F1C765',
		  trailColor: '#eee',
		  trailWidth: 1,
		  svgStyle: {width: '100%', height: '100%'},
		});

		bar.animate(0.6);
		*/

		$.notify("Welcome back, <?php echo getToken() -> firstname; ?>!", {
			position: "top left",
			className: "success"
		});
		


		$('.list-group-item').on('click', function(event) {
			var id = $(this).attr('data-id');
			$.redirect('subjectpage.php', {'s_id': id}, "POST");
		});
		
	</script>
	
	<?php include 'footer.php'; ?>
</html>

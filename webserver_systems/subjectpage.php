<!DOCTYPE HTML>
<html>
	
	<!-- Header -->
	<head>
		<?php include 'resource.php';?>
		<?php include 'scripts/utility/ItemFactory.php';?>
		<?php include 'navbar.php';?>
		<?php include 'scripts/utility/SubjectViewer.php';?>
	</head>

	<title> <?php echo $sub['name']; ?></title>
	
	<!-- Styles -->
	<link href="/styles/pages/subjectpage.css" rel="stylesheet" type="text/css">

	<!-- Body-->
	<body>
		<main role="main" class="container">
		<div id="subjectCard" class="card">
			<h3 id="subjectCardHeader" class="card-header text-center"><b><?php echo $sub['name']; ?></b></h3>
			<div class="card-body">
			<div class="subject-header">
				<img class="subject-img" src="<?php echo $sub['icon']; ?>"/>
				
				<!--<h2></h2>-->

				<div class="flex-nowrap">
					<img class="icon" src="/images/icons/person.svg" alt="Teacher">
					<small class="text-muted"><?php echo $sub['teacher']; ?></small>
					<img class="icon" src="/images/icons/tags.svg" alt="Uploaded"> </img>
					<span class="badge badge-info"><?php echo $sub['semester']; ?></span>
					<span class="badge badge-primary"> Midterms </span>
					<span class="badge badge-warning"><?php echo $sub['track']; ?></span>
				</div>
			</div> 
			<hr>
			
			<!--
			<div class="subject-header">
				<img class="subject-img" src="<?php echo $sub['icon']; ?>"/>
				
				<h2><?php echo $sub['name']; ?></h2>

				<div class="flex-nowrap">
					<img class="icon" src="/images/icons/person.svg" alt="Teacher">
					<small class="text-muted"><?php echo $sub['teacher']; ?></small>
					<img class="icon" src="/images/icons/tags.svg" alt="Uploaded"> </img>
					<span class="badge badge-info"><?php echo $sub['semester']; ?></span>
					<span class="badge badge-warning"><?php echo $sub['track']; ?></span>
				</div>
			</div> 
			-->
			

			<!-- Cards -->
			<div class="card component">
				<!-- Written Work -->
				<div class="card-header" id="headingOne">
					<h5 class="mb-0">
						<button class="btn btn-link" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
							<i id="headerIcon" class="fas fa-pencil-alt"></i> Written Work <small> (<?php echo $sub['ww_w']; ?>%) </small> 
						</button>
					</h5>
				</div>
				
				<div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
					<div class="card-body">
						<ul class="list-group list-group-flush">
							<?php SubjectManager::constructGrades(0, $s_id); ?>
						</ul>
					</div>
				</div>

				<!-- Performance Tasks -->
				<div class="card-header" id="headingTwo">
					<h5 class="mb-0">
						<button class="btn btn-link" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo">
							<i id="headerIcon" class="fas fa-tachometer-alt"></i> Performance Tasks <small> (<?php echo $sub['pt_w']; ?>%) </small>
						</button>
					</h5>
				</div>
							
				<div id="collapseTwo" class="collapse show" aria-labelledby="headingTwo" data-parent="#accordion">
					<div class="card-body">
						<ul class="list-group list-group-flush">
							<?php SubjectManager::constructGrades(1, $s_id); ?>
						</ul>
					</div>
				</div>
				
				<!-- Quarterly Assessment -->				
				<div class="card-header" id="headingThree">
					<h5 class="mb-0">
						<button class="btn btn-link" data-toggle="collapse" data-target="#collapseThree" aria-expanded="true" aria-controls="collapseThree">
							<i id="headerIcon" class="fas fa-paperclip"></i> Quarterly Assessment <small> (<?php echo $sub['qa_w']; ?>%) </small>
						</button>
					</h5>
				</div>

				<div id="collapseThree" class="collapse show" aria-labelledby="headingThree" data-parent="#accordion">
					<div class="card-body">
						<ul class="list-group list-group-flush">
							<?php SubjectManager::constructGrades(2, $s_id); ?>
						</ul>
					</div>
				</div>
			</div>
			</div>
			</main>
		</body>

		<!-- Scripts -->
		<?php include 'scripts/loader.php';?>
		<script src="scripts/navbar.js"> </script>
</html>
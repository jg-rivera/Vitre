<!DOCTYPE HTML>
<html>
	
	<!-- Header -->
	<title>My Subjects</title>

	<head>
		<?php include 'resource.php';?>
		<?php include 'navbar.php';?>
		<?php include_once 'scripts/utility/SubjectManager.php';?>
	</head>
	
	<!-- Body -->
	<body>
		<main role="main" class="container">
			<br>
			<div class="card" id="root">
				<h5 class="card-header text-center"><b>My Subjects</b></h5>	
				<div class="card-body">			
					<div class="input-group mb-3">
					  <input type="text" id="search" class="form-control" placeholder="Search subject..." aria-label="Search Subject" aria-describedby="basic-addon2">
					  <div class="input-group-append">
						<button class="btn btn-outline-secondary" id="clear" type="button">
							<i class="fas fa-times"></i>
						</button>
					  </div>
					</div>
					
					<hr>
					<div class="subject-cards">
						<?php SubjectManager::constructSubjects(); ?>
						<div class="no-sub d-none">
							<i class="fa fa-ellipsis-h" style="color:#808080;"> </i>
							<span class="text-muted"> No subject(s) found.</span>
						</div>
					</div>
				</div>
			</div>
		</main>
	</body>

	<style>
	

	.helper {
		display: inline-block;
		height: 100%;
		vertical-align: middle;
	}

	.no-sub {
		text-align: center; 
		flex-direction: column;
		display: flex; 
		align-items:center; 
		justify-content: center;
	}

	#cardImg  {
		vertical-align: middle;
		width: 64px;
		height: 64px;
		margin-right: 0.5em;
	}
	
	.subject-cards .card {
		border-width: 1px;
		border-radius: 10px 10px 10px 10px;
	}
	.subject-cards .card .card-title {
		margin-top: 0.5em;
		margin-bottom: 0.1em;
	}
	
	.subject-cards .card .card-block {
		padding-bottom: 0.6em;
	}
	
		
	#root {
		border-radius: 15px 15px 15px 15px;
		border-color: #17566B;
		border-width: 3px;
	}
	
	#root .card-header {
		border-radius: 5px 5px 0px 0px;
		border-bottom: 4px solid #428095;
		background-color: #17566B;
		color: #FFFFFF;
	}
	
	</style>
	
	<?php include 'footer.php';?>
	<?php include 'scripts/loader.php'; ?>
	
	<!-- Scripts -->
	<script>
		$('.subject-cards .card').on('click', function(event) {
			var id = $(this).attr('data-id');
			$.redirect('subjectpage.php', {'s_id': id}, "POST");
		});		
		
		$.extend($.expr[':'], {
		  'containsi': function(elem, i, match, array)
		  {
			return (elem.textContent || elem.innerText || '').toLowerCase()
			.indexOf((match[3] || "").toLowerCase()) >= 0;
		  }
		});

		var allSubs = $('.subject-cards').find('.card .row .col .card-block .card-title').length;
		
		$('#search').keyup(function (){
			$('.s-card').removeClass('d-none');
			var filter = $(this).val();
			
			// Hide all irrelevant results			
			var toHide = $('.subject-cards').find(
				'.card .row .col .card-block .card-title:not(:containsi("'+filter+'"))');
			toHide.parents('.s-card').addClass('d-none');
				
			var shown = allSubs - toHide.length;
			
			if (shown > 0)
				$('.no-sub').addClass('d-none');
			else
				$('.no-sub').removeClass('d-none');
		});
		
		$('#clear').on('click', function(event) {
			if (allSubs > 0)
				$('.no-sub').addClass('d-none');
			$('.s-card').removeClass('d-none');
			$('#search').val('');
		});
		
		
	</script>
	
	<script src="scripts/navbar.js"> </script>
</html>
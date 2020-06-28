<!DOCTYPE HTML>
<html>
	
	<title>My Account</title>
	
	<!-- Header -->
	<head>
		<?php include 'resource.php';?>
		<?php include 'navbar.php';?>
	</head>
	
	<!-- Body -->
	<body>
		<main role="main" class="container">
			<br>
			<div class="card">
				<h5 class="card-header text-center"><b>Account Settings</b></h5>
				
				<div class="card-body">
					<form action="/scripts/save_account.php" method="post">
						<ul class="list-group list-group-flush">
							<li class="list-group-item">
								<p class="setting mb-1"> <b> Account Name </b></p>
								<br>
								<input name="account_name" type="text" class="form-control" id="account_name" value="<?php echo getToken() -> firstname . " " . getToken() -> lastname; ?>" maxlength ="128" disabled>
								<span class="float-right"> 
									<small> <a href="">Request Name Correction</a> </small>
								</span>
								<div class="row spacer"></div>
							</li>
							<li class="list-group-item">
								<p class="setting mb-1"> <b> Account ID </b></p>
								<br>
								<input name="account_username" value="<?php echo getToken() -> username; ?>" type="text" class="form-control" id="account_username" maxlength ="128">
								<span class="float-right" style="color:red;"> 
									<small> This username is already taken. </small>
								</span>
								<div class="row spacer"></div>
							</li>
							<li class="list-group-item">
								<p class="setting mb-1"> <b> New Password </b></p>
								<br>
								<div class="input-group">
									<input name="account_pwd_new" class="form-control" type="password">
								</div>
								<br>
								<p class="setting mb-1"> <b> Confirm New Password </b></p>
								<br>
								<input name="account_pwd_confirm" class="form-control" type="password">
								<div class="row spacer"></div>				
							</li>
							<li class="list-group-item">
								<p class="setting mb-1"> <b> Other Tweaks </b></p>
								<br>
								<label class="custom-control custom-checkbox" data-toggle="popover" data-content="Disable this if you have lagging issues within the Vitre.">
									<input type="checkbox" class="custom-control-input">
									<span class="custom-control-indicator"></span>
									<span class="custom-control-description">Enable animations and visual effects</span>
								</label>
									<br>
								<div class="row spacer"></div>
								<div class="float-right">
									<button type="submit" id="loginBtn" class="btn btn-primary">Save</button>
								</div>
							</li>
						</ul>
					</form>	
				</div>
			</div>
		</main>
	</body>

	<!--Style-->
	<style>
		a, a:hover {
			color:#333
		}
		.spacer {
			margin-top: 10px;
		}
		.setting {
			display: inline;
			color: #808080;
		}
		.card btn btn-link {
			color: #FFFFFF;
			text-decoration: none;
		}
		
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
		
	</style>
	
	<!-- Scripts-->
	<?php include 'scripts/loader.php';?>
	
	<script src="scripts/navbar.js"> </script>
	<script src="scripts/pages/account.js"> </script>
	
	<script> 
		$(function () {
			$('[data-toggle="popover"]').popover({trigger:'hover',
			animation: false,
			placement: 'bottom'});
		});
	</script>

</html>
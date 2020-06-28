<!DOCTYPE HTML>
<html>

	<title>Portal</title>
	
	<!-- Header -->	
	<head>
		<?php 
			include 'resource.php';
			include 'scripts/utility/TokenUtils.php';
			portalCheck();
		?>
		
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<a class="navbar-brand mx-auto" href="">
				<img id="dashboardIcon" src="/images/textual_logo@3x.png"/>
			</a>
		</nav>
	</head>
	
	<!-- Style -->
	<style> 
		a, a:hover {
			color:#438196;
		}
		
		#eye_btn {
			background-color: #ffffff;
		}
		
	</style>
	
	<!-- Body -->
	<body>
		<main role="main" class="container">
			<br>
			<div class="container"  id="loginContainer">
				
				<!--<h3 class="title" style="color: #438196;">Account Portal</h3>-->
				<img class="img-fluid" id="welcomeImg" src="images/Welcome-Screen.png">
				<br>				
					<form action="/scripts/auth.php" method="post">
						<div class="row justify-content-center">
							<div class="input-fields">
								<div class="form-group row">
									<label for="username">
										<b style="color: #438196;">
											Username
										</b>
									</label>
									<input name="username" type="text" class="form-control" id="id" maxlength ="128">
								</div>
								<div class="form-group row" id="show_hide_password">
									<label for="password">
										<b style="color: #438196;">
											Password
										</b>
									</label>
								<div class="input-group">
									<input name="password" class="form-control" type="password">
									<div id="eye_btn" class="input-group-addon">
										<a href="">
											<i class="fa fa-eye-slash" aria-hidden="true"></i>
										</a>
									</div>
								</div>
							</div>
							<?php 
								if (isset($_GET["s"]) && $_GET["s"] == 'f') {
									echo "<span style='color:red;'>Incorrect username or password.</span>";
								}
							?>
							<div class="form-check">
								<label for="remember" class="form-check-label">
									<input name="remember" class="form-check-input" type="checkbox"> 
									<b style="color: #438196;"> Remember Me </b>
								</label> <br>
								<small style="color: #438196;">
									<b> Forgot your password? </b>
								</small>
								<small>
									<b>
										<a href>Click here.</a> 
									</b>
								</small>
							</div>
							<button type="submit" id="loginBtn" class="btn btn-primary">Login</button>
						</div>
					</div>
				</form>
			</div>
		</main>
	</body>

	<!-- Scripts -->
	<?php include 'scripts/loader.php'; ?>
	
	<script src="scripts/auth.js"> </script>
</html>
<!-- Navbar -->
<?php include 'scripts/utility/TokenUtils.php'; checkToken(); ?>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">

	<a class="navbar-brand" href="" >
		<img id="dashboardIcon" src="/images/textual_logo@3x.png"/>
	</a>
	
	<button class="navbar-toggler custom-toggler" type="button" data-toggle="collapse" data-target="#navbarVitre" aria-controls="navbarVitre" aria-expanded="false" aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"/>
	</button>

	<div class="collapse navbar-collapse" id="navbarVitre">
		<!-- Float-Left -->
		<ul class="nav navbar-nav mr-auto">
			<li class="nav-item">
				<a class="nav-link" href="/">Home</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="/account.php">My Account</a>
			</li>
			<li class="nav-item">
				<a class="nav-link" href="/subjects.php">My Subjects
					<span class="badge badge-light" style="vertical-align: middle;"> <?php echo getToken() -> subject_count; ?></span>
				</a>
			</li>
			<li class="nav-item">
				<a class="nav-link disabled" href="#">My Teachers</a>
			</li>
		</ul>
		
		<!-- Float-Right -->
		<ul class="navbar-nav ml-auto">
			<li role="separator" class="divider"/>
			<li class="nav-item">
				<a class="nav-link">
					Hi, <?php echo getToken() -> firstname ?>!
				</a>
			</li>
			<li class="nav-item">
				<button type="button" id="logoutItem" class="btn btn-outline-warning">Logout</button>
			</li>
		</ul>
	</div>
</nav>


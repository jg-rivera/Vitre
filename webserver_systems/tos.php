<!DOCTYPE HTML>
<html>
	
	<!-- Header -->
	<title>PHS Vitre</title>

	<head>
		<?php 
			include 'resource.php';
			include 'scripts/utility/TokenUtils.php';
		?>
		
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<a class="navbar-brand mx-auto" href="">
				P.S.H.S.
				<img id="dashboardIcon" src="/images/textual_logo@3x.png"/>
			</a>
		</nav>
	</head>
	
	<!-- Body -->
	<body>
		<main role="main" class="container">
			<div class="card">
				<div class="card-body">
					<div class="tos_img text-center">
						<img class="img-fluid" src="/images/terms_of_service.png">
					</div>
					<br>
					<p class="text-justify"> 
						<b> Thank you for prompting to use our service! </b> 
						<br> Before we proceed, please read the following terms of service carefully as they contain important information regarding your legal rights, remedies and obligations. You must follow any policies made available to you within the Services as they are collectively beneficial to your prolonged use of the Vitre Early Access Platform.
					</p>
					<h3> Upon Using our Services </h3> <hr>
					<p class="text-justify">
						<ol type="1">
							<li>
								Do not attempt to extract nor claim any of its default elements unless you obtain permission to do so by the owners or other authoritative figures certified by the owners as these elements are original conceptions of the developers.	
								<ul>
									<li> This means that claiming or using assets found inside the web server such as images, icons or even the logo of the Vitre platform are partly prohibited. </li>
									<li> Posters or News articles found in the news page of the Vitre are however an exception as they are made by certified content creators that are given the permission to publish content on the Vitre therefore rights to those works are reserved to the entities that made them readily available. </li>
									<li> We may review content that we believe to violate our policies or the law as well as remove them entirely. </li>
								</ul>
							</li>
							<li>
								Do not tamper with the system that facilitates the web server. This includes hacking, cracking, or interfering with the operations that makes the Vitre functional unless you are a Vitre personnel. 
								<ul> 
									<li> Violators caught may receive consequences from school authorities and may be extruded from the Early Access Program. </li>
								</ul>
							</li>
						</ol>
					</p>
					<h3> Concerning your Vitre Account </h3> <hr>
					<p class="text-justify">
						<ol type="1">
							<li>
								The developers of the Vitre are in nowhere liable for the results that the platform presents in reference to your grades in classes. The data presented are verified and constantly tested to avoid miscalculations and false information. 
								<ul>
									<li>
									In rare cases of system bugs, users may complain and express their feedback by either talking to the developers personally, extending their opinions to the faculty and other trained authorities, or by using the following contacts: 
										<ul>
											<li>pitogoshs.vitre@gmail.com </li>
										</ul>
									</li>
								</ul>
							</li>
							<li>
								The user has all rights to his or her grades and their disclosure. 
								<ul>
									<li>The data in the Vitre, being essentially significant and personal, is securely reinforced to cater to the privacy of the user. All information is encrypted and is assured to be seen only by the user associated to it. Even the developers are unable to extract this information thus emphasizing the privacy as well as the anonymity of each individual users.</li>
									<li>It is not advised that the user show their grades to other people as they are held accountable for them. Signing off before leaving the system is recommended although, for added information, each user account will be signed off automatically after a pre-configured time limit of idle activity (5 minutes).</li>
									<li>In the case that the personal grades of a user are forcefully disclosed by another person either digitally or directly, respective punishments and responsibility are in the hands of the school authorities for violating school policies of maltreatment and forcible actions.</li>
								</ul>
							</li>
						</ol>
					</p>
					<h3> Modifying our Services </h3> <hr>
					<p class="text-justify">
						<ol type="1">
							<li>We are constantly improving and changing our services based on the feedback we receive from the users along with our overall vision of the Vitre. We may add or remove features and fix bugs with updates wherein the users are given advance notice.</li>
							<li>Using our service is entirely voluntary although it is highly recommended that you do so as it has been proven to help you in your journey as students in the campus. </li>
						</ol>
					</p> <br>
					<div class="text-center">
						<form action="/action_page.php">
							<div class="checkbox">
								<label><input type="checkbox"> I agree to the Vitre Early Access Terms of Services. </label>
							</div>
							<small class="text-muted">
									By clicking 'Continue', your Vitre account is now officially activated.
							</small> <br>
						  <button type="submit" id="continue_btn" class="btn btn-secondary">Continue</button> 
						  
						</form>
					</div>
				</div>
			</div>
		</main>
	</body>
	
	<style>
		.card {
			margin-top: 6em;
			margin-bottom: 3em;
		}
		.tos_img {
			height: 100px;
		}
		#continue_btn {
			margin-top: 1em;
		}
		
	</style>
	
	<?php include 'scripts/loader.php'; ?>
	
</html>
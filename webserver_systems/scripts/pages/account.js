$(document).ready(function() {
	$("#animationsBtn").on('click', function(event) {
		event.preventDefault();
		if ($("#animationsBtn").hasClass("active")) {
			
			$("#animationsBtn").addClass("btn-danger");
			$("#animationsBtn").removeClass("btn-success");
			$("#animationsBtn i").addClass("fa-times-circle");
			$("#animationsBtn i").removeClass("fa-check-circle");
			$("#animationsBtn").removeClass("active");
			$("#animationsBtn span").html("Disabled");
		} else {	
			
			$("#animationsBtn").addClass("btn-success");
			$("#animationsBtn").removeClass("btn-danger");
			$("#animationsBtn").addClass("active");
			
			$("#animationsBtn i").addClass("fa-check-circle");
			$("#animationsBtn i").removeClass("fa-times-circle");
			$("#animationsBtn span").html("Enabled");
		}
	});
});
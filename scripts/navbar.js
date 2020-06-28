 $(document).ready(function () {
        var url = window.location.pathname;
        $('ul.nav a[href="'+ url +'"]').parent().addClass('active');
		
        $('ul.nav a').filter(function() {
             return this.href == url;
        }).parent().addClass('active');
    });
	
$('#logoutItem').click(function() {
	$.ajax({
		type: "POST",
		url: "/scripts/logout.php",
		success: function(data) {
			window.location.href = "/portal.php";
		}
	})
});
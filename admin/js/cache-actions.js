$(".act-delete").click(function() {
    var $row = $(this).closest("tr");    // Find the row
    var $hash = $row.find(".hash").text(); // Find the text
    
	$.ajax({
        type: "post",
        url: "/admin/ajax/delete_cache.php",
        data: { 
            hash: $hash
        },
        success: function(result) {
			location.reload();
        },
        error: function(result) {
            alert('Error in deleting cache.');
        }
    });
});
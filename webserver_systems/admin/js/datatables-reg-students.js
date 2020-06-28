// Call the dataTables jQuery plugin
$(document).ready(function() {
  $table = $('#dataTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
			"url": "/admin/ajax/load_table_students.php",
			"type": "post"
		},
		"columnDefs": [ 
			{
				"targets": 4,
				"data": null,
				"defaultContent": 
					'<button class="btn btn-info act-info"> <i class="fas fa-info"></i> </button>'
			},
			{ className: "sid", "targets": [ 0 ] }, 
			{ 'visible': false, 'targets': [] }	
		]
    }),	
	
	// Modal functionality
	$table.on('click', '.act-info', function() {
		var $row = $(this).closest("tr");    // Find the row
		var $sidRaw = $row.find(".sid").text(); // Find the text
		var $sid = $sidRaw.substr(1);
		
		$('.modal-body').load('/admin/ajax/modal_students_info.php?id=' + $sid,function(){
			$('#myModal').modal( {
				show:true
			});
		});
	});
	
	// Delete all students
	$(".act-delete-all").click(function() {
		if (!confirm("Are you sure you want to delete all students?"))
			return;
		
		$.ajax({
			type: "post",
			url: "/admin/ajax/delete_all_students.php",
			success: function(result) {
				alert(result);
				location.reload();
			},
			error: function(result) {
				alert('Error in deleting all students.');
			}
		});
	});
});

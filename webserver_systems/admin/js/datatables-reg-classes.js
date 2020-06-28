// Call the dataTables jQuery plugin
$(document).ready(function() {
  $table = $('#dataTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
			"url": "/admin/ajax/load_table_classes.php",
			"type": "post"
		},
		"columnDefs": [ 
			{
				"targets": [6],
				"data": null,
				"defaultContent": 
					'<div class="btn-group" role="group" aria-label="Basic example">' +
					'<button type="button" class="btn btn-info act-info"><i class="fas fa-fw fa-info"></i></button>' +
					'<button type="button" class="btn btn-danger act-delete"><i class="fas fa-fw fa-trash-alt"></i></button></div>'
			},
			{ className: "sid", "targets": [0] }, 
			{ 'visible': false, 'targets': [2] }	
		]
    }),	
	
	// Modal functionality
	// Handle info
	$table.on('click', '.act-info', function() {
		var $row = $(this).closest("tr");    // Find the row
		var $sidRaw = $row.find(".sid").text(); // Find the text
		var $sid = $sidRaw.substr(1); // Subject id
		
		$('.modal-body').load('/admin/ajax/modal_classes_info.php?id=' + $sid,function(){
			$('#classInfoModal').modal( {
				show: true
			});
		});
	});
	
	// Handle deletion
	$table.on('click', '.act-delete', function() {
		var $row = $(this).closest("tr");    // Find the row
		var $sidRaw = $row.find(".sid").text(); // Find the text
		var $sid = $sidRaw.substr(1); // Subject id
		
		if (!confirm("Are you sure you want to delete this class?"))
			return;
		
		$.ajax({
			type: "post",
			url: "/admin/ajax/delete_class.php",
			data: { 
				id: $sid
			},
			success: function(result) {
				location.reload();
			},
			error: function(result) {
				alert('Error in deleting class.');
			}
		});
	});
	
	// Register subject
	$(".act-register").click(function() {
		$('.modal-body').load('/admin/ajax/modal_classes_registry.php',function(){
			$('#classRegistryModal').modal( {
				show: true
			});
		});
	});
	
	$(".act-register-confirm").click(function() {
		var $header = $("#classRegistryModal .modal-dialog .modal-content .modal-body");
		var $name = $header.find("#name").val();
		var $icon_name = $header.find("#icon_name").val();
		var $instructor = $header.find("#instructor :selected").text();
		var $track = $header.find("#track :selected").text();
		var $semester = $header.find("#semester :selected").text();
		
		alert($name + $icon_name + $instructor + $track + $semester);
		$.ajax({
			type: "post",
			url: "/admin/ajax/register_class.php",
			data: { 
				name: $name,
				icon_name: $icon_name,
				instructor: $instructor,
				track: $track,
				semester: $semester
			},
			success: function(result) {
				location.reload();
			},
			error: function(result) {
				alert('Error in registering class.');
			}
		});
	});
});

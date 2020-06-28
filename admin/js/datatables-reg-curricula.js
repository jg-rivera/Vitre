// Call the dataTables jQuery plugin
$(document).ready(function() {
  $table = $('#dataTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
			"url": "/admin/ajax/load_table_curricula.php",
			"type": "post"
		},
		"columnDefs": [ 
			{
				"targets": [2],
				"data": null,
				"defaultContent": 
					'<div class="btn-group" role="group" aria-label="Basic example">' +
					'<button type="button" class="btn btn-info act-info"><i class="fas fa-fw fa-info"></i></button>' +
					'<button type="button" class="btn btn-success act-edit"><i class="fas fa-fw fa-pencil-alt"></i></button>' + 
					'<button type="button" class="btn btn-danger act-delete"><i class="fas fa-fw fa-trash-alt"></i></button>' + '</div>'
			},
			{ className: "tid", "targets": [0] }
		]
    }),	
	
	// Modal functionality
	// Handle info
	$table.on('click', '.act-info', function() {
		var $row = $(this).closest("tr");    // Find the row
		var $tidRaw = $row.find(".tid").text(); // Find the text
		var $tid = $tidRaw.substr(1); // Teacher id
		
		$('.modal-body').load('/admin/ajax/modal_curricula_info.php?id=' + $tid,function(){
			$('#teacherInfoModal').modal( {
				show: true
			});
		});
	});
	
	// Handle deletion
	$table.on('click', '.act-delete', function() {
		var $row = $(this).closest("tr");    // Find the row
		var $tidRaw = $row.find(".tid").text(); // Find the text
		var $tid = $tidRaw.substr(1); // Subject id
		
		if (!confirm("Are you sure you want to delete this curriculum?"))
			return;
		
		$.ajax({
			type: "post",
			url: "/admin/ajax/delete_curriculum.php",
			data: { 
				id: $tid
			},
			success: function(result) {
				location.reload();
			},
			error: function(result) {
				alert('Error in deleting curriculum.');
			}
		});
	});
	
	// Register subject
	$(".act-register").click(function() {
		$('.modal-body').load('/admin/ajax/modal_teachers_registry.php',function(){
			$('#teacherRegistryModal').modal( {
				show: true
			});
		});
	});
	
	$(".act-register-confirm").click(function() {
		var $header = $("#teacherRegistryModal .modal-dialog .modal-content .modal-body");
		var $name = $header.find("#name").val();
		var $honorific = $header.find("#honorific :selected").text();
		
		$.ajax({
			type: "post",
			url: "/admin/ajax/register_curriculum.php",
			data: { 
				name: $name,
				honorific: $honorific
			},
			success: function(result) {
				location.reload();
			},
			error: function(result) {
				alert('Error in registering teacher.');
			}
		});
	});
});

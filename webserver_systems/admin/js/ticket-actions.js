
// View ticket action from the table
$(".act-view").click(function() {
    var $row = $(this).closest("tr");    // Find the row
    var $idRaw = $row.find(".ticket_id").contents().get(0).nodeValue; // Might break with <br> tags
	var $id = $idRaw.substr(1);
	
	$('.modal-body').load('/admin/ajax/modal_tickets_info.php?id=' + $id,function(){
			$('#ticketModal').modal( {
				show:true
			});
	});
});

// Delete ticket action from the table
$(".act-delete").click(function() {
    var $row = $(this).closest("tr");    // Find the row
    var $idRaw = $row.find(".ticket_id").contents().get(0).nodeValue; // Might break with <br> tags
	var $id = $idRaw.substr(1);
	
	$.ajax({
        type: "post",
        url: "/admin/ajax/delete_ticket.php",
        data: { 
            ticket_id: $id
        },
        success: function(result) {
			location.reload();
        },
        error: function(result) {
            alert('Error in deleting ticket.');
        }
    });
});

// Update ticket action, from the modal itself.
$(".act-update").click(function() {
	var $header = $("#ticketModal .modal-dialog .modal-content .modal-body");
	var $data = $header.find(".tid").text();
	var $state = $header.find(".state-dropdown :selected").text();
	
	var $id = $data.match(/\d/g);
	$id = $id.join("");	
	
	$.ajax({
        type: "post",
        url: "/admin/ajax/update_ticket.php",
        data: { 
            ticket_id: $id,
			ticket_state: $state
        },
        success: function(result) {
			location.reload();
        },
        error: function(result) {
            alert('Error in updating ticket.');
        }
    });
});
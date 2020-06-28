// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
			"url": "/admin/ajax/load_table_analytics.php",
			"type": "post"
		}
    });
});

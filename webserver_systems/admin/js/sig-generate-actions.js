
    $("#generate").click(function() {
        $.ajax({
            type: "post",
            url: "/admin/ajax/generate_sha256.php",
            data: {
                raw: $("#raw-data").val()
            },
            success: function(result) {
                $("#result").html(result);
            }
        });
    });
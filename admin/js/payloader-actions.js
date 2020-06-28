
    $("#decrypt").click(function() {
        $.ajax({
            type: "post",
            url: "/admin/ajax/decrypt_payload.php",
            data: {
                raw: $("#raw-data").val()
            },
            success: function(result) {
                $("#decrypted code").html(result.trim());
            }
        });
    });
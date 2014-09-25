<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>API sample</title>
</head>

<body>

	<form method="post" id="submit_form">
		<input type="submit" value="Submit" id="submit_btn" /> <input
			name="label" value="/save" id="target" />
	</form>
	<form action="/MSS/save" method="post" id="secret_form">
	</form>
	<textarea name="report" cols="80" rows="30" form="secret_form">
{
    "auth": {
        "hash": "040a82e45f964f3ec1a75f5dfe0e97825dd099bc"
    },
    "data": {
        "action": "save",
        "areas": [
            {
                "id": "0",
                "latitude": "1.5664",
                "longitude": "6.35422",
                "radius": "100",
                "settings": {
                    "silent": "yes",
                    "vibrate": "no"
                }
            },
            {
                "id": "1",
                "latitude": "4",
                "longitude": "7",
                "radius": "100",
                "settings": {
                    "silent": "yes",
                    "vibrate": "yes"
                }
            },
            {
                "id": "2",
                "latitude": "2.5",
                "longitude": "3",
                "radius": "25",
                "settings": {
                    "silent": "no",
                    "vibrate": "no"
                }
            }
        ]
    }
}
     </textarea>

</body>
<script
	src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript">
	$('#submit_form').on("submit", function(event) {
		event.preventDefault();
		console.log('BENIS');
		stringToSend = '/MSS' + $('#target').val();
		var frm = $('#secret_form') || null;
		if (frm) {
			frm.attr('action', stringToSend);
			frm.trigger('submit');
		}
	});
</script>
</html>

<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <title>API sample</title>
    </head>

    <body>

        <form method="post" id="submit_form">
            <input type="submit" value="Submit" id="submit_btn" /> <input
                name="label" value="/ask" id="target" />
        </form>
        <form action="/BPServer/ask" method="post" id="secret_form">
        </form>
        <textarea name="report" cols="80" rows="30" form="secret_form">
{
            "type" : "phone",
            "code" : "secret"      
         }
        </textarea>

    </body>
    <script
    src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script type="text/javascript">
        $('#submit_form').on("submit", function (event) {
            event.preventDefault();
            console.log('BENIS');
            stringToSend = '/BPServer' + $('#target').val();
            var frm = $('#secret_form') || null;
            if (frm) {
                frm.attr('action', stringToSend);
                frm.trigger('submit');
            }
        });
    </script>
</html>

[
	<#list areas as c>
		<#if c_index != 0>,</#if>{       
		"hash": "${c.getcircle_hash()}",
		"latitude": "${c.getLatitude()}",
		"longitude": "${c.getLongitude()}",
		"radius": "${c.getRadius()}",
		"settings": {
			"silent":"${c.getSettings().isSilent()?c}",
			"vibrate":"${c.getSettings().isVibrate()?c}"
		}
	}
	</#list>
]

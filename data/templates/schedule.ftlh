<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Schedule</title>
</head>
<body>
<h1>${month} ${year}</h1>
<ul>
<#list days as d>
    <li>
        <a href="/patients?day=${d.num}">
            День ${d.num} (${d.dow}) — записей: ${d.patients}
        </a>
    </li>
</#list>
</ul>
</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Расписание</title>
    <link rel="stylesheet" href="/style.css"> </head>
<body>
<h1>${month} ${year}</h1>
<ul>
<#list days as d>
    <li<#if d.isToday> class="today"</#if>>
        <a href="/patients?day=${d.num}">
            День ${d.num} (${d.dow}) — записей: ${d.patients}
        </a>
    </li>
</#list>
</ul>
</body>
</html>
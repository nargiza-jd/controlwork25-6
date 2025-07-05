<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Расписание</title>
    <link rel="stylesheet" href="/static/css/freemarker.css">
    <link rel="stylesheet" href="/static/css/forms.css">
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<div class="container"> <h1>${month} ${year}</h1>
    <ul>
    <#list days as d>
        <li<#if d.isToday> class="today"</#if>>
            <a href="/patients?day=${d.num}">
                День ${d.num} (${d.dow}) — записей: ${d.patients}
            </a>
        </li>
    </#list>
    </ul>
</div> </body>
</html>
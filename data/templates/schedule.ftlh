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
<div class="container">
    <h1>Расписание на ${currentMonth} ${year}</h1> <ul class="calendar-grid">
    <#list days as d>
        <li<#if d.isToday> class="today"</#if>>
            <a href="/patients?day=${d.number}"> <span class="day-number">${d.number}</span>
                <span class="day-info">${d.dayOfWeek}</span> <span class="day-info">Записей: ${d.patientCount}</span> </a>
        </li>
    </#list>
    </ul>
</div>
</body>
</html>
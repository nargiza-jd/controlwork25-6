<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Расписание</title>
    <link rel="stylesheet" href="/static/css/freemarker.css">
    <link rel="stylesheet" href="/static/css/forms.css">
    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/calendar.css"> </head>
<body>
<div class="container">
    <div class="calendar-header">
        <a href="#" class="nav-button">&lt; Сегодня</a>
        <h1>Расписание на ${currentMonth} ${year}</h1>
        <a href="#" class="nav-button"></a> </div>

    <div class="calendar-grid-container">
        <div class="day-names">
            <div class="day-name">Пн</div>
            <div class="day-name">Вт</div>
            <div class="day-name">Ср</div>
            <div class="day-name">Чт</div>
            <div class="day-name">Пт</div>
            <div class="day-name">Сб</div>
            <div class="day-name">Вс</div>
        </div>
        <div class="calendar-days-grid">
            <#if firstDayOfMonthOffset??>
                <#list 1..firstDayOfMonthOffset as i>
                    <div class="calendar-day empty"></div>
                </#list>
            </#if>

            <#list days as d>
                <a href="/patients?day=${d.number}" class="calendar-day <#if d.isToday>today</#if>">
                    <div class="day-number">${d.number}</div>
                    <#if d.patientCount gt 0>
                        <div class="patient-count">Записей: ${d.patientCount}</div>
                    </#if>
                </a>
            </#list>

            <#if lastDayOfMonthOffset??>
                <#list 1..lastDayOfMonthOffset as i>
                    <div class="calendar-day empty"></div>
                </#list>
            </#if>
        </div>
    </div>
</div>
</body>
</html>
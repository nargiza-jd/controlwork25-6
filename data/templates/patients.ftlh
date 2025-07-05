<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Пациенты – день ${day}</title>
    <link rel="stylesheet" href="/style.css"> </head>
<body>
<h1>Пациенты на дату ${date}</h1>

<#if patients?has_content>
    <ul>
        <#list patients as p>
            <li>
                ${p.time?string["HH:mm"]} - ${p.fullName} (
                <span class="patient-type-${p.type?lower_case}">
                    ${p.type}
                </span>
                ) - ${p.symptoms}
            </li>
        </#list>
    </ul>
<#else>
    <p>Пока никто не записан на эту дату.</p>
</#if>

<p><a href="/schedule">Вернуться к расписанию</a></p>

</body>
</html>
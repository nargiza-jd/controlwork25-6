<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Пациенты – день ${day}</title>
    <link rel="stylesheet" href="/static/css/freemarker.css">
    <link rel="stylesheet" href="/static/css/forms.css">
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<div class="container">
    <h1>Пациенты на дату ${date}</h1>

    <#if patients?has_content>
        <ul>
            <#list patients as p>
                <li>
                    ${p.formattedTime} - ${p.fullName} (
                    <span class="patient-type-${p.type?lower_case}">
                        ${p.type}
                    </span>
                    ) - ${p.symptoms}
                    <a href="/patients/delete?day=${day}&time=${p.time}&fullName=${p.fullName}" class="delete-button">Удалить</a>
                </li>
            </#list>
        </ul>
    <#else>
        <p>Пока никто не записан на эту дату.</p>
    </#if>

    <p>
        <a href="/schedule" class="button-link">Вернуться к расписанию</a>
    </p>
    <p>
        <a href="/patients/add?day=${day}" class="button-link">Добавить нового пациента на ${date}</a>
    </p>
</div>
</body>
</html>
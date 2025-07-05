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
    <h1>Пациенты на ${date}</h1>

    <#if patients?has_content>
        <table>
            <thead>
              <tr>
                <th>Время</th>
                <th>Тип</th>
                <th>ФИО</th>
                <th>Симптомы</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
            <#list patients as p>
              <tr>
                <td>${p.formattedTime}</td>
                <td>${p.type}</td>
                <td>${p.fullName}</td>
                <td>${p.symptoms}</td>
                <td>
                  <a href="/patients/delete?day=${day?c}&time=${p.formattedTime?url('UTF-8')}&fullName=${p.fullName?url('UTF-8')}" class="delete-link">
                      Удалить
                  </a>
                </td>
              </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <p>Пока никто не записан на эту дату.</p>
    </#if>

    <p><a href="/patients/add?day=${day?c}">Добавить пациента</a></p>
    <p><a href="/schedule">Вернуться к расписанию</a></p>
</div>
</body>
</html>
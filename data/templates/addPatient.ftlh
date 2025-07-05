<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Добавить пациента на ${date}</title>
    <link rel="stylesheet" href="/static/css/forms.css">
    <link rel="stylesheet" href="/static/css/freemarker.css">
    <link rel="stylesheet" href="/static/css/style.css">
</head>
<body>
<h1>Добавить нового пациента на ${date}</h1>

<form action="/patients/add" method="post">
    <input type="hidden" name="day" value="${day}">
    <input type="hidden" name="date" value="${date}">

    <div class="form-element">
        <label for="time">Время:</label>
        <input type="time" id="time" name="time" required pattern="([01]?[0-9]|2[0-3]):[0-5][0-9]">
    </div>

    <div class="form-element">
        <label for="fullName">ФИО:</label>
        <input type="text" id="fullName" name="fullName" required placeholder="Полное имя пациента">
    </div>

    <div class="form-element">
        <label for="type">Тип пациента:</label>
        <select id="type" name="type" required>
            <option value="первичный">Первичный</option>
            <option value="вторичный">Вторичный</option>
            <option value="консультация">Консультация</option>
        </select>
    </div>

    <div class="form-element">
        <label for="symptoms">Краткий анамнез (симптомы):</label>
        <textarea id="symptoms" name="symptoms" rows="4" placeholder="Например: головные боли, температура, обследование"></textarea>
    </div>

    <div class="form-element">
        <button type="submit" class="register-button">Записать пациента</button>
    </div>
</form>

<p><a href="/patients?day=${day}">Вернуться к списку пациентов</a></p>
<p><a href="/schedule">Вернуться к расписанию</a></p>

</body>
</html>
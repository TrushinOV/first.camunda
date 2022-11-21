# first.camunda
Учебный проект по camunda

Запускаем приложение Application.java, затем воркеры InterBankPayment и IntraBankPayment.
При запуске тестов не должно быть "висящих" деплойментов. Иначе получим ошибку типа ".. Multiple external tasks found for externalTask".

Текущая настройка: spring.datasource.url: jdbc:h2:file (можно переключить через *.yaml)

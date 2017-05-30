Учебный проект клиента календаря Google на Eclipse RCP с использованием JavaFX

## Настройка среды разработки Eclipse IDE

### Предварительные требования:

  * JDK 8
  * Maven 3.x
  * Eclipse Installer (для установки см. [Установка Eclipse Installer](https://github.com/agentlab/ru.agentlab.parent/wiki/Установка-Eclipse-Installer))

### Как настроить кастомную сборку Eclipse IDE для этого проекта с помощью Eclipse Installer

Используйте настройки ниже и следуйте руководству [Настройка Eclipse IDE под проекты с помощью Eclipse Installer](https://github.com/agentlab/ru.agentlab.parent/wiki/Настройка-Eclipse-IDE-под-проекты-с-помощью-Eclipse-Installer)

Настройки для Eclipse Installer

  * Продукт Eclipse:
    * Eclipse IDE for JavaFX with Maven and Tools
  * Версия продукта:
    * Oxygen (unstable)

  * Проекты (Выделите 4 проекта. Внимание! Нам нужны только эти 4 проекта!):
    * GoogleCalendar
    * OSGi Eclipse e4 RCP JavaFX Projects
    * OSGi Eclipse e4 RCP Projects
    * OSGi Projects
  * Версия целевой платформы:
    * Oxygen

Все остальное в соответствии с руководством.

### Запуск программы из Eclipse IDE

1. Запустить в Eclipse на выполнение конфигурацию запуска CalendarSample из раздела Java Applications.
2. Убедиться в консоли, что пример использования Google Calendar API работает, но требует ID и Secret.
3. Получить ID и Secret от Calendar API на сайте Google для вашей учетной записи.

Дальнейшую информацию по проекту см на [Wiki](https://github.com/agentlab/ru.agentlab.calendar/wiki)

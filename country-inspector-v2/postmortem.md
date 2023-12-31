## Локализация

1. Достаточно сделать класс/объект `MessageSource`, который будет по имени проперти вытаскивать строку локализации
   либо преобразовывать объект в локализованное строковое представление. Локализованные строки можно как
   печатать сразу, так и хранить в полях-сообщениях.
   - Пример: локализация ошибок парсинга команд, `MessageSource.localizeParseError(error: ParseErrorType): String`
   - Пример: локализация выполняемых команд, `MessageSource.localizeCommandAction(action: CommandAction): String`
 
2. Использовались проперти-файлы и `Properties`; загрузка сообщений должна проводиться через `InputStreamReader` c 
   обязательным указанием кодировки, иначе оттуда считается упячка. `MessageBundle` не использовались. 
 
3. Локализация самих команд скорее не оправдывает себя: выглядит согласованно только в самых простых случаях;
   необходимо в общем случае локализовать все аргументы, параметры, флаги (`city airports code`).

## Парсинг команд

1. Текущая реализация токены из входной строки получает разбивкой по пробелам. В случаях, когда пробелы --
   это часть данных (города из нескольких слов, пути файлов), необходим настоящий парсер
   аргументов командной строки. Дополнительные материалы:
   - https://yetanotherchris.dev/csharp/command-line-arguments-parser/
   - https://jawher.me/parsing-command-line-arguments-finite-state-machine-backtracking/
2. Вызов справки для отдельных команд обрабатывается отдельно :(
   Скорее всего, из-за того, что классы `CommandObject` выделены и/или создаются неподходящим образом.

## Необходимые концепции

1. Иерархия уровней команд `Hierarchy`.
2. Список команд, принадлежащих каждому уровню. Классы команд были точно необходимы при их локализации,
   возможно, будут удобны в целом при маппинге. Непонятно, нужна ли им типизация `H: Hierarchy`. 
   Возможно, этот класс не нужен, если парсер команд будет сразу отдавать объекты типа `CommandObject`.
3. Команды существуют и могут быть применены в контексте.
   1. Команда применима на своем уровне.
   2. Команды уровня ниже неприменимы. 
   3. Команды уровня выше в общем случае применимы с ограничениями (например, если они не меняют
      состояние модели или меняют его, не разрушая целостности).
   4. Команды могут иметь префикс уровня, таким образом можно явно вызывать команды с другого уровня иерархии,
      а также различать одноименные команды разных уровней.
   5. Если команда введена без префикса, поиск подходящей команды начинается с текущего уровня, таким образом,
      из всех команд с данным именем в приоритете будет команда более низкого уровня иерархии, начиная с текущего. 
   6. Не применимая в данном контексте команда считается ошибкой ввода.
4. Тип `ParseResult`. Подтип ошибки возвращается в случае ошибок ввода или неприменимых команд и содержит
   информацию о типе ошибки; подтип успеха содержит в себе `CommandObject`. 
5. Классы `CommandObject`, относящихся к уровню. Инкапсулируют разобранный инпут. Не уверен, нужна ли им
   дополнительная параметризация типом иерархии `H: Hierarchy`.
6. Классы `CommandAction`. Не зеркалируют структуру иерархии. Выводят информацию о модели,
   ошибки ввода, меняют модель.
7. **(!)**  Классы уровней иерархии `H: Hierarchy` умеют распознавать и отдавать `CommandObject` своего типа
   и умеют такие объекты преобразовывать в `CommandAction`.
   - Попробовать выделить парсер из класса иерархии `H: Hierarchy` и организовать параллельную иерархию
      на уровне парсеров.
   - Парсеры, исходя из текущего уровня иерархии, сами будут определять применимость команды из инпута и выделять
      `CommandObject`.

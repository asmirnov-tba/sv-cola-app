Описание эндпоинтов:  
  
### */api/v1/registration*  
  
Метод GET  
Необходим для регстрации команды.  
Принимает два параметра.  
* teamName - имя команды (может быть не именем, а уникальным кодом). Тип данных - строка  
* deviceId - невидимый пользователю идентификатор, однозначно идентифицирующий устройство. Тип данных - строка  
  
В процессе регистрации проверяется наличие команды в таблице (определяется параметром teamName). В случае если команда допущена поверяется не зарешистрированно ли уже другое устройство на эту команду (по deviceid). После этих проверок за командой закрепляется переданный deviceid и генерируется маршрут (на данный момент для всех одной длинны). Маршрут генерируется случайным выбором точек и вопросов из таблицы точек и вопросов.  
  
В случае успешной регистраци возвращает JSON объект класса Play со следующим полями:  
* id: идентификатор зарегестрированного игрока  
* teamName: Название команды (код, переданный при регистрации)  
* deviceId: уникальный идентификатор устройства (переданный при регистрации)  
* registrationTS: Временная метка регистрации в миллисекундах с 1970-01-01  
  
Возвращает ошибку 401 если команды нет в таблице с участниками или если другой deviceid уже зарегестрирован (это для того чтобы не регились одной командой с множества устройств)  
  
### */api/v1/status*  
  
Метод GET  
Необходим для получения текущего статуса игрока.  
Принимает один параметр.  
* deviceId - невидимый пользователю идентификатор, однозначно идентифицирующий устройство. Тип данных - строка.  
Возвращает JSON объект класса PlayerStatus со следующими полями:  
* playerStatus - текущий статус игрока, строка может принимать три значения:  
    * "GameInProgress" - игра в процессе, пока не выявлен победитель, то есть ни один из игроков не прошел все назначенные ему точки  
    * "Winner" - Игра окончена, игрок стал побелителем  
    * "Loser" - Игра окончна, победил кто-то другой  
* totalPoints - общее количество точек, назначенных игроку  
* pointsPassed - количество пройденных точек  
* curentPoint - объект, описывающий текущую точку. В случае, если игрок прошел все точки (totalPoints = pointsPassed) содержит null. Содержит следующие поля:  
    * Id - идентификатор точки  
    * lat - широта, тип double  
    * lon - долгота, тип double  
* currentQuestion - объект описывающий текущий вопрос. В случае, если игрок прошел все точки (totalPoints = pointsPassed) содержит null. Содержит следующие поля:  
    * Id - идентификатор вопроса  
    * txt - текст вопроса  
    * optionA - вариант А  
    * optionB - вариант B  
    * optionC - вариант C  
    * optionD - вариант D  
* registrationTS: Временная метка регистрации в миллисекундах с 1970-01-01  
  
Возвращает ошибку 401, если переданный deviceid не зарегестрирован  
  
### */api/v1/answer*  
  
Метод GET  
Необходим для передачи игроком ответов на вопросы.  
Принимает два параметра:  
* deviceId - невидимый пользователю идентификатор, однозначно идентифицирующий устройство. Тип данных - строка.  
* answer - ответ на текущий вопрос (который по данному deviceid возвращает эндпоинт status). Строка, может принимать четыре значения: "A", "B", "C", "D", все остальные значения гарантированно будут засчитаны неправильным ответом  
  
Принимает ответ на текущий вопрос игрока. В случае правильного ответа переводит его к следующей точке и следующему вопросу. В случае, если был дан ответ на последний вопрос, игра завершается и игроку назначается статус "Winner", всем остальным назначается статус "Loser"  
  
Возвращает JSON объект класса Response со одним полем:  
* status - текстовое поле,  принимает два значения: "CORRECT" и "INCORRECT", соответсвенно в случае верного или неверного ответов на вопрос.  
  
### *Общая логика работы приложения:*  
1. Пользователи заходят в приложение, регистрируются путем ввода имени команды (или какого-то полученного ими кода, идентифицирующего команду). Клиент взаимодействует с эндпоинтом registration  
2. Приложение получает статус от эндпоинта status с точкой к которой надо следовать и вопросом, который необходимо в этой точке показать  
3. Пользователи приходят на точку, кликают на вопрос, получают вопрос  
4. Пользователи отвечают на вопросы. Приложение передает ответы через эндпоинт answer. В случае неправильного ответа пользователи получабт таймаут. Пока кажется, что его проще булет организовать просто на клиенте, но в принципе можно будет добавить еще один вариант в эндпоинт answer. В случае правильного ответа приложение проверяет статус и, если это была не последняя точка, пользователи отправляются в следующую точку, если это была последняя точка (статус Winner), то пользователь получает сообщение о выигрыше.  
  
Отдельным пунктом - если в любой момент при проверке статуса приложение получает статус Loser - приложение блокируется с сообщением о поражении  

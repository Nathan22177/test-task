# test-task

## vote-gen
Генерирует голоса.
### controllers
* [VoteGeneratorController](src/main/java/dev/nathan22177/votegen/controllers/VoteGeneratorController.java) - принимает вызов для получения голосов.

### exchange
* [Vote](src/main/java/dev/nathan22177/votegen/exchange/Vote.java) - Репрезентует 1 голос.
* [VoteBacth](src/main/java/dev/nathan22177/votegen/exchange/VoteBatch.java) - Служит для передачи голосов n голосования i раунда на сервер обрабатывающий голоса.
* [VotingResult](src/main/java/dev/nathan22177/votegen/exchange/VotingResult.java) - Принимается от обрабатывающего голоса сервера при завершении голосования.

### feedback
* [VoteGeneratorFeedbackController](src/main/java/dev/nathan22177/votegen/feedback/VoteGeneratorFeedbackController.java) - Принимает уведомления об учете голсов изавершении голосования.

### services
* [VoteGeneratorService](src/main/java/dev/nathan22177/votegen/services/VoteGeneratorService.java) - Генерирует голоса, обрабатывает завершение голосования.
* [VoteProcessingChecker](src/main/java/dev/nathan22177/votegen/services/VoteProcessingChecker.java) - Сверяется с сервером обработки о состоянии голосования.

### voters
* [Voters](src/main/java/dev/nathan22177/votegen/voters/Voters.java) - Описывает поведение участников.

## vote-proc
Обрабатывает голоса.

### common
* [Voting](src/main/java/dev/nathan22177/voteproc/common/Voting.java) - Репрезентует голосование.
* [VotingStatus](src/main/java/dev/nathan22177/voteproc/common/VotingStatus.java) - Отображает состояние голосования.

### controllers
* [VoteProcessingController](src/main/java/dev/nathan22177/voteproc/controllers/VoteProcessingController.java) - Принимает вызовы от генерирующего сервера о состоянии голосования.

### feedback
* [VoteGeneratorNotifier](src/main/java/dev/nathan22177/voteproc/feedback/VoteGeneratorNotifier.java) - Уведомляет генерирующий сервер об учете голоса и завершении голосования.

### services
* [VoteGeneratorRequestScheduler](src/main/java/dev/nathan22177/voteproc/services/VoteGeneratorRequestScheduler.java) - Запрашивает голоса у гененрирующего сервера.
* [VoteProcessingService](src/main/java/dev/nathan22177/voteproc/services/VoteProcessingService.java) - Обрабатывает приходящие голоса.

## Возникшие проблемы
* Не понимаю почему но тесты нормально не прогоняются в таске мавена но работают и дебажатся как положенно через идею.
* >каждый /notify должен быть успешно доставлен минимум 1 раз 
  >
  Не уверен в том что у меня адекватная политика ретраев. Прежде не сталкивался.
  
## Комментарии
* Все данные передаются в JSON из коробки через обжект маппер jackson который идет в комплекте с spring-web.
* Обрабатывающий сервер имеет зависимость на генерящий для того чтобы пользоваться классами из exchange. Если нужно было бы делать в разных репах, наверно делал бы интероп который друг к другу тянется. Если надо могу решить проблему более явной сериализацией\десериализацией.
* Использую ломбок исключительно воимя избежания написания одних и тех же дефолтных аксессоров. Могу от него избавиться если надо.

## TODO 
* какой нибудь более централизованный логгер потому что много мест где одни и те-же записи по сути.
* более тщательное тестирование обработки голосов и веб слоя обрабатывающего сервера.
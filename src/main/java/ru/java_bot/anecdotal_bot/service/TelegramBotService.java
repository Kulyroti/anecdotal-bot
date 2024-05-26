package ru.java_bot.anecdotal_bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.java_bot.anecdotal_bot.model.JokeModel;
import ru.java_bot.anecdotal_bot.model.JokeWithCount;

import java.util.List;
import java.util.Optional;

@Service
public class TelegramBotService {

    private final TelegramBot telegramBot;
    private final JokeService jokeService; // Зависимость сервиса для работы с анекдотами

    private boolean isAddingJoke = false;
    private boolean isViewingJoke = false;
    private boolean isDeletingJoke = false;
    private boolean isEditingJoke = false;
    private String jokeIdForEdit;

    public TelegramBotService(@Autowired TelegramBot telegramBot, @Autowired JokeService jokeService) {
        this.telegramBot = telegramBot;
        this.jokeService = jokeService;
        this.telegramBot.setUpdatesListener(updates -> { //Лямбда - регистрируем слушателя обновлений
            updates.forEach(this::handleUpdate); //В лямбде забираем все обновления - и вызываем обработку их
            return UpdatesListener.CONFIRMED_UPDATES_ALL; //Подтверждаем, что все забрали
        }, Throwable::printStackTrace);
    }

    private void buttonClickReact(Update update) { //Реагируем на событие
        //Подготавливаем сообщение на ответ
        SendMessage request = new SendMessage(update.message().chat().id(), "Я не знаю таких команд(") //update.message().chat().id() - Id, в какой чат отправлять сообщение, в данном случае - тому, кто написал
                .parseMode(ParseMode.HTML) //Без понятия, что такое, но было в документации
                .disableWebPagePreview(true) //Без понятия, что такое, но было в документации
                .disableNotification(true) //Без понятия, что такое, но было в документации
                .replyToMessageId(update.message().messageId()); //Делаем наш ответ как ответ на отправленное ранее сообщение
        this.telegramBot.execute(request); //Отправляем подготовленное сообщение
    }

    private void handleUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String command = update.message().text();

            if (command.equals("/start")) {
                // Отправляем приветственное сообщение
                sendMessage(update.message().chat().id(), "Анекдоты не найдены.");
            } else if (command.equals("/add")) {
                // Устанавливаем флаг добавления шутки
                isAddingJoke = true;
                // Просим пользователя ввести текст шутки
                handleAddJoke(update.message().chat().id());
            } else if (command.equals("/all")) {
                // Выводим список всех шуток
                handleViewAllJoke(update.message().chat().id());
            } else if (command.equals("/edit")) {
                // Устанавливаем флаг редактирования шутки
                isEditingJoke = true;
                // Просим пользователя ввести ID шутки для редактирования
                handleAskEditJoke(update.message().chat().id());
            } else if (command.equals("/delete")) {
                // Устанавливаем флаг удаления шутки
                isDeletingJoke = true;
                // Просим пользователя ввести ID шутки для удаления
                handleAskForDeleteJoke(update.message().chat().id());
            } else if (command.equals("/view")) {
                // Устанавливаем флаг просмотра шутки
                isViewingJoke = true;
                // Просим пользователя ввести ID шутки для просмотра
                handleAskViewJoke(update.message().chat().id());
            } else if (command.equals("/random")) {
                // Выполняем функцию обработки случайной шутки
                handleRandomJoke(update.message().chat().id());
            } else if (isAddingJoke) {
                // Сбрасываем флаг добавления шутки
                isAddingJoke = false;
                // Обрабатываем текст шутки
                handleJokeText(update.message().chat().id(), command);
            } else if (isViewingJoke) {
                // Сбрасываем флаг просмотра шутки
                isViewingJoke = false;
                // Получаем userId из объекта Message
                Long userId = update.message().from().id();

                // Обрабатываем ID шутки для просмотра, передавая userId
                handleViewJoke(update.message().chat().id(), command, userId);
            } else if (isEditingJoke) {
                // Проверяем, введен ли уже ID шутки
                if (jokeIdForEdit == null) {
                    // Запоминаем ID шутки для редактирования
                    jokeIdForEdit = command;
                    // Просим пользователя ввести новый текст шутки
                    SendMessage request = new SendMessage(update.message().chat().id(), "Введите новый текст шутки:");
                    this.telegramBot.execute(request);
                } else {
                    Long userId = update.message().from().id();
                    // Вызываем функцию для изменения текста шутки
                    handleEditJoke(update.message().chat().id(), jokeIdForEdit, command, userId);
                    // Сбрасываем переменную ID для редактирования
                    jokeIdForEdit = null;
                    // Сбрасываем флаг редактирования
                    isEditingJoke = false;
                }
            } else if (isDeletingJoke) {
                // Сбрасываем флаг удаления шутки
                isDeletingJoke = false;
                Long userId = update.message().from().id();
                // Вызываем функцию для удаления шутки
                handleDeleteJoke(update.message().chat().id(), command, userId);
            } else if (command.equals("/top5")) {
                top5Jokes(update.message().chat().id());
            } else {
                buttonClickReact(update);
            }

        }
    }

    private void handleAddJoke(Long chatId) {
        // Отправляем сообщение с просьбой ввести текст шутки
        SendMessage request = new SendMessage(chatId, "Введите текст шутки для добавления");
        this.telegramBot.execute(request);
    }

    private void handleJokeText(Long chatId, String jokeText) {
        // Создаем объект JokeModel с полученным текстом шутки
        JokeModel jokeModel = new JokeModel();
        jokeModel.setText(jokeText);

        // Вызываем метод сервиса для создания новой шутки
        Optional<JokeModel> response = this.jokeService.createJoke(jokeModel);

        // Проверяем результат создания шутки
        if (response.isPresent()) {
            // Шутка успешно добавлена, отправляем подтверждение
            SendMessage responseMessage = new SendMessage(chatId, "Шутка успешно добавлена!");
            this.telegramBot.execute(responseMessage);
        } else {
            // Не удалось добавить шутку, отправляем сообщение об ошибке
            SendMessage responseMessage = new SendMessage(chatId, "Не удалось добавить шутку");
            this.telegramBot.execute(responseMessage);
        }
    }

    private void handleViewAllJoke(Long chatId) {
        int currentPage = 0;

        // Получаем список всех шуток из сервиса
        Page<JokeModel> jokesPage = jokeService.getAllJokes(currentPage);

        // Проверяем, есть ли анекдоты
        if (jokesPage.isEmpty()) {
            sendMessage(chatId, "Анекдоты не найдены.");
            return;
        }

        // Отправляем первую страницу анекдотов
        sendJokePage(chatId, jokesPage, currentPage);
    }

    private void sendJokePage(Long chatId, Page<JokeModel> jokesPage, int currentPage) {
        StringBuilder response = new StringBuilder("Страница " + (currentPage + 1) + ":\n\n");
        for (JokeModel joke : jokesPage.getContent()) {
            // Добавляем текст шутки
            response.append("Шутка: ").append(joke.getText()).append("\n");
            // Добавляем ID шутки
            response.append("ID: ").append(joke.getId()).append("\n");
            // Добавляем дату создания
            response.append("Дата создания: ").append(joke.getCreatedDate()).append("\n");
            // Добавляем дату обновления
            response.append("Дата обновления: ").append(joke.getUpdatedDate()).append("\n");
            // Добавляем пустую строку для разделения шуток
            response.append("\n");
        }

        // Отправляем сообщение с текстом всех шуток
        SendMessage request = new SendMessage(chatId, response.toString());
        this.telegramBot.execute(request);
    }

    private void sendMessage(Long chatId, String s) {
        // Отправляем приветственное сообщение с описанием функционала
        SendMessage request = new SendMessage(chatId, "Привет! Я анекдотический чат-бот. У меня есть команды:");
        this.telegramBot.execute(request);
    }

    private void handleAskViewJoke(Long chatId) {
        // Отправляем сообщение с просьбой ввести ID шутки
        SendMessage request = new SendMessage(chatId, "Введите ID шутки, которую хотите посмотреть:");
        this.telegramBot.execute(request);
    }

    private void handleViewJoke(Long chatId, String jokeId, Long userId) {
        // Пытаемся преобразовать введенный текст в числовой ID
        try {
            Long jokeIdLong = Long.valueOf(jokeId);

            // Запрашиваем шутку по ID, передавая userId
            Optional<JokeModel> joke = jokeService.getJokeById(jokeIdLong, userId);

            // Проверяем, найдена ли шутка по ID
            if (joke.isPresent()) {
                // Отправляем сообщение с текстом шутки
                SendMessage request = new SendMessage(chatId, "Шутка: " + joke.get().getText());
                this.telegramBot.execute(request);
            } else {
                // Шутка не найдена, отправляем сообщение об ошибке
                SendMessage request = new SendMessage(chatId, "Шутка с таким ID не найдена");
                this.telegramBot.execute(request);
            }


        } catch (NumberFormatException e) {
            // Введенный текст не является числом, отправляем сообщение об ошибке
            SendMessage request = new SendMessage(chatId, "Некорректный ID. Введите числовое значение.");
            this.telegramBot.execute(request);
        }
    }

    private void handleAskForDeleteJoke(Long chatId) {
        // Отправляем сообщение с просьбой ввести ID шутки для удаления
        SendMessage request = new SendMessage(chatId, "Введите ID шутки для удаления:");
        this.telegramBot.execute(request);
    }

    private void handleDeleteJoke(Long chatId, String jokeId, Long userId) {
        // Пытаемся преобразовать введенный текст в числовой ID
        try {
            Long jokeIdLong = Long.valueOf(jokeId);
            Optional<JokeModel> joke = jokeService.getJokeById(jokeIdLong, userId);
            // Проверяем наличие шутки с введенным ID
            if (joke.isPresent()) {
                // Удаляем шутку из сервиса
                jokeService.deleteJokeById(jokeIdLong);

                // Отправляем сообщение об успешном удалении
                SendMessage request = new SendMessage(chatId, "Шутка успешно удалена!");
                this.telegramBot.execute(request);
            } else {
                // Отправляем сообщение о том, что шутки с таким ID не существует
                SendMessage request = new SendMessage(chatId, "Шутка с таким ID не найдена!");
                this.telegramBot.execute(request);
            }
        } catch (NumberFormatException e) {
            // Введенный текст не является числом, отправляем сообщение об ошибке
            SendMessage request = new SendMessage(chatId, "Некорректный ID шутки. Введите числовой ID.");
            this.telegramBot.execute(request);
        } catch (Exception e) {
            // Произошла ошибка при удалении, отправляем сообщение об ошибке
            SendMessage request = new SendMessage(chatId, "Ошибка при удалении шутки: " + e.getMessage());
            this.telegramBot.execute(request);
        }
    }

    private void handleEditJoke(Long chatId, String jokeId, String newJokeText, Long userId) {
        // Пытаемся преобразовать введенный текст в числовой ID
        try {
            Long jokeIdLong = Long.parseLong(jokeId);

            // Проверяем, существует ли шутка с указанным ID
            Optional<JokeModel> existingJoke = jokeService.getJokeById(jokeIdLong,  userId);
            if (existingJoke.isPresent()) {
                // Создаем объект JokeModel с обновленным текстом
                JokeModel updatedJoke = new JokeModel();
                updatedJoke.setId(jokeIdLong);
                updatedJoke.setText(newJokeText);

                // Вызываем метод сервиса для обновления шутки
                jokeService.changeJokeById(updatedJoke.getId(), updatedJoke);

                // Отправляем сообщение об успешном изменении
                SendMessage request = new SendMessage(chatId, "Шутка успешно изменена!");
                this.telegramBot.execute(request);
            } else {
                // Шутка с указанным ID не найдена, отправляем сообщение об ошибке
                SendMessage request = new SendMessage(chatId, "Шутка с таким ID не найдена");
                this.telegramBot.execute(request);
            }
        } catch (Exception e) {
            // Произошла ошибка при изменении, отправляем сообщение об ошибке
            SendMessage request = new SendMessage(chatId, "Ошибка при изменении шутки: " + e.getMessage());
            this.telegramBot.execute(request);
        }
    }

    private void handleAskEditJoke(Long chatId) {
        // Сбрасываем переменную ID для редактирования
        jokeIdForEdit = null;

        // Отправляем сообщение с просьбой ввести ID шутки для редактирования
        SendMessage request = new SendMessage(chatId, "Введите ID шутки для редактирования:");
        this.telegramBot.execute(request);
    }

    private void top5Jokes(Long chatId) {
        // Получаем список всех шуток из сервиса
        List<JokeWithCount> jokesCount = jokeService.getTop5Jokes();

        // Формируем строку с текстом ответа, включающую все шутки
        StringBuilder response = new StringBuilder("5 самых популярных шуток:\n");
        for (JokeWithCount joke : jokesCount) {
            // Добавляем текст шутки
            response.append("Шутка: ").append(joke.getText()).append("\n");
            // Добавляем ID шутки
            response.append("ID: ").append(joke.getId()).append("\n");
            // Добавляем дату создания
            response.append("Дата создания: ").append(joke.getCreatedDate()).append("\n");
            // Добавляем дату обновления
            response.append("Дата обновления: ").append(joke.getUpdatedDate()).append("\n");
            // Добавляем пустую строку для разделения шуток
            response.append("\n");
        }

        // Отправляем сообщение с текстом всех шуток
        SendMessage request = new SendMessage(chatId, response.toString());
        this.telegramBot.execute(request);
    }

    private void handleRandomJoke(Long chatId) {

        Optional<JokeModel> randomJoke = this.jokeService.getRandomJoke();

        // Проверяем, была ли получена случайная шутка
        if (randomJoke.isPresent()) {
            // Отправляем случайную шутку пользователю
            SendMessage responseMessage = new SendMessage(chatId, randomJoke.get().getText());
            this.telegramBot.execute(responseMessage);
        } else {
            // Если случайная шутка не была найдена, отправляем сообщение об ошибке
            SendMessage errorMessage = new SendMessage(chatId, "Не удалось найти случайную шутку.");
            this.telegramBot.execute(errorMessage);
        }
    }

}

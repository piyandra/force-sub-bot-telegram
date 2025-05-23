package org.telegram.forcesub.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.entity.MessageText;
import org.telegram.forcesub.repository.MessageTextRepository;

@Service
public class MessageTextService {

    private final MessageTextRepository messageTextRepository;
    private final String startNotJoin;
    private final String startJoin;
    private final String startWelcome;
    private final String aboutMessage;

    public MessageTextService(
            MessageTextRepository messageTextRepository,
            @Value("${start.message.not.join}") String startNotJoin,
            @Value("${start.message.after.join}") String startJoin,
            @Value("${help.message}") String startWelcome,
            @Value("${help.message}") String aboutMessage
    ) {
        this.messageTextRepository = messageTextRepository;
        this.startNotJoin = startNotJoin;
        this.startJoin = startJoin;
        this.startWelcome = startWelcome;
        this.aboutMessage = aboutMessage;
    }

    public void saveMessageText(MessageInformation messageInformation, String text) {
        MessageText existingMessageText = messageTextRepository.getMessageTextsByMessageInformation(messageInformation);
        if (existingMessageText != null) {
            if (!existingMessageText.getText().equals(text)) {
                existingMessageText.setText(text);
                messageTextRepository.save(existingMessageText);
            }
        } else {
            messageTextRepository.save(MessageText.builder()
                    .messageInformation(messageInformation)
                    .text(text)
                    .build());
        }
    }
    public String getMessageText(MessageInformation messageInformation) {
        MessageText messageText = messageTextRepository.getMessageTextsByMessageInformation(messageInformation);
        if (messageText != null) {
            return messageText.getText();
        }
        return null;
    }

    @PostConstruct
    public void setUpMessageText() {
        messageTextRepository.deleteAll();
        saveMessageText(MessageInformation.START, startNotJoin);
        saveMessageText(MessageInformation.WELCOME, startWelcome);
        saveMessageText(MessageInformation.HELP, startJoin);
        saveMessageText(MessageInformation.ABOUT, aboutMessage);
    }
}

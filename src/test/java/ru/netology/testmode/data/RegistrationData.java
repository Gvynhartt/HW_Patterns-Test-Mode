package ru.netology.testmode.data;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class RegistrationData {
    String login;
    String password;
    String status;
}

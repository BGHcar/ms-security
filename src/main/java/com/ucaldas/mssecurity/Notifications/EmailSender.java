package com.ucaldas.mssecurity.Notifications;
import com.azure.communication.email.*;
import com.azure.communication.email.models.*;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.ucaldas.mssecurity.Services.JwtService;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class EmailSender 
{
    public static Map<String, String> getEnvVariables(String envFilePath) {
        Map<String, String> envVars = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(envFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Buscar el índice del primer signo igual para dividir la línea en nombre y valor
                int equalsIndex = line.indexOf('=');
                if (equalsIndex != -1) {
                    // Obtener el nombre y el valor de la variable de entorno
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    // Eliminar comillas alrededor del valor si están presentes
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    // Agregar la variable y su valor al mapa
                    envVars.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo .env: " + e.getMessage());
        }
        return envVars;
    }


    public void sendEmail(String email, String random)
    { 

        Map<String, String> env = getEnvVariables(".env");

        // Puede obtener la cadena de conexión del recurso en Azure Portal.
        String connectionString = env.get("CONNECTION_STRING");
        String senderAddress = env.get("SENDER_ADDRESS");

        EmailClient emailClient = new EmailClientBuilder().connectionString(connectionString).buildClient();

        EmailAddress toAddress = new EmailAddress(email);
    
        EmailMessage emailMessage = new EmailMessage()
            .setSenderAddress(senderAddress)
            .setToRecipients(toAddress)
            .setSubject("Clave de autenticacion")
            .setBodyPlainText("Su clave de autenticacion es: " + random + "\n Por favor no comparta esta clave con nadie.");

        

        SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage, null);
        PollResponse<EmailSendResult> result = poller.waitForCompletion();
    }


    public static String generateRandomWord() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        // Generar tres letras en mayúsculas
        for (int i = 0; i < 3; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            sb.append(letter);
        }

        // Generar tres dígitos
        for (int i = 0; i < 3; i++) {
            char digit = (char) (random.nextInt(10) + '0');
            sb.append(digit);
        }

        return sb.toString();
    }

}
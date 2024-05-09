package com.ucaldas.mssecurity.Controllers;
import com.ucaldas.mssecurity.Notifications.EmailSender;
import java.util.Map;



import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.Services.EncryptionService;
import com.ucaldas.mssecurity.Services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;


@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private JwtService theJwtService;

    @PostMapping("/login")
    public Map <String, String> login(@RequestBody User theUser,
                        final HttpServletResponse response) throws IOException{
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {
            if (theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theUser.getPassword()))) {

                token = EmailSender.generateRandomWord();
                EmailSender emailSender = new EmailSender();
                theActualUser.setToken(token);
                this.theUserRepository.save(theActualUser);
                emailSender.sendEmail(theActualUser.getEmail(), token);
                

                 // token = theJwtService.generateToken(theActualUser);
            }else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
        }
        return Map.of("id", theActualUser.get_id(), "token", token);
    }

    @PutMapping("/secondauth")
    public String secondAuth(@RequestBody Map<String, String> requestBody,
                             final HttpServletResponse response) throws IOException{
        String id = requestBody.get("id");
        User theUser = this.theUserRepository
                                .findById(id)
                                .orElse(null);
        String token = requestBody.get("token");
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {
            if (theActualUser.getToken().equals(token)) {
                token = theJwtService.generateToken(theActualUser);
                theActualUser.setToken(null);
                this.theUserRepository.save(theActualUser);
            }else {
                // theActualUser.setToken(null);
                // this.theUserRepository.save(theActualUser);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Second auth failed");

            }
        }
        return token;
    }

    @PutMapping("/reset-password")
    public String reseetPass(@RequestBody User theUser,
                        final HttpServletResponse response) throws IOException{
        String token = "";
        User theActualUser = this.theUserRepository.getUserByEmail(theUser.getEmail());
        if (theActualUser != null) {

                token = EmailSender.generateRandomWord();
                EmailSender emailSender = new EmailSender();
                theActualUser.setToken(token);
                this.theUserRepository.save(theActualUser);
                emailSender.sendEmail(theActualUser.getEmail(), token);
                
            }else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
    
        return "message: 'Email sent to your email address'";
    }

    @PutMapping("/change-password/{id}")
    public String changePassword(@PathVariable String id, @RequestBody Map<String, String> requestBody,
                             final HttpServletResponse response) throws IOException{
        User theActualUser = this.theUserRepository
                                .findById(id)
                                .orElse(null);
        String token = requestBody.get("token");
        System.err.println(token);
        if (theActualUser != null) {
            if (theActualUser.getToken().equals(token)) {
                theActualUser.setPassword(theEncryptionService.convertSHA256(requestBody.get("password")));
                theActualUser.setToken(null);
                this.theUserRepository.save(theActualUser);
            }else {

                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "INVALID TOKEN");
            }
        }
        return "message: 'Password changed successfully'";
    }
    
}
    




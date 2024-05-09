package com.ucaldas.mssecurity.Controllers;

import com.ucaldas.mssecurity.Services.EncryptionService;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;

import com.ucaldas.mssecurity.Models.Role;
import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.RoleRepository;
import com.ucaldas.mssecurity.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;


@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private RoleRepository theRoleRepository;
    @GetMapping ("")
    public List<User> findAll(){return this.theUserRepository.findAll();}
    @ResponseStatus(HttpStatus.CREATED)
    
    @PostMapping("public")
    public User create(@Valid @RequestBody User theNewUser) {
        if (!isValidEmail(theNewUser.getEmail())) {
            if (!isValidPassword(theNewUser.getPassword())) {
                System.err.println("La contraseña no cumple con los requisitos mínimos");
                return null;
            }
            System.err.println("El email ya está registrado o no es válido");
            return null;
        }
        theNewUser.setPassword(theEncryptionService.convertSHA256(theNewUser.getPassword()));
        return this.theUserRepository.save(theNewUser);}
    
    @GetMapping("{id}")
    public User findById(@PathVariable String id) {
        User theUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        return theUser;
    }

    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User theNewUser) {
        User theActualUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        if (theActualUser != null) {
            theActualUser.setName(theNewUser.getName());
            theActualUser.setEmail(theNewUser.getEmail());
            theActualUser.setPassword(theEncryptionService.convertSHA256(theNewUser.getPassword()));
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        User theUser = this.theUserRepository
                .findById(id)
                .orElse(null);
        if (theUser != null) {
            this.theUserRepository.delete(theUser);
        }
    }

    @PutMapping("{userId}/role/{roleId}")
    public User matchRole(@PathVariable String userId, @PathVariable String roleId) {
        User theActualUser = this.theUserRepository
                .findById(userId)
                .orElse(null);

        Role theActualRole = this.theRoleRepository
                .findById(roleId)
                .orElse(null);

        if (theActualUser != null && theActualRole != null) {
            if (theActualUser.getRole() == theActualRole) {
                return null;
            }
            theActualUser.setRole(theActualRole);
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }


    @PutMapping("{userId}/unmatch-role/{roleId}")
    public User unMatchRole(@PathVariable String userId, @PathVariable String roleId) {
        User theActualUser = this.theUserRepository
                .findById(userId)
                .orElse(null);

        Role theActualRole = this.theRoleRepository
                .findById(roleId)
                .orElse(null);

        if (theActualUser != null
                && theActualRole != null
                && theActualUser.getRole().get_id().equals(roleId)) {
            theActualUser.setRole(null);
            return this.theUserRepository.save(theActualUser);
        } else {
            return null;
        }
    }

    public boolean isValidEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        
        // Validar la sintaxis del email usando una expresión regular
        boolean isValidFormat = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(email).matches();
        if (!isValidFormat) {
            return false;
        }

        // Verificar si el email ya está registrado en la base de datos
        return theUserRepository.getUserByEmail(email) == null;
    }

    public boolean isValidPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        boolean isValidFormat = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$").matcher(password).matches();
        return isValidFormat;
    }

}

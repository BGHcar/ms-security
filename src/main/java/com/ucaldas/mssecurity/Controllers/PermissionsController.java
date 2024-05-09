package com.ucaldas.mssecurity.Controllers;

import com.azure.core.annotation.Put;
import com.ucaldas.mssecurity.Models.Permission;
import com.ucaldas.mssecurity.Repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {
    @Autowired
    private PermissionRepository thePermissionRepository;
    @GetMapping("")
    public List<Permission> findAll(){
        return this.thePermissionRepository.findAll();
    }

    @PutMapping("{id}")
    public Permission update(@PathVariable String id, @RequestBody Permission thePermission){
        Permission thePermissionToUpdate = this.thePermissionRepository
                .findById(id)
                .orElse(null);
        if (thePermissionToUpdate != null) {
            thePermissionToUpdate.setUrl(thePermission.getUrl());
            thePermissionToUpdate.setMethod(thePermission.getMethod());
            return this.thePermissionRepository.save(thePermissionToUpdate);
        }
        return null;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Permission create(@RequestBody Permission theNewPermission){
        Permission thePermission = this.thePermissionRepository
                .getPermission(theNewPermission.getUrl(), theNewPermission.getMethod());
        if (thePermission != null) {
            System.err.println("Permission already exists");
            return null;
        }
        else
            return this.thePermissionRepository.save(theNewPermission);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Permission thePermission = this.thePermissionRepository
                .findById(id)
                .orElse(null);
        if (thePermission != null) {
            this.thePermissionRepository.delete(thePermission);
        }
    }
}

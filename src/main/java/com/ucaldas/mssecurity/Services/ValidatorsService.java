package com.ucaldas.mssecurity.Services;

import com.ucaldas.mssecurity.Controllers.StatisticController;
import com.ucaldas.mssecurity.Models.Permission;
import com.ucaldas.mssecurity.Models.Role;
import com.ucaldas.mssecurity.Models.RolePermission;
import com.ucaldas.mssecurity.Models.Statistic;
import com.ucaldas.mssecurity.Models.User;
import com.ucaldas.mssecurity.Repositories.PermissionRepository;
import com.ucaldas.mssecurity.Repositories.RolePermissionRepository;
import com.ucaldas.mssecurity.Repositories.UserRepository;
import com.ucaldas.mssecurity.Repositories.StatisticRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidatorsService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private StatisticRepository theStatisticRepository;

    @Autowired
    private PermissionRepository thePermissionRepository;
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;
    private static final String BEARER_PREFIX = "Bearer ";
    public boolean validationRolePermission(HttpServletRequest request,String url,String method){
        boolean success=false;
        User theUser=this.getUser(request);
        if(theUser!=null){
            Role theRole=theUser.getRole();
            System.out.println("Antes URL "+url+" metodo "+method);
            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");
            System.out.println("URL "+url+" metodo "+method);
            Permission thePermission=this.thePermissionRepository.getPermission(url,method);
            System.err.println("Permission "+thePermission);
            System.err.println("Role "+theRole);
            if(theRole!=null && thePermission!=null){
                System.out.println("Rol "+theRole.get_id()+ " Permission "+thePermission.get_id());
                RolePermission theRolePermission=this.theRolePermissionRepository.getRolePermission(theRole.get_id(),thePermission.get_id());
                System.err.println("RolePermission "+theRolePermission);
                Statistic theStatistic=theStatisticRepository.getStatisticByPermission(thePermission.get_id());
                if (theStatistic == null){
                    Statistic newStatistic = new Statistic();
                    newStatistic.setNumberVisits(1);
                    theStatistic =  newStatistic;
                    theStatistic.setPermission(thePermission);
                }
                else
                    theStatistic.setNumberVisits(theStatistic.getNumberVisits()+1);

                
                this.theStatisticRepository.save(theStatistic);
                //thePermission.getStatistic().setNumberVisits(thePermission.getStatistic().getNumberVisits()+1);
                //this.thePermissionRepository.save(thePermission);
                System.out.println("aqui> "+theRolePermission.get_id());
                if (theRolePermission!=null){
                    success=true;
                }
            }else{
                
                success=false;
            }
        }
        return success;
    }
    public User getUser(final HttpServletRequest request) {
        User theUser=null;
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Header "+authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            System.out.println("Bearer Token: " + token);
            User theUserFromToken=jwtService.getUserFromToken(token);
            if(theUserFromToken!=null) {
                theUser= this.theUserRepository.findById(theUserFromToken.get_id())
                        .orElse(null);
                theUser.setPassword("");
            }
        }
        return theUser;
    }
}

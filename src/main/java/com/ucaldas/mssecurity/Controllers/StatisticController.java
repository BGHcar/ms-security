package com.ucaldas.mssecurity.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.mssecurity.Models.Statistic;
import com.ucaldas.mssecurity.Repositories.StatisticRepository;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    
    
    @Autowired
    private StatisticRepository theStatisticRepository;

    // @GetMapping("permission/{permissionId}")
    // public List<Statistic> findRolesByPermission(@PathVariable String permissionId) {
    //     return this.theStatisticRepository.find(permissionId);
    // }

    @GetMapping()
    public List<Statistic> findAll() {
        return this.theStatisticRepository.findAll();
    }

    @GetMapping("{id}")
    public Statistic findById(@PathVariable String id) {
        Statistic theStatistic = this.theStatisticRepository
                .findById(id)
                .orElse(null);
        return theStatistic;
    }


}

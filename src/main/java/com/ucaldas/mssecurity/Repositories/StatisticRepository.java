package com.ucaldas.mssecurity.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ucaldas.mssecurity.Models.Statistic;

public interface StatisticRepository extends MongoRepository<Statistic, String>{
    @Query("{'permission.$id': ObjectId(?0)}")
    Statistic getStatisticByPermission(String permissionId);
}

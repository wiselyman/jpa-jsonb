package com.example.jpajsonb.repository;

import com.example.jpajsonb.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by wangyunfei on 2017/6/26.
 */
public interface PersonRepository extends JpaRepository<Person,Long> {
    @Query(value = "select * from person where info ->> 'name' = :name" , nativeQuery = true)
    List<Person> findByName(@Param("name") String name);
}

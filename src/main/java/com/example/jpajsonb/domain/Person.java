package com.example.jpajsonb.domain;

import com.example.jpajsonb.support.JsonbType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.domain.DomainEvents;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;

/**
 * Created by wangyunfei on 2017/6/26.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "JsonbType", typeClass = JsonbType.class)
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private Map<String,Object> info;
}

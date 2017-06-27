package com.example.jpajsonb;

import com.example.jpajsonb.domain.Person;
import com.example.jpajsonb.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class JpaJsonbApplication {

//	@Bean
	CommandLineRunner saveAndReadJsonb(PersonRepository personRepository){
		return e -> {
			Person p = new Person();
			Map m = new HashMap();
			m.put("name","汪云飞");
			m.put("age",11);
			p.setInfo(m);
			Person returnPerson = personRepository.save(p);
			Map returnMap = returnPerson.getInfo();

			for(Object entry :returnMap.entrySet()){
					log.info(entry.toString());
			}
		};
	}

	//@Bean
	CommandLineRunner queryJsonb(PersonRepository personRepository){
		return e -> {
			List<Person> people = personRepository.findByName("吴亦凡");
			for (Person person : people){
				Map info = person.getInfo();
				log.info(person.getId() + "/" + info.get("name") + "/" +info.get("age"));
			}
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(JpaJsonbApplication.class, args);
	}
}

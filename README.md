在我们项目中经常会遇到数据结构不定的情况，这时普通的关系型数据库不能满足我们的要求。Postgres为我们提供了`jsonb`数据类型，我们可在此类型的字段存储`json`数据，并可对此数据进行查询。本例将结合`hibernate`,`Spring Data JPA`,`Spring Boot`来实现。
### 1. 自定义方言

```java
public class JsonbPostgresDialect extends PostgreSQL94Dialect {

    public JsonbPostgresDialect() {
        this.registerColumnType(Types.JAVA_OBJECT,"jsonb");
    }
}
```

指定方言

`spring.jpa.database-platform: com.example.jpajsonb.support.JsonbPostgresDialect`

### 2. 自定义jsonb数据类型

这里主要实现了`Map`映射`PGObject`(postgres对象类型)，通过`ObjectMapper`来实现两个数据类型的转换。

```java
public class JsonbType implements UserType{

    private final ObjectMapper mapper = new ObjectMapper();;

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            try{
                st.setObject(index, mapper.writeValueAsString(value), Types.OTHER);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public Object deepCopy(Object originalValue) throws HibernateException {
        if (originalValue != null) {
            try {
                return mapper.readValue(mapper.writeValueAsString(originalValue),
                        returnedClass());
            } catch (IOException e) {
                throw new HibernateException("Failed to deep copy object", e);
            }
        }
        return null;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        PGobject o = (PGobject) rs.getObject(names[0]);
        if (o.getValue() != null) {
            try {
                return mapper.readValue(o.getValue(),Map.class);

            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return new HashMap<String, Object>();
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        Object copy = deepCopy(value);

        if (copy instanceof Serializable) {
            return (Serializable) copy;
        }

        throw new SerializationException(String.format("Cannot serialize '%s', %s is not Serializable.", value, value.getClass()), null);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }

        return x.hashCode();
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return ObjectUtils.nullSafeEquals(x, y);
    }

    @Override
    public Class<?> returnedClass() {
        return Map.class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

}
```

### 3. 声明使用

先定义数据类型，再在字段上使用

```java
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

```

### 4.Repository

通过`postgres`原生sql语句查询，本例含义为`json`数据`info`的一个`key`为`name`的值等于。具体的JSON的sql查询方式请参考：
[https://www.postgresql.org/docs/9.5/static/functions-json.html](https://www.postgresql.org/docs/9.5/static/functions-json.html)

```java
public interface PersonRepository extends JpaRepository<Person,Long> {
    @Query(value = "select * from person where info ->> 'name' = :name" , nativeQuery = true)
    List<Person> findByName(@Param("name") String name);
}

```

### 5. 保存和读取测试
```java
@Bean
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
	
```

### 6. 查询测试

```java
@Bean
CommandLineRunner queryJsonb(PersonRepository personRepository){
    return e -> {
        List<Person> people = personRepository.findByName("吴亦凡");
        for (Person person : people){
            Map info = person.getInfo();
            log.info(person.getId() + "/" + info.get("name") + "/" +info.get("age"));
        }
    };
}
```

### 7. 源码地址
[https://github.com/wiselyman/jpa-jsonb](https://github.com/wiselyman/jpa-jsonb)
# Stream to Map

## 說明

Stream要轉成Map主要有兩種需求，

1. 一對一，依某個Property或Value來產生Map
2. 一對多，有Group的概念


### Collectors.toMap()

```java
List<User> users;

Map<String, User> map = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
```

### Collectors.groupingBy()

```java
List<User> users;

Map<Integer, List<User>> map = users.stream().collect(Collectors.groupingBy(User::getAge));

```
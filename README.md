# 강의 정리


## MySQL로 동시성 이슈 해결하는 방법
### Optimistic Lock
lock 을 걸지않고 문제가 발생할 때 처리합니다.
대표적으로 version column 을 만들어서 해결하는 방법이 있습니다.

### Pessimistic Lock (exclusive lock)
다른 트랜잭션이 특정 row 의 lock 을 얻는것을 방지합니다.
A 트랜잭션이 끝날때까지 기다렸다가 B 트랜잭션이 lock 을 획득합니다.
특정 row 를 update 하거나 delete 할 수 있습니다.
일반 select 는 별다른 lock 이 없기때문에 조회는 가능합니다.
```java
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id=:id")
    Stock findByIdWithPessimisticLock(Long id);
```

### named Lock 활용하기
이름과 함께 lock 을획득합니다. 해당 lock 은 다른세션에서 획득 및 해제가 불가능합니다.


## redis 사용법
```java
docker exec -it {docker id} redis-cli // 레디스 이미지
        
127.0.0.1:6379> setnx 1 lock // key: 1, value: lock
(integer) 1
127.0.0.1:6379> setnx 1 lock // 이미 있는 값이어서 실패(0)
(integer) 0
127.0.0.1:6379> del 1
(integer) 1
127.0.0.1:6379> setnx 1 lock // 키값 삭제 하고 다시 넣으니 성공(1)
(integer) 1
```
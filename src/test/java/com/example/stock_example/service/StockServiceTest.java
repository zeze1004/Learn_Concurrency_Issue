package com.example.stock_example.service;

import com.example.stock_example.domain.Stock;
import com.example.stock_example.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void insert() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void delete() {
        stockRepository.deleteAll();
    }

    @Test
    public void decrease_test() {
        // 요청이 하나씩 들어와 재고가 하나씩 차감된다는 전제
        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - 1 = 99

        assertEquals(99, stock.getQuantity());
        System.out.println("test 성공");
    }

    @Test
    public void 동시에_100명이_주문() throws InterruptedException {
        // race condition으로 인해 동시에 수량을 1로 줄여서, 테스트 실패
        // @transiction 주석 처리와 syncronized 메소드 추가하면 테스트 성공
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount); // CountDownLatch란 다른 스레드의 작업이 완료될 때까지 기댜려주는 클래

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown(); // latch의 수를 하나 씩 감소
                }
            });
        }

        latch.await(); // latch가 0이 될 때까지 기다리는 코드

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - (100 * 1) = 0
        assertEquals(0, stock.getQuantity());
    }
}
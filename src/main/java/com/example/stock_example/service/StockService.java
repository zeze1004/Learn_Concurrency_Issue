package com.example.stock_example.service;

import com.example.stock_example.domain.Stock;
import com.example.stock_example.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

//    @Transactional
    // synchronized를 사용해도 @Transactional 어노테이션 때문에 테스트 실패
    // 재고 감소 -> 저장(재고 업데이트) 과정에서 다른 스레드가 재고 감소 이벤트를 시도할 수 있어서 레이스 컨디션 일어남
    // synchronized 는 각 프로세스에서 한 스레드만 접근 가능하도록 보장 가능하기 때문
    // db를 사용해보자!
//    public synchronized void decrease(Long id, Long quantity) { // synchronized: 하나의 스레드만 접근 가능
//        // get stock 재고 가져오기
//        // 재고 감소
//        // 저장
//
//        Stock stock = stockRepository.findById(id).orElseThrow();
//
//        stock.decrease(quantity);
//
//        stockRepository.saveAndFlush(stock);
//
//    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}

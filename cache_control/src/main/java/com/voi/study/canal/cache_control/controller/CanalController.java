package com.voi.study.canal.cache_control.controller;

import com.voi.study.canal.cache_control.config.Constant;
import com.voi.study.canal.cache_control.entity.Book;
import com.voi.study.canal.cache_control.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author : liu·yu
 * date : 2/23/22
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("book")
public class CanalController {

    private BookRepository bookRepository;

    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("count")
    public Long queryBookCount() {
        String bookCount = redisTemplate.opsForValue().get(Constant.REDIS_BOOK_COUNT_KEY);
        if (bookCount != null) {
            log.info("query book count hit cache");
            return Long.parseLong(bookCount);
        }
        return countBook();
    }

    @PostMapping("add")
    public String addBook(@RequestBody Book book) {
        bookRepository.save(book);
        return "success";
    }

    private synchronized long countBook() {
        String bookCount = redisTemplate.opsForValue().get(Constant.REDIS_BOOK_COUNT_KEY);
        if (bookCount != null) {
            log.info("query book count hit cache");
            return Long.parseLong(bookCount);
        }
        log.info("query book count miss cache,query database and set cache!");
        long count = bookRepository.count();
        redisTemplate.opsForValue().set(Constant.REDIS_BOOK_COUNT_KEY, String.valueOf(count));
        return count;
    }

}

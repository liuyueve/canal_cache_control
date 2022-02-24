package com.voi.study.canal.cache_control.repository;

import com.voi.study.canal.cache_control.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : liu·yu
 * date : 2/23/22
 */
public interface BookRepository extends JpaRepository<Book, Integer> {
}

package com.voi.study.canal.cache_control.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author : liu·yu
 * date : 2/23/22
 */
@Data
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String author;

    private Integer price;

}

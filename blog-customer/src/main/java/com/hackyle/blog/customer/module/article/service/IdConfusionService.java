package com.hackyle.blog.customer.module.article.service;

public interface IdConfusionService {
    String encode(long id);

    long decode(String id);

}

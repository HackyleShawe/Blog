package com.hackyle.blog.admin.module.article.service;

public interface IdConfusionService {
    String encode(long id);

    long decode(String id);

}

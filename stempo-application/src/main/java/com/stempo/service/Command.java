package com.stempo.service;

public interface Command<T> {

    T execute();
}

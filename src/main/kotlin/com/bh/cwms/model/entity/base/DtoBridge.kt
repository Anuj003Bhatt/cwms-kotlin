package com.bh.cwms.model.entity.base

interface DtoBridge<T> {
    fun toDto(): T;
}
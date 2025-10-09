package com.shdatalink.framework.common.model;

import lombok.Data;


@Data
public class DictBean<T> implements IDict<T> {
    private final T code;
    private final String text;
}
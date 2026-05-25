package com.develop.mvp.pk.module.system.domain.notice;

import java.util.Objects;

public final class Notice {

    private final Long id;
    private final String title;
    private final Integer type;
    private final String content;
    private final Integer status;

    private Notice(Long id, String title, Integer type, String content, Integer status) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.type = type;
        this.content = content;
        this.status = status;
    }

    public static Notice of(Long id, String title, Integer type, String content, Integer status) {
        return new Notice(id, title, type, content, status);
    }

    public Long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public Integer type() {
        return type;
    }

    public String content() {
        return content;
    }

    public Integer status() {
        return status;
    }
}

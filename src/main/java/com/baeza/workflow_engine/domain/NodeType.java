package com.baeza.workflow_engine.domain;

public enum NodeType {
    WEBHOOK_TRIGGER,
    HTTP_REQUEST,
    SEND_EMAIL,
    LOG_CONSOLE,
    SAVE_DATABASE
}
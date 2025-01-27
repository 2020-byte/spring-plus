package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * org.example.expert.domain.todo.dto.response.QTodoSearchResponse is a Querydsl Projection type for TodoSearchResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QTodoSearchResponse extends ConstructorExpression<TodoSearchResponse> {

    private static final long serialVersionUID = -1940571367L;

    public QTodoSearchResponse(com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<Long> managerCount, com.querydsl.core.types.Expression<Long> commentCount) {
        super(TodoSearchResponse.class, new Class<?>[]{String.class, long.class, long.class}, title, managerCount, commentCount);
    }

}


package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepositoryImpl implements TodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public TodoRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoSearchResponse> searchTodosAdvanced(
            String title, String managerNickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QUser user = QUser.user;
        QComment comment = QComment.comment;

        JPAQuery<TodoSearchResponse> query = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        manager.count(),
                        comment.count()
                ))
                .from(todo)
                .innerJoin(todo.managers, manager)
                .innerJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(title),
                        managerNicknameContains(managerNickname),
                        createdAtBetween(startDate, endDate)
                )
                .groupBy(todo.id, todo.title)
                .orderBy(todo.createdAt.desc());

        long total = query.fetch().size();
        List<TodoSearchResponse> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression titleContains(String title) {
        return title != null ? QTodo.todo.title.contains(title) : null;
    }

    private BooleanExpression managerNicknameContains(String nickname) {
        return nickname != null ? QUser.user.username.contains(nickname) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return QTodo.todo.createdAt.between(startDate, endDate);
        }
        if (startDate != null) {
            return QTodo.todo.createdAt.goe(startDate);
        }
        if (endDate != null) {
            return QTodo.todo.createdAt.loe(endDate);
        }
        return null;
    }
}

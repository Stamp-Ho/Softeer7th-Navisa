package com.navisa.be.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .map(EntityType::getJavaType)
                .filter(javaType -> !javaType.getName().contains("$"))
                .filter(javaType -> javaType.getAnnotation(Table.class) != null)
                .map(javaType -> javaType.getAnnotation(Table.class).name())
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute() {
        entityManager.flush();
        
        if (tableNames.isEmpty()) return;

        // 외래키로 인한 문제를 피하기 위해서 모든 테이블 이름을 쉼표로 연결하여 한 번의 쿼리로 실행
        String truncateQuery = "TRUNCATE TABLE " + String.join(", ", tableNames) + " RESTART IDENTITY CASCADE";
        entityManager.createNativeQuery(truncateQuery).executeUpdate();
    }
}
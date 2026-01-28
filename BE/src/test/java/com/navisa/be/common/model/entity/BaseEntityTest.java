package com.navisa.be.common.model.entity;

import com.navisa.be.common.config.JpaAuditConfig;
import com.navisa.be.support.IntegrationTestSupport;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Import(JpaAuditConfig.class)
@EntityScan(basePackages = {"com.navisa.be"})
class BaseEntityTest extends IntegrationTestSupport {

    @Autowired
    private EntityManager em;

    @Entity
    static class TestEntity extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        protected TestEntity() {}
        public TestEntity(String name) { this.name = name; }
    }

    @Test
    @DisplayName("BaseEntity를 상속받은 엔티티는 저장 시 생성일과 수정일이 자동 기입된다")
    void auditingTest() {

        TestEntity entity = new TestEntity("테스트");

        em.persist(entity);
        em.flush();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}

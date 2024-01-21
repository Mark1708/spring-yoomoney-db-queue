package org.example.test.repository;

import org.example.test.domain.TestMessageStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestMessageStatRepository extends JpaRepository<TestMessageStat, Long> {

    @Query(value = """
    select s from TestMessageStat s
        where s.testId = :testId
    order by s.sentAt
    limit 1
    """)
    Optional<TestMessageStat> findFirstMessage(Long testId);

    @Query(value = """
    select s from TestMessageStat s
        where s.testId = :testId
    order by s.sentAt DESC
    limit 1
    """)
    Optional<TestMessageStat> findLastMessage(Long testId);

    long countByTestId(Long testId);
}

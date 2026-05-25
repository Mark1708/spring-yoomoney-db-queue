package org.example.test.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.test.domain.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    Optional<Test> findByStartAt(LocalDateTime startAt);

    Optional<Test> findByEndAt(LocalDateTime endAt);
}

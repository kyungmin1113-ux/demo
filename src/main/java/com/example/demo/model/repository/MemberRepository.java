package com.example.demo.model.repository;

import java.util.Optional;
import java.util.List;

import com.example.demo.model.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Member> findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
        String username, String name, org.springframework.data.domain.Sort sort);
}

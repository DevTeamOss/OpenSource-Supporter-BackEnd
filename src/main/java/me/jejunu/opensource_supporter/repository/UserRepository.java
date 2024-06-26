package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    void deleteByUserName(String userName);
    List<User> findAll();
    Page<User> findAllByOrderByUsedPointDescCreatedAtAsc(Pageable pageable);
    List<User> findAllByOrderByUsedPointDescCreatedAtAsc();
    List<User> findByUserNameContainingOrCustomNameContaining(String userName, String customName);
}

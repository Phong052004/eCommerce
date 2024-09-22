package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.enitty.ChatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2);

    @Query("select c from ChatEntity c where c.userId1 = :userId1 order by c.newestChatTime DESC")
    Page<ChatEntity> findAllByUserId1(Long userId1, Pageable pageable);
}

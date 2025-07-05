package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.entities.Comment;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select nextval('comment_code_seq')", nativeQuery = true)
    long getNextCommentCode();

    @Modifying
    @Query("delete from Comment c where c.username = :username and c.code = :commentCode")
    int deleteByUserIdAndCommentCode(String username, String commentCode);

    @Query(
            """
            select c from Comment c where
            c.featureId = (select f.id from Feature f where f.code = :featureCode)
            """)
    List<Comment> findCommentsByFeatureCode(String featureCode, PageRequest pageRequest);
}

package io.mountblue.BlogApplication.repository;

import io.mountblue.BlogApplication.entity.PostTag;
import java.util.*;

import io.mountblue.BlogApplication.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByTag(Tag tag);
}

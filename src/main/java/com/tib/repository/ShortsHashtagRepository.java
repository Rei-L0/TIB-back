package com.tib.repository;

import java.util.Collection;
import java.util.List;
import com.tib.entity.Shorts;
import com.tib.entity.ShortsHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsHashtagRepository extends JpaRepository<ShortsHashtag, Long> {

  List<ShortsHashtag> findByShortsIn(Collection<Shorts> shorts);
}

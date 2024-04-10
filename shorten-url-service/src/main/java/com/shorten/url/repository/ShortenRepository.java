package com.shorten.url.repository;

import com.shorten.url.entity.Shorten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenRepository extends JpaRepository<Shorten,String> {
}

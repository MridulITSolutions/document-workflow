package com.docshare.common.repository;

import com.docshare.common.entity.RequestProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestProgressRepository
        extends JpaRepository<RequestProgress, Long> {

    List<RequestProgress> findByRequestId(Long requestId);

}
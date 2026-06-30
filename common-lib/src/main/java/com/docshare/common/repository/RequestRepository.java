package com.docshare.common.repository;

import com.docshare.common.entity.RequestMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<RequestMaster, Long> {

    List<RequestMaster> findByRequestedById(Long requestedById);

}
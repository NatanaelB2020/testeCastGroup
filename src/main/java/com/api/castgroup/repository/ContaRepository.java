package com.api.castgroup.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.api.castgroup.entity.ContaEntity;

@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, Long> {

    Optional<ContaEntity> findByNumeroConta(String numeroConta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ContaEntity> findById(Long id);
}

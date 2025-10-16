package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import service.sllbackend.entity.VoucherStatus;

public interface VoucherStatusRepo extends JpaRepository<VoucherStatus, Integer> {
}

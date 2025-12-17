package service.sllbackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Voucher;
import service.sllbackend.enumerator.DiscountType;

public interface VoucherRepo extends JpaRepository<Voucher, Integer> {

    @Query("select v from Voucher v left join fetch v.voucherStatus")
    List<Voucher> findAllWithStatus();

    @Query("select v from Voucher v left join fetch v.voucherStatus where v.id = :id")
    Optional<Voucher> findByIdWithStatus(@Param("id") Integer id);

    @Query("select v from Voucher v left join fetch v.voucherStatus " +
           "where (coalesce(nullif(:code, ''), null) is null or lower(v.voucherCode) like lower(concat('%', :code, '%'))) " +
           "and (coalesce(nullif(:name, ''), null) is null or lower(v.voucherName) like lower(concat('%', :name, '%'))) " +
           "and (:discountType is null or v.discountType = :discountType) " +
           "and (:statusId is null or v.voucherStatus.id = :statusId)")
    List<Voucher> searchVouchers(@Param("code") String code,
                                  @Param("name") String name,
                                  @Param("discountType") DiscountType discountType,
                                  @Param("statusId") Integer statusId);

    @Query("""
        SELECT v
        FROM Voucher v
        WHERE v.voucherStatus.name = :statusCode
          AND :now BETWEEN v.effectiveFrom AND v.effectiveTo
          AND v.usedCount < v.maxUsage
        ORDER BY v.effectiveTo ASC
    """)
    List<Voucher> findAvailableVouchers(
            @Param("statusCode") String statusCode,
            @Param("now") LocalDateTime now
    );

    Optional<Voucher> findByVoucherCodeIgnoreCase(String voucherCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Voucher v WHERE LOWER(v.voucherCode) = LOWER(:code)")
    Optional<Voucher> findByVoucherCodeForUpdate(@Param("code") String code);
}

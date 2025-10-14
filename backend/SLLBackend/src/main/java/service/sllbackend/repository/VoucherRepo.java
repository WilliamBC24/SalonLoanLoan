package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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
           "where (:code is null or :code = '' or lower(v.voucherCode) like lower(concat('%', :code, '%'))) " +
           "and (:name is null or :name = '' or lower(v.voucherName) like lower(concat('%', :name, '%'))) " +
           "and (:discountType is null or v.discountType = :discountType) " +
           "and (:statusId is null or v.voucherStatus.id = :statusId)")
    List<Voucher> searchVouchers(@Param("code") String code,
                                  @Param("name") String name,
                                  @Param("discountType") DiscountType discountType,
                                  @Param("statusId") Integer statusId);
}

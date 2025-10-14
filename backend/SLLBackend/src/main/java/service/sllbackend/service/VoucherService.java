package service.sllbackend.service;

import java.util.List;

import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;

public interface VoucherService {
    List<Voucher> getVouchers(String code, String name, String discountType, Integer statusId);
    
    Voucher getVoucherById(Integer id);
    
    List<VoucherStatus> getAllVoucherStatuses();
    
    Voucher createVoucher(Voucher voucher);
    
    Voucher updateVoucher(Integer id, Voucher voucher);
}

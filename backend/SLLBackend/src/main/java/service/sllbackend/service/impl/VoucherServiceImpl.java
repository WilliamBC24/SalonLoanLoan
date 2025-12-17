package service.sllbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.repository.VoucherRepo;
import service.sllbackend.repository.VoucherStatusRepo;
import service.sllbackend.service.VoucherService;
import service.sllbackend.web.dto.VoucherPublicDTO;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepo voucherRepo;
    private final VoucherStatusRepo voucherStatusRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getVouchers(String code, String name, String discountType, Integer statusId) {
        // Get all vouchers with status eagerly loaded
        List<Voucher> allVouchers = voucherRepo.findAllWithStatus();
        List<Voucher> filtered = new ArrayList<>();

        // Filter by code
        if (code != null && !code.trim().isEmpty()) {
            String lowerCaseCode = code.trim().toLowerCase();
            for (Voucher voucher : allVouchers) {
                if (voucher.getVoucherCode() != null && 
                    voucher.getVoucherCode().toLowerCase().contains(lowerCaseCode)) {
                    filtered.add(voucher);
                }
            }
        } else {
            filtered = new ArrayList<>(allVouchers);
        }

        // Filter by name
        if (name != null && !name.trim().isEmpty()) {
            String lowerCaseName = name.trim().toLowerCase();
            filtered.removeIf(voucher -> 
                voucher.getVoucherName() == null || 
                !voucher.getVoucherName().toLowerCase().contains(lowerCaseName)
            );
        }

        // Filter by discount type
        if (discountType != null && !discountType.isEmpty()) {
            try {
                DiscountType discType = DiscountType.valueOf(discountType);
                filtered.removeIf(voucher -> !discType.equals(voucher.getDiscountType()));
            } catch (IllegalArgumentException e) {
                // Invalid discount type, ignore filter
            }
        }

        // Filter by status
        if (statusId != null) {
            filtered.removeIf(voucher -> 
                voucher.getVoucherStatus() == null || 
                !statusId.equals(voucher.getVoucherStatus().getId())
            );
        }

        return filtered;
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherById(Integer id) {
        return voucherRepo.findByIdWithStatus(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherStatus> getAllVoucherStatuses() {
        return voucherStatusRepo.findAll();
    }

    @Override
    @Transactional
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepo.save(voucher);
    }

    @Override
    @Transactional
    public Voucher updateVoucher(Integer id, Voucher voucher) {
        Voucher existingVoucher = voucherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found with id: " + id));

        existingVoucher.setVoucherName(voucher.getVoucherName());
        existingVoucher.setVoucherDescription(voucher.getVoucherDescription());
        existingVoucher.setVoucherCode(voucher.getVoucherCode());
        existingVoucher.setDiscountType(voucher.getDiscountType());
        existingVoucher.setDiscountAmount(voucher.getDiscountAmount());
        existingVoucher.setEffectiveFrom(voucher.getEffectiveFrom());
        existingVoucher.setEffectiveTo(voucher.getEffectiveTo());
        existingVoucher.setMaxUsage(voucher.getMaxUsage());
        existingVoucher.setVoucherStatus(voucher.getVoucherStatus());

        return voucherRepo.save(existingVoucher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherPublicDTO> getAvailableVouchersForCheckout() {
        LocalDateTime now = LocalDateTime.now();

        List<Voucher> vouchers = voucherRepo.findAvailableVouchers("ACTIVE", now);

        return vouchers.stream().map(v -> VoucherPublicDTO.builder()
                .id(v.getId())
                .voucherName(v.getVoucherName())
                .voucherDescription(v.getVoucherDescription())
                .voucherCode(v.getVoucherCode())
                .discountType(v.getDiscountType())
                .discountAmount(v.getDiscountAmount())
                .effectiveFrom(v.getEffectiveFrom())
                .effectiveTo(v.getEffectiveTo())
                .build()
        ).toList();
    }
}

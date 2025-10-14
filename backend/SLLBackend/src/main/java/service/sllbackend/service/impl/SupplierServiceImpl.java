package service.sllbackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Supplier;
import service.sllbackend.repository.SupplierRepo;
import service.sllbackend.service.SupplierService;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepo supplierRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getSuppliers(List<Integer> categoryIds, String name) {
        List<Supplier> allSuppliers = supplierRepo.findAllWithCategory();
        
        if (categoryIds != null && !categoryIds.isEmpty()) {
            allSuppliers.removeIf(supplier -> supplier.getSupplierCategory() == null || !categoryIds.contains(supplier.getSupplierCategory().getId()));
        }

        if (name != null && !name.trim().isEmpty()) {
            String lowerCaseName = name.trim().toLowerCase();
            allSuppliers.removeIf(supplier -> supplier.getSupplierName() == null || !supplier.getSupplierName().toLowerCase().contains(lowerCaseName));
        }

        return allSuppliers;
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier getSupplierById(Integer id) {
        return supplierRepo.findByIdWithCategory(id).orElse(null);
    }

    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Integer id, Supplier supplier) {
        Supplier existingSupplier = supplierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        existingSupplier.setSupplierName(supplier.getSupplierName());
        existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setSupplierCategory(supplier.getSupplierCategory());
        existingSupplier.setNote(supplier.getNote());

        return supplierRepo.save(existingSupplier);
    }
}

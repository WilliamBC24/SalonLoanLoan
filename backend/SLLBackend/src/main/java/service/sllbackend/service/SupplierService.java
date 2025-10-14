package service.sllbackend.service;

import java.util.List;

import service.sllbackend.entity.Supplier;

public interface SupplierService {
    List<Supplier> getSuppliers(List<Integer> categoryIds, String name);
    
    Supplier getSupplierById(Integer id);
    
    Supplier createSupplier(Supplier supplier);
    
    Supplier updateSupplier(Integer id, Supplier supplier);
}

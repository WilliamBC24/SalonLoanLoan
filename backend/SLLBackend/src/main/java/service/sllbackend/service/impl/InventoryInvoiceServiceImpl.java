package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.InventoryInvoiceService;
import service.sllbackend.web.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryInvoiceServiceImpl implements InventoryInvoiceService {

    private final InventoryInvoiceRepo inventoryInvoiceRepo;
    private final InventoryInvoiceDetailRepo inventoryInvoiceDetailRepo;
    private final StaffAccountRepo staffAccountRepo;
    private final SupplierRepo supplierRepo;
    private final ProductRepo productRepo;

    @Override
    @Transactional
    public InventoryInvoiceViewDTO createInvoiceRequest(InventoryInvoiceCreateDTO dto, String username) {
        // Get staff from username
        StaffAccount staffAccount = staffAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Get supplier
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        // Create invoice with AWAITING status
        InventoryInvoice invoice = InventoryInvoice.builder()
                .staff(staffAccount.getStaff())
                .supplier(supplier)
                .note(dto.getNote())
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        
        invoice = inventoryInvoiceRepo.save(invoice);
        
        // Create invoice details
        List<InventoryInvoiceDetail> details = new ArrayList<>();
        for (InventoryInvoiceDetailDTO detailDTO : dto.getDetails()) {
            Product product = productRepo.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + detailDTO.getProductId()));
            
            InventoryInvoiceDetail detail = InventoryInvoiceDetail.builder()
                    .inventoryInvoice(invoice)
                    .product(product)
                    .orderedQuantity(detailDTO.getOrderedQuantity())
                    .unitPrice(detailDTO.getUnitPrice())
                    .build();
            
            details.add(inventoryInvoiceDetailRepo.save(detail));
        }
        
        log.info("Invoice request created by staff: {} for supplier: {}", username, supplier.getSupplierName());
        
        return convertToViewDTO(invoice, details);
    }

    @Override
    @Transactional
    public InventoryInvoiceViewDTO approveInvoice(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        if (invoice.getInvoiceStatus() != InventoryInvoiceStatus.AWAITING) {
            throw new RuntimeException("Can only approve invoices with AWAITING status");
        }
        
        invoice.setInvoiceStatus(InventoryInvoiceStatus.COMPLETE);
        invoice = inventoryInvoiceRepo.save(invoice);
        
        List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo.findByInventoryInvoiceId(invoiceId);
        
        log.info("Invoice {} approved and marked as COMPLETE", invoiceId);
        
        return convertToViewDTO(invoice, details);
    }

    @Override
    @Transactional
    public InventoryInvoiceViewDTO rejectInvoice(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        if (invoice.getInvoiceStatus() != InventoryInvoiceStatus.AWAITING) {
            throw new RuntimeException("Can only reject invoices with AWAITING status");
        }
        
        invoice.setInvoiceStatus(InventoryInvoiceStatus.CANCELLED);
        invoice = inventoryInvoiceRepo.save(invoice);
        
        List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo.findByInventoryInvoiceId(invoiceId);
        
        log.info("Invoice {} rejected and marked as CANCELLED", invoiceId);
        
        return convertToViewDTO(invoice, details);
    }

    @Override
    @Transactional
    public InventoryInvoiceViewDTO editInvoice(Integer invoiceId, InventoryInvoiceCreateDTO dto, String username) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        // Only allow editing if status is AWAITING
        if (invoice.getInvoiceStatus() != InventoryInvoiceStatus.AWAITING) {
            throw new RuntimeException("Can only edit invoices with AWAITING status");
        }
        
        // Verify the staff owns this invoice
        StaffAccount staffAccount = staffAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        if (!invoice.getStaff().getId().equals(staffAccount.getStaff().getId())) {
            throw new RuntimeException("You can only edit your own invoices");
        }
        
        // Update supplier if changed
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            invoice.setSupplier(supplier);
        }
        
        // Update note
        invoice.setNote(dto.getNote());
        invoice = inventoryInvoiceRepo.save(invoice);
        
        // Delete existing details
        List<InventoryInvoiceDetail> existingDetails = inventoryInvoiceDetailRepo.findByInventoryInvoiceId(invoiceId);
        inventoryInvoiceDetailRepo.deleteAll(existingDetails);
        
        // Create new details
        List<InventoryInvoiceDetail> newDetails = new ArrayList<>();
        for (InventoryInvoiceDetailDTO detailDTO : dto.getDetails()) {
            Product product = productRepo.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + detailDTO.getProductId()));
            
            InventoryInvoiceDetail detail = InventoryInvoiceDetail.builder()
                    .inventoryInvoice(invoice)
                    .product(product)
                    .orderedQuantity(detailDTO.getOrderedQuantity())
                    .unitPrice(detailDTO.getUnitPrice())
                    .build();
            
            newDetails.add(inventoryInvoiceDetailRepo.save(detail));
        }
        
        log.info("Invoice {} edited by staff: {}", invoiceId, username);
        
        return convertToViewDTO(invoice, newDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryInvoiceViewDTO> searchInvoices(InventoryInvoiceSearchDTO searchDTO) {
        Specification<InventoryInvoice> spec = (root, query, cb) -> cb.conjunction();
        
        if (searchDTO.getSupplierId() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("supplier").get("id"), searchDTO.getSupplierId()));
        }
        
        if (searchDTO.getStaffId() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("staff").get("id"), searchDTO.getStaffId()));
        }
        
        if (searchDTO.getStatus() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("invoiceStatus"), searchDTO.getStatus()));
        }
        
        if (searchDTO.getStartDate() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getStartDate()));
        }
        
        if (searchDTO.getEndDate() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getEndDate()));
        }
        
        List<InventoryInvoice> invoices = inventoryInvoiceRepo.findAll(spec);
        
        return invoices.stream()
                .map(invoice -> {
                    List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo
                            .findByInventoryInvoiceId(invoice.getId());
                    return convertToViewDTO(invoice, details);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryInvoiceViewDTO> getAllInvoices() {
        List<InventoryInvoice> invoices = inventoryInvoiceRepo.findAll();
        
        return invoices.stream()
                .map(invoice -> {
                    List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo
                            .findByInventoryInvoiceId(invoice.getId());
                    return convertToViewDTO(invoice, details);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryInvoiceViewDTO getInvoiceById(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo
                .findByInventoryInvoiceId(invoiceId);
        
        return convertToViewDTO(invoice, details);
    }

    private InventoryInvoiceViewDTO convertToViewDTO(InventoryInvoice invoice, List<InventoryInvoiceDetail> details) {
        List<InventoryInvoiceDetailViewDTO> detailDTOs = details.stream()
                .map(detail -> InventoryInvoiceDetailViewDTO.builder()
                        .id(detail.getId())
                        .productName(detail.getProduct().getProductName())
                        .productId(detail.getProduct().getId())
                        .orderedQuantity(detail.getOrderedQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .subtotal(detail.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        
        Integer totalAmount = details.stream()
                .mapToInt(detail -> detail.getSubtotal() != null ? detail.getSubtotal() : 
                    detail.getOrderedQuantity() * detail.getUnitPrice())
                .sum();
        
        return InventoryInvoiceViewDTO.builder()
                .id(invoice.getId())
                .staffName(invoice.getStaff().getName())
                .staffId(invoice.getStaff().getId())
                .supplierName(invoice.getSupplier().getSupplierName())
                .supplierId(invoice.getSupplier().getId())
                .createdAt(invoice.getCreatedAt())
                .note(invoice.getNote())
                .invoiceStatus(invoice.getInvoiceStatus())
                .totalAmount(totalAmount)
                .details(detailDTOs)
                .build();
    }
}

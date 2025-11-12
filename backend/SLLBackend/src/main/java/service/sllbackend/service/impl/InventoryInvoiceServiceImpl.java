package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.InventoryInvoiceService;
import service.sllbackend.web.dto.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryInvoiceServiceImpl implements InventoryInvoiceService {
    
    private final InventoryInvoiceRepo inventoryInvoiceRepo;
    private final InventoryInvoiceDetailRepo inventoryInvoiceDetailRepo;
    private final StaffRepo staffRepo;
    private final SupplierRepo supplierRepo;
    private final ProductRepo productRepo;
    
    @Override
    @Transactional
    public InventoryInvoice createInvoiceRequest(InventoryInvoiceCreateDTO dto, Integer staffId) {
        // Validate staff
        Staff staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));
        
        // Validate supplier
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + dto.getSupplierId()));
        
        // Create invoice
        InventoryInvoice invoice = InventoryInvoice.builder()
                .staff(staff)
                .supplier(supplier)
                .note(dto.getNote())
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .createdAt(LocalDateTime.now())
                .build();
        
        invoice = inventoryInvoiceRepo.save(invoice);
        
        // Create invoice details
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (InventoryInvoiceItemDTO itemDTO : dto.getItems()) {
                Product product = productRepo.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));
                
                InventoryInvoiceDetail detail = InventoryInvoiceDetail.builder()
                        .inventoryInvoice(invoice)
                        .product(product)
                        .orderedQuantity(itemDTO.getOrderedQuantity())
                        .unitPrice(itemDTO.getUnitPrice())
                        .build();
                
                inventoryInvoiceDetailRepo.save(detail);
            }
        }
        
        log.info("Created invoice request with id: {}", invoice.getId());
        return invoice;
    }
    
    @Override
    @Transactional
    public void approveInvoice(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        
        if (invoice.getInvoiceStatus() != InventoryInvoiceStatus.AWAITING) {
            throw new RuntimeException("Only invoices with AWAITING status can be approved");
        }
        
        invoice.setInvoiceStatus(InventoryInvoiceStatus.COMPLETE);
        inventoryInvoiceRepo.save(invoice);
        
        log.info("Approved invoice with id: {}", invoiceId);
    }
    
    @Override
    @Transactional
    public void rejectInvoice(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        
        if (invoice.getInvoiceStatus() != InventoryInvoiceStatus.AWAITING) {
            throw new RuntimeException("Only invoices with AWAITING status can be rejected");
        }
        
        invoice.setInvoiceStatus(InventoryInvoiceStatus.CANCELLED);
        inventoryInvoiceRepo.save(invoice);
        
        log.info("Rejected invoice with id: {}", invoiceId);
    }
    
    @Override
    @Transactional
    public void updateInvoiceStatus(Integer invoiceId, InventoryInvoiceStatus status) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        
        invoice.setInvoiceStatus(status);
        inventoryInvoiceRepo.save(invoice);
        
        log.info("Updated invoice {} status to: {}", invoiceId, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InventoryInvoiceListDTO> searchInvoices(Integer supplierId, InventoryInvoiceStatus status,
                                                         LocalDateTime fromDate, LocalDateTime toDate) {
        List<InventoryInvoice> invoices = inventoryInvoiceRepo.searchInvoices(supplierId, status, fromDate, toDate);
        return convertToListDTOs(invoices);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InventoryInvoiceListDTO> getAllInvoices() {
        List<InventoryInvoice> invoices = inventoryInvoiceRepo.findAllWithDetails();
        return convertToListDTOs(invoices);
    }
    
    @Override
    @Transactional(readOnly = true)
    public InventoryInvoiceViewDTO getInvoiceDetail(Integer invoiceId) {
        InventoryInvoice invoice = inventoryInvoiceRepo.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));
        
        List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo.findByInvoiceIdWithProduct(invoiceId);
        
        List<InventoryInvoiceDetailDTO> detailDTOs = details.stream()
                .map(detail -> InventoryInvoiceDetailDTO.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getProductName())
                        .orderedQuantity(detail.getOrderedQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .subtotal(detail.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        
        Integer totalAmount = details.stream()
                .mapToInt(InventoryInvoiceDetail::getSubtotal)
                .sum();
        
        return InventoryInvoiceViewDTO.builder()
                .id(invoice.getId())
                .staffId(invoice.getStaff().getId())
                .staffName(invoice.getStaff().getName())
                .supplierId(invoice.getSupplier().getId())
                .supplierName(invoice.getSupplier().getSupplierName())
                .createdAt(invoice.getCreatedAt())
                .note(invoice.getNote())
                .invoiceStatus(invoice.getInvoiceStatus())
                .details(detailDTOs)
                .totalAmount(totalAmount)
                .build();
    }
    
    private List<InventoryInvoiceListDTO> convertToListDTOs(List<InventoryInvoice> invoices) {
        List<InventoryInvoiceListDTO> dtos = new ArrayList<>();
        
        for (InventoryInvoice invoice : invoices) {
            List<InventoryInvoiceDetail> details = inventoryInvoiceDetailRepo.findByInvoiceIdWithProduct(invoice.getId());
            
            Integer totalItems = details.size();
            Integer totalAmount = details.stream()
                    .mapToInt(InventoryInvoiceDetail::getSubtotal)
                    .sum();
            
            InventoryInvoiceListDTO dto = InventoryInvoiceListDTO.builder()
                    .id(invoice.getId())
                    .staffName(invoice.getStaff().getName())
                    .supplierName(invoice.getSupplier().getSupplierName())
                    .createdAt(invoice.getCreatedAt())
                    .invoiceStatus(invoice.getInvoiceStatus())
                    .totalItems(totalItems)
                    .totalAmount(totalAmount)
                    .build();
            
            dtos.add(dto);
        }
        
        return dtos;
    }
}

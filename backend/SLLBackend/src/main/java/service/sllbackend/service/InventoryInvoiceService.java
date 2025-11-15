package service.sllbackend.service;

import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.web.dto.InventoryInvoiceCreateDTO;
import service.sllbackend.web.dto.InventoryInvoiceListDTO;
import service.sllbackend.web.dto.InventoryInvoiceViewDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryInvoiceService {
    
    /**
     * Function 15.1: Request to Create Purchase Invoice (Staff+)
     */
    InventoryInvoice createInvoiceRequest(InventoryInvoiceCreateDTO dto, Integer staffId);
    
    /**
     * Function 15.2: Approve Invoice Purchase Request (Admin only)
     */
    void approveInvoice(Integer invoiceId);
    
    /**
     * Function 15.3: Reject Invoice Purchase Request (Admin only)
     */
    void rejectInvoice(Integer invoiceId);
    
    /**
     * Function 15.4: Edit Invoice Status (Staff+)
     */
    void updateInvoiceStatus(Integer invoiceId, InventoryInvoiceStatus status);
    
    /**
     * Function 15.5: Search Invoice By Parameter (Staff+)
     */
    List<InventoryInvoiceListDTO> searchInvoices(Integer supplierId, InventoryInvoiceStatus status, 
                                                   LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Function 15.6: View Invoice List (Staff+)
     */
    List<InventoryInvoiceListDTO> getAllInvoices();
    
    /**
     * Function 15.7: View Invoice Detail (Staff+)
     */
    InventoryInvoiceViewDTO getInvoiceDetail(Integer invoiceId);
}

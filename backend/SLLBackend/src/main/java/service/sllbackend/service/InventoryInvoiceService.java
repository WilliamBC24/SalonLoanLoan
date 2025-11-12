package service.sllbackend.service;

import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.web.dto.InventoryInvoiceCreateDTO;
import service.sllbackend.web.dto.InventoryInvoiceSearchDTO;
import service.sllbackend.web.dto.InventoryInvoiceViewDTO;

import java.util.List;

public interface InventoryInvoiceService {
    
    /**
     * Create a new purchase invoice request (Staff only)
     * Status will be AWAITING by default
     */
    InventoryInvoiceViewDTO createInvoiceRequest(InventoryInvoiceCreateDTO dto, String username);
    
    /**
     * Approve a purchase invoice request (Admin only)
     * Changes status from AWAITING to COMPLETE
     */
    InventoryInvoiceViewDTO approveInvoice(Integer invoiceId);
    
    /**
     * Reject a purchase invoice request (Admin only)
     * Changes status from AWAITING to CANCELLED
     */
    InventoryInvoiceViewDTO rejectInvoice(Integer invoiceId);
    
    /**
     * Edit an invoice (Staff can edit if status is AWAITING)
     */
    InventoryInvoiceViewDTO editInvoice(Integer invoiceId, InventoryInvoiceCreateDTO dto, String username);
    
    /**
     * Search invoices by parameters (Staff and up)
     */
    List<InventoryInvoiceViewDTO> searchInvoices(InventoryInvoiceSearchDTO searchDTO);
    
    /**
     * Get all invoices (Staff and up)
     */
    List<InventoryInvoiceViewDTO> getAllInvoices();
    
    /**
     * Get invoice detail by ID (Staff and up)
     */
    InventoryInvoiceViewDTO getInvoiceById(Integer invoiceId);
}

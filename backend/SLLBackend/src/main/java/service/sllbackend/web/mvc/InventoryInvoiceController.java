package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.service.InventoryInvoiceService;
import service.sllbackend.web.dto.InventoryInvoiceCreateDTO;
import service.sllbackend.web.dto.InventoryInvoiceSearchDTO;
import service.sllbackend.web.dto.InventoryInvoiceViewDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/invoices")
@RequiredArgsConstructor
@Slf4j
public class InventoryInvoiceController {

    private final InventoryInvoiceService inventoryInvoiceService;

    /**
     * Function 15.1: Request to Create Purchase Invoice (Staff only)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryInvoiceViewDTO> createInvoiceRequest(
            @Valid @RequestBody InventoryInvoiceCreateDTO dto,
            Principal principal) {
        try {
            InventoryInvoiceViewDTO result = inventoryInvoiceService.createInvoiceRequest(dto, principal.getName());
            log.info("Invoice request created successfully by: {}", principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Error creating invoice request: {}", e.getMessage());
            throw new RuntimeException("Failed to create invoice request: " + e.getMessage());
        }
    }

    /**
     * Function 15.2: Approve Invoice Purchase Request (Admin only)
     */
    @PutMapping("/{invoiceId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryInvoiceViewDTO> approveInvoice(@PathVariable Integer invoiceId) {
        try {
            InventoryInvoiceViewDTO result = inventoryInvoiceService.approveInvoice(invoiceId);
            log.info("Invoice {} approved", invoiceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error approving invoice: {}", e.getMessage());
            throw new RuntimeException("Failed to approve invoice: " + e.getMessage());
        }
    }

    /**
     * Function 15.3: Reject Invoice Purchase Request (Admin only)
     */
    @PutMapping("/{invoiceId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryInvoiceViewDTO> rejectInvoice(@PathVariable Integer invoiceId) {
        try {
            InventoryInvoiceViewDTO result = inventoryInvoiceService.rejectInvoice(invoiceId);
            log.info("Invoice {} rejected", invoiceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error rejecting invoice: {}", e.getMessage());
            throw new RuntimeException("Failed to reject invoice: " + e.getMessage());
        }
    }

    /**
     * Function 15.4: Edit Invoice Status (Staff can edit their own AWAITING invoices)
     */
    @PutMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryInvoiceViewDTO> editInvoice(
            @PathVariable Integer invoiceId,
            @Valid @RequestBody InventoryInvoiceCreateDTO dto,
            Principal principal) {
        try {
            InventoryInvoiceViewDTO result = inventoryInvoiceService.editInvoice(invoiceId, dto, principal.getName());
            log.info("Invoice {} edited by: {}", invoiceId, principal.getName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error editing invoice: {}", e.getMessage());
            throw new RuntimeException("Failed to edit invoice: " + e.getMessage());
        }
    }

    /**
     * Function 15.5: Search Invoice By Parameter (Staff and up)
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<InventoryInvoiceViewDTO>> searchInvoices(
            @RequestBody InventoryInvoiceSearchDTO searchDTO) {
        try {
            List<InventoryInvoiceViewDTO> results = inventoryInvoiceService.searchInvoices(searchDTO);
            log.info("Search invoices returned {} results", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error searching invoices: {}", e.getMessage());
            throw new RuntimeException("Failed to search invoices: " + e.getMessage());
        }
    }

    /**
     * Function 15.6: View Invoice List (Staff and up)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<InventoryInvoiceViewDTO>> getAllInvoices() {
        try {
            List<InventoryInvoiceViewDTO> results = inventoryInvoiceService.getAllInvoices();
            log.info("Retrieved {} invoices", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error retrieving invoices: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve invoices: " + e.getMessage());
        }
    }

    /**
     * Function 15.7: View Invoice Detail (Staff and up)
     */
    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<InventoryInvoiceViewDTO> getInvoiceById(@PathVariable Integer invoiceId) {
        try {
            InventoryInvoiceViewDTO result = inventoryInvoiceService.getInvoiceById(invoiceId);
            log.info("Retrieved invoice {}", invoiceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving invoice: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve invoice: " + e.getMessage());
        }
    }
}

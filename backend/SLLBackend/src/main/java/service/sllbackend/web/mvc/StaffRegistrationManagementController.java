package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.service.*;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff/registration")
@RequiredArgsConstructor
@Slf4j
public class StaffRegistrationManagementController {
    private final AppointmentService appointmentService;
    private final AppointmentDetailsService appointmentDetailsService;
    private final UserAccountService userAccountService;
    private final RequestedServicesService requestedServicesService;
    private final StaffAccountService staffAccountService;
    private final StaffService staffService;
    private final ServicesService servicesService;
    private final AppointmentInvoiceService appointmentInvoiceService;
    private final PaymentTypeService paymentTypeService;
    private final SatisfactionRatingService satisfactionRatingService;
    private final VietQrService vietQrService;
    private final AppointmentImageService appointmentImageService;
    private final DTOMapper dtoMapper;

    @GetMapping("/list")
    public String staffRegistrationList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model) {

        List<Appointment> registrations;
        String trimmedName = name != null ? name.trim().replaceAll("\\s+", " ") : null;
        if (trimmedName == null) {
             registrations = appointmentService.getByNameAndStatus(trimmedName, statuses);
        } else {
            registrations = appointmentService.getByPhoneNumberAndStatus(trimmedName, statuses);
        }

        model.addAttribute("registrations", registrations);
        model.addAttribute("allStatuses", AppointmentStatus.values());

        return "staff-registration-list";
    }

    @GetMapping("/view/{id}")
    public String staffRegistrationView(@PathVariable int id, Model model) {
        Appointment appointment = appointmentService.findById((long) id);

        AppointmentDetails appointmentDetails = appointmentDetailsService.findByAppointmentId(id);
        AppointmentDetailsViewDTO appointmentDetailsDTO;
        boolean disableDetails = false;

        if (appointmentDetails != null
                && appointmentDetails.getScheduledStart() != null
                && appointment.getResponsibleStaffId() != null) {

            UserAccount user = appointmentDetails.getUser();
            appointmentDetailsDTO = dtoMapper.toAppointmentDetailsViewDTO(appointmentDetails, user);
        } else {
            appointmentDetailsDTO = new AppointmentDetailsViewDTO();
            disableDetails = true;
        }

        List<RequestedService> requestedServices = requestedServicesService.findByAppointmentId(id);
        List<StaffAccount> allStaff = staffAccountService.findAllActive();

        // ==== NEW: check if this appointment already has an invoice ====
        // e.g. service method: Optional<AppointmentInvoice> findByAppointmentId(Integer appointmentId)
        boolean finalized = appointmentInvoiceService
                .findByAppointmentId(appointment.getId().intValue())
                .isPresent();

        // ==== Load before/after images if appointment is COMPLETED ====
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            model.addAttribute("beforeImages", appointmentImageService.getBeforeImages(id));
            model.addAttribute("afterImages", appointmentImageService.getAfterImages(id));
        }

        model.addAttribute("allStatuses", AppointmentStatus.values());
        model.addAttribute("appointment", appointment);
        model.addAttribute("appointmentDetails", appointmentDetailsDTO);
        model.addAttribute("disableDetails", disableDetails);
        model.addAttribute("requestedServices", requestedServices);
        model.addAttribute("allStaff", allStaff);
        model.addAttribute("finalized", finalized); // <--- for HTML

        return "staff-registration-edit";
    }


    @PostMapping("/save/{id}")
    public String staffRegistrationSave(@PathVariable int id,
                                        @Valid @ModelAttribute AppointmentDetailsEditDTO dto,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            return "redirect:/staff/registration/view/" + id;
        }

        // ===== 1. Update Appointment fields =====
        Appointment appointment = appointmentService.findById((long) id);

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            appointment.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getScheduledAt() != null) {
            appointment.setScheduledAt(dto.getScheduledAt());
        }

        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }

        if (dto.getAssignedStaffId() != null) {
            appointment.setResponsibleStaffId(staffService.findById(dto.getAssignedStaffId()));
        }

        appointmentService.save(appointment);

        // ===== 2. Update AppointmentDetails fields =====
        AppointmentDetails details = appointmentDetailsService.findByAppointmentId(id);

        if (dto.getActualStart() != null) {
            details.setActualStart(dto.getActualStart());
        }

        if (dto.getActualEnd() != null) {
            details.setActualEnd(dto.getActualEnd());
        }

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            UserAccount user = userAccountService.findByUsername(dto.getUsername());
            details.setUser(user);
        }

        appointmentDetailsService.save(details);

        // ===== 3. Sync requested services (add / update / delete) =====
        List<RequestedService> existing = requestedServicesService.findByAppointmentId(id);

// Map existing RS by their Integer ID (because getId() returns int)
        Map<Integer, RequestedService> existingById = existing.stream()
                .collect(Collectors.toMap(RequestedService::getId, rs -> rs));

// Track which IDs we keep (also Integer)
        Set<Integer> keepIds = new HashSet<>();

        if (dto.getRequestedServices() != null) {
            for (RequestedServiceEditDTO rsDto : dto.getRequestedServices()) {

                if (rsDto.getServiceId() <= 0) {
                    continue;
                }

                RequestedService rs;
                Integer rsDtoId = rsDto.getId();

                if (rsDtoId != null && rsDtoId > 0 && existingById.containsKey(rsDtoId)) {
                    rs = existingById.get(rsDtoId);
                } else {
                    rs = new RequestedService();
                    rs.setAppointment(appointment);

                    Service service = servicesService.getServiceById(rsDto.getServiceId());
                    rs.setService(service);
                }

                rs.setPriceAtBooking(rsDto.getPriceAtBooking());

                if (rsDto.getResponsibleStaffId() > 0) {
                    Staff staff = staffService.findById(rsDto.getResponsibleStaffId());
                    rs.setResponsibleStaff(staff);
                } else {
                    rs.setResponsibleStaff(null);
                }

                requestedServicesService.save(rs);

                keepIds.add(rs.getId());
            }
        }


// delete any requested services removed from the form
        for (RequestedService rs : existing) {
            if (!keepIds.contains(rs.getId())) {
                requestedServicesService.delete(rs);
            }
        }


        return "redirect:/staff/registration/view/" + id;
    }


    @GetMapping("/user-search")
    @ResponseBody
    public List<SimpleUserDTO> searchUsers(@RequestParam String phone) {
        List<UserAccount> results = userAccountService.getSomeByPhoneNumber(phone);
        List<SimpleUserDTO> userDTOS = new ArrayList<>();
        for (UserAccount result : results) {
            userDTOS.add(dtoMapper.toSimpleUserDTO(result));
        }
        return userDTOS;
    }

    @GetMapping("/invoice/{id}")
    public String appointmentInvoice(@PathVariable int id,
                                     Model model,
                                     Principal principal) {

        // 1. Load base data
        Appointment appointment = appointmentService.findById((long) id);
        AppointmentDetails appointmentDetail =
                appointmentDetailsService.findByAppointmentId(id);
        List<RequestedService> requestedServices =
                requestedServicesService.findByAppointmentId(id);

        // 2. Build view DTOs
        AppointmentViewDTO appointmentDto =
                toAppointmentViewDTO(appointment, appointmentDetail);

        List<AppointmentServiceLineDTO> lineDtos =
                requestedServices.stream()
                        .map(this::toServiceLineDTO)
                        .toList();

        AppointmentInvoiceViewDTO invoicePreview =
                buildInvoicePreview(appointment.getId(), lineDtos);

        // 3. Check if an invoice already exists for this appointment
        Optional<AppointmentInvoice> existingInvoiceOpt =
                appointmentInvoiceService.findByAppointmentId(appointment.getId());

        boolean completed = existingInvoiceOpt.isPresent();
        model.addAttribute("completed", completed);

        if (completed) {
            AppointmentInvoice existingInvoice = existingInvoiceOpt.get();
            // reflect saved invoice info in the preview
            invoicePreview.setId(existingInvoice.getId());
            invoicePreview.setTotalAmount(existingInvoice.getTotalPrice());
            // if you later add discount/subtotal, adjust here
        }

        model.addAttribute("appointment", appointmentDto);
        model.addAttribute("appointmentDetails", lineDtos);
        model.addAttribute("invoice", invoicePreview);

        // 4. Load satisfaction rating if it exists
        Optional<SatisfactionRating> ratingOpt =
                satisfactionRatingService.findByAppointmentId(appointment.getId());

        ratingOpt.ifPresent(rating -> {
            model.addAttribute("satisfactionRatingValue", (int) rating.getRating());
            model.addAttribute("satisfactionComment", rating.getComment());
        });

        // 5. Generate VietQR ONLY if invoice is not yet completed
        if (!completed) {
            UserAccount userAccount = appointmentDetail.getUser();
            String username;

            if (userAccount != null && userAccount.getUsername() != null) {
                username = userAccount.getUsername();
            } else if (principal != null) {
                username = principal.getName();
            } else {
                username = "guest";
            }

            String qrUrl = vietQrService.generateQrUrl(
                    appointment.getId(),
                    username,
                    invoicePreview.getTotalAmount()
            );
            model.addAttribute("paymentQrUrl", qrUrl);
        }

        return "staff-create-appointment-invoice";
    }


    @PostMapping("/invoice/{id}/complete")
    public String completeAppointmentInvoice(@PathVariable int id,
                                             @RequestParam(name = "paymentMethod") String paymentMethod,
                                             @RequestParam(name = "satisfactionRating", required = false) Integer satisfactionRating,
                                             @RequestParam(name = "customerNotes", required = false) String customerNotes,
                                             RedirectAttributes redirectAttributes) {

        // 1. Load appointment
        Appointment appointment = appointmentService.findById((long) id);

        // 2. Load requested services and compute total price (server-side, ignore any client values)
        List<RequestedService> requestedServices =
                requestedServicesService.findByAppointmentId(id);

        int totalPrice = requestedServices.stream()
                .mapToInt(RequestedService::getPriceAtBooking)
                .sum();

        // 3. Resolve payment type from the radio value (CASH / BANK_TRANSFER)
        // Adjust this logic to your PaymentType model (code/name/id, etc.)
        PaymentType paymentType = paymentTypeService.findByName(paymentMethod);

        // 4. Responsible staff = appointment.getResponsibleStaffId() (returns Staff)
        Staff responsibleStaff = appointment.getResponsibleStaffId();
        if (responsibleStaff == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Responsible staff is missing for this appointment");
        }

        // 5. Create and save the invoice (only now is it persisted)
        AppointmentInvoice invoice = AppointmentInvoice.builder()
                .appointment(appointment)
                .totalPrice(totalPrice)
                .paymentType(paymentType)
                .responsibleStaff(responsibleStaff)
                .build();

        AppointmentInvoice savedInvoice = appointmentInvoiceService.save(invoice);

        // 6. Optionally create/update SatisfactionRating
        // - rating is optional
        // - comment is ignored if rating is null
        if (satisfactionRating != null) {
            if (satisfactionRating < 1 || satisfactionRating > 5) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Satisfaction rating must be between 1 and 5");
            }

            // if you want to allow updating rating for this appointment:
            SatisfactionRating ratingEntity = satisfactionRatingService.findByAppointmentId(id)
                    .orElseGet(SatisfactionRating::new);

            ratingEntity.setAppointment(appointment);
            ratingEntity.setRating(satisfactionRating.shortValue());

            if (customerNotes != null && !customerNotes.isBlank()) {
                ratingEntity.setComment(customerNotes.trim());
            } else {
                ratingEntity.setComment(null);
            }

            satisfactionRatingService.save(ratingEntity);
        }
        // If satisfactionRating == null: DO NOTHING, ignore customerNotes completely

        // 7. Optionally mark appointment as “PAID / COMPLETED” here if you have such a status
        // appointment.setStatus(AppointmentStatus.COMPLETED);
        // appointmentService.save(appointment);

        // 8. Flash message + redirect back to invoice page or appointments list
        redirectAttributes.addFlashAttribute("successMessage", "Invoice completed successfully.");
        return "redirect:/staff/registration/invoice/" + id;
    }


    private AppointmentViewDTO toAppointmentViewDTO(Appointment appointment,
                                                    AppointmentDetails detail) {

        AppointmentViewDTO dto = new AppointmentViewDTO();
        dto.setId(appointment.getId());

        // time from appointment_details
        dto.setStartTime(detail.getScheduledStart());

        // user from appointment_details
        UserAccount user = detail.getUser();
        if (user != null) {
            dto.setCustomerName(user.getUsername());       // adjust to your field
            dto.setCustomerPhoneNumber(user.getPhoneNumber());
        } else {
            dto.setCustomerName("Unknown customer");
            dto.setCustomerPhoneNumber("-");
        }

        // if your Appointment has staff assigned (adjust naming!)
        if (appointment.getResponsibleStaffId() != null) {
            dto.setAssignedStaffName(
                    appointment.getResponsibleStaffId().getName()
            );
        } else {
            dto.setAssignedStaffName("-");
        }

        return dto;
    }

    private AppointmentServiceLineDTO toServiceLineDTO(RequestedService rs) {

        AppointmentServiceLineDTO dto = new AppointmentServiceLineDTO();

        dto.setServiceName(rs.getService().getServiceName());

        if (rs.getResponsibleStaff() != null) {
            dto.setStaffName(rs.getResponsibleStaff().getName());
        } else {
            dto.setStaffName("-");
        }

        // duration: null unless you later connect with AppointmentDetails.schedule
        dto.setDurationMinutes(null);

        int price = rs.getPriceAtBooking();
        dto.setUnitPrice(price);

        return dto;
    }

    private AppointmentInvoiceViewDTO buildInvoicePreview(Integer appointmentId,
                                                          List<AppointmentServiceLineDTO> lines) {

        AppointmentInvoiceViewDTO dto = new AppointmentInvoiceViewDTO();
        dto.setAppointmentId(appointmentId);

        int total = lines.stream()
                .mapToInt(AppointmentServiceLineDTO::getUnitPrice)
                .sum();

        dto.setTotalAmount(total);

        return dto;
    }

    // ===== IMAGE UPLOAD ENDPOINTS =====
    
    @PostMapping("/view/{id}/upload-before-image")
    public String uploadBeforeImage(@PathVariable int id,
                                   @RequestParam("beforeImage") org.springframework.web.multipart.MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            appointmentImageService.addBeforeImage(id, file);
            redirectAttributes.addFlashAttribute("successMessage", "Before service image uploaded successfully.");
        } catch (Exception e) {
            log.error("Error uploading before image for appointment {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image: " + e.getMessage());
        }
        return "redirect:/staff/registration/view/" + id;
    }

    @PostMapping("/view/{id}/upload-after-image")
    public String uploadAfterImage(@PathVariable int id,
                                  @RequestParam("afterImage") org.springframework.web.multipart.MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {
            appointmentImageService.addAfterImage(id, file);
            redirectAttributes.addFlashAttribute("successMessage", "After service image uploaded successfully.");
        } catch (Exception e) {
            log.error("Error uploading after image for appointment {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image: " + e.getMessage());
        }
        return "redirect:/staff/registration/view/" + id;
    }

    @GetMapping("/create")
    public String showCreateAppointmentForm(Model model) {
        // DTO used by the form; make sure field names match the form inputs:
        // name, phoneNumber, appointmentDate, appointmentTime,
        // selectedServices (comma-separated IDs), staffId, ...
        if (!model.containsAttribute("appointmentRegisterDTO")) {
            model.addAttribute("appointmentRegisterDTO", new AppointmentRegisterDTO());
        }
        return "staff-create-appointment"; // staff-appointment-create.html
    }

    @PostMapping("/create")
    public String createAppointment(
            @ModelAttribute("appointmentRegisterDTO") AppointmentRegisterDTO appointmentRegisterDTO,
            RedirectAttributes redirectAttributes
    ) {
        try {
            appointmentService.register(appointmentRegisterDTO);
            // after creating, go back to staff appointment list
            return "redirect:/staff/registration/create?success";
        } catch (Exception ex) {
            // if something blows up, show the same page with ?error
            redirectAttributes.addFlashAttribute("appointmentRegisterDTO", appointmentRegisterDTO);
            return "redirect:/staff/registration/create?error";
        }
    }

}

package service.sllbackend.dev;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.*;
import service.sllbackend.repository.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
	private final UserAccountRepo userAccountRepo;
	private final StaffRepo staffRepo;
	private final StaffPositionRepo staffPositionRepo;
	private final StaffCurrentPositionRepo staffCurrentPositionRepo;
	private final PasswordEncoder passwordEncoder;
	private final ServiceRepo serviceRepo;
	private final ServiceCategoryRepo serviceCategoryRepo;
	private final ProductRepo productRepo;
	private final VoucherRepo voucherRepo;
	private final VoucherStatusRepo voucherStatusRepo;
	private final PromotionRepo promotionRepo;
	private final PromotionStatusRepo promotionStatusRepo;
	private final SupplierRepo supplierRepo;
	private final SupplierCategoryRepo supplierCategoryRepo;
	private final JobPostingRepo jobPostingRepo;
	private final JobPostingApplicationRepo jobPostingApplicationRepo;
	private final LoyaltyLevelRepo loyaltyLevelRepo;
	private final AppointmentRepo appointmentRepo;
	private final InventoryLotRepo inventoryLotRepo;
	private final PaymentTypeRepo paymentTypeRepo;
	private final InventoryConsignmentRepo inventoryConsignmentRepo;
	private final InventoryInvoiceRepo inventoryInvoiceRepo;
	private final InventoryInvoiceDetailRepo inventoryInvoiceDetailRepo;
	private final ShiftTemplateRepo shiftTemplateRepo;
	private final ExpenseCategoryRepo expenseCategoryRepo;
	private final StaffCommissionRepo staffCommissionRepo;
	private final ProductImageRepo productImageRepo;
	private final ServiceImageRepo serviceImageRepo;

	@Override
	public void run(String... args) {
		log.info("############ \n            Loading initial data\n############");
//		registerLoyaltyLevel();
//		registerUser();
//		registerStaff();
//		registerVouchers();
//		registerPromotions();
//		registerProviders();
//		registerInventory();
//		registerJobPosting();
//		registerJobApplication();
//		registerShiftTemplates();
//		registerPaymentTypes();
//		registerExpenseCategories();
//		registerStaffCommissions();
	}


	public void registerStaffCommissions() {
		StaffCommission mainStylistCommission = StaffCommission.builder()
				.commissionType(CommissionType.APPOINTMENT)
				.position(staffPositionRepo.findByPositionName("staff"))
				.commission((short) 30).build();
		StaffCommission assistantStylistCommission = StaffCommission.builder()
				.commissionType(CommissionType.APPOINTMENT)
				.position(staffPositionRepo.findByPositionName("assistant"))
				.commission((short) 20).build();
		StaffCommission managerStylistCommission = StaffCommission.builder()
				.commissionType(CommissionType.APPOINTMENT)
				.position(staffPositionRepo.findByPositionName("manager"))
				.commission((short) 40).build();
		StaffCommission adminStylistCommission = StaffCommission.builder()
				.commissionType(CommissionType.APPOINTMENT)
				.position(staffPositionRepo.findByPositionName("admin"))
				.commission((short) 100).build();
		staffCommissionRepo.save(mainStylistCommission);
		staffCommissionRepo.save(assistantStylistCommission);
		staffCommissionRepo.save(managerStylistCommission);
		staffCommissionRepo.save(adminStylistCommission);
	}

	public void registerExpenseCategories() {
		ExpenseCategory expenseCategory = ExpenseCategory.builder().name("Bills").build();
		ExpenseCategory expenseCategory2 = ExpenseCategory.builder().name("Others").build();
		expenseCategoryRepo.save(expenseCategory);
		expenseCategoryRepo.save(expenseCategory2);
	}

	public void registerPaymentTypes(){
		PaymentType paymentType = PaymentType.builder().name("CASH").build();
		PaymentType paymentType2 = PaymentType.builder().name("BANK_TRANSFER").build();
		paymentTypeRepo.save(paymentType);
		paymentTypeRepo.save(paymentType2);
	}

	public void registerShiftTemplates(){
		ShiftTemplate shiftTemplateAM = new ShiftTemplate();
		shiftTemplateAM.setShiftStart(LocalTime.of(7, 30));
		shiftTemplateAM.setShiftEnd(LocalTime.of(12, 0));

		ShiftTemplate shiftTemplatePM = new ShiftTemplate();
		shiftTemplatePM.setShiftStart(LocalTime.of(12, 30));
		shiftTemplatePM.setShiftEnd(LocalTime.of(19, 0));

		shiftTemplateRepo.save(shiftTemplateAM);
		shiftTemplateRepo.save(shiftTemplatePM);
	}

	public void registerPromotions() {
		PromotionStatus activeStatus = promotionStatusRepo.save(PromotionStatus.builder()
				.name("ACTIVE")
				.build());

		PromotionStatus inactiveStatus = promotionStatusRepo.save(PromotionStatus.builder()
				.name("INACTIVE")
				.build());

		promotionRepo.save(Promotion.builder()
				.promotionName("Holiday Discount")
				.promotionDescription("20% off on all services during the holiday season")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(20)
				.effectiveFrom(LocalDateTime.of(2024, 12, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2024, 12, 31, 23, 59))
				.promotionStatus(activeStatus)
				.build());

		promotionRepo.save(Promotion.builder()
				.promotionName("New Year Special")
				.promotionDescription("Flat 100,000 VND off on all services")
				.discountType(DiscountType.AMOUNT)
				.discountAmount(100000)
				.effectiveFrom(LocalDateTime.of(2025, 1, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 1, 15, 23, 59))
				.promotionStatus(activeStatus)
				.build());

		promotionRepo.save(Promotion.builder()
				.promotionName("Summer Sale")
				.promotionDescription("15% off on selected services")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(15)
				.effectiveFrom(LocalDateTime.of(2024, 6, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2024, 6, 30, 23, 59))
				.promotionStatus(inactiveStatus)
				.build());

		log.info("Successfully loaded 3 promotions with 2 statuses");
	}

	public void registerUser() {
		String username = "alice";
		String rawPassword = "alice";

		String hashedPassword = passwordEncoder.encode(rawPassword);
		userAccountRepo.save(UserAccount.builder()
				.username(username)
				.password(hashedPassword)
				.gender(Gender.MALE)
				.birthDate(LocalDate.of(2004, 9, 6))
				.phoneNumber("0999999999")
				.email("alice@wonderland.com")
				.accountStatus(AccountStatus.ACTIVE)
				.build());

		userAccountRepo.save(UserAccount.builder()
				.username("anon")
				.password(passwordEncoder.encode("anon"))
				.gender(Gender.MALE)
				.birthDate(LocalDate.of(2004, 9, 6))
				.phoneNumber("0888888888")
				.email("a@gomal.com")
				.accountStatus(AccountStatus.ACTIVE)
				.build());

		log.info("User registered: " + username + " with password: " + rawPassword);
	}

	public void registerStaff() {
		String name = "admin";
		String role = "admin";

		Staff staff = staffRepo.save(Staff.builder()
				.name(name)
				.birthDate(LocalDate.of(2004, 1, 1))
				.build());

		StaffPosition staffPosition = staffPositionRepo.save(StaffPosition.builder()
				.positionName(role)
				.build());

		staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
				.staff(staff)
				.position(staffPosition)
				.build());

		Staff staff2 = staffRepo.save(Staff.builder()
				.name("staff")
				.birthDate(LocalDate.of(2004, 1, 1))
				.build());

		StaffPosition staffPosition2 = staffPositionRepo.save(StaffPosition.builder()
				.positionName("staff")
				.build());

		staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
				.staff(staff2)
				.position(staffPosition2)
				.build());

		Staff staff3 = staffRepo.save(Staff.builder()
				.name("assistant")
				.birthDate(LocalDate.of(2004, 1, 1))
				.build());

		StaffPosition staffPosition3 = staffPositionRepo.save(StaffPosition.builder()
				.positionName("assistant")
				.build());

		staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
				.staff(staff3)
				.position(staffPosition3)
				.build());

		Staff staff4 = staffRepo.save(Staff.builder()
				.name("manager")
				.birthDate(LocalDate.of(2004, 1, 1))
				.build());

		StaffPosition staffPosition4 = staffPositionRepo.save(StaffPosition.builder()
				.positionName("manager")
				.build());

		staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
				.staff(staff4)
				.position(staffPosition4)
				.build());
		log.info("Staff registered: " + name + " with role: " + role);
	}


	public void registerInventory() {
		// Get all products
		List<Product> products = productRepo.findAll();
		
		// Get first staff for creating invoice
		Staff staff = staffRepo.findAll().stream().findFirst()
				.orElseThrow(() -> new RuntimeException("No staff found"));
		
		// Get first supplier
		Supplier supplier = supplierRepo.findAll().stream().findFirst()
				.orElseThrow(() -> new RuntimeException("No supplier found"));
		
		// Create inventory invoice
		InventoryInvoice inventoryInvoice = inventoryInvoiceRepo.save(InventoryInvoice.builder()
				.staff(staff)
				.supplier(supplier)
				.note("Initial stock load")
				.invoiceStatus(InventoryInvoiceStatus.COMPLETE)
				.build());
		
		// For each product, create inventory invoice detail, consignment, and lot
		for (Product product : products) {
			// Create inventory invoice detail
			InventoryInvoiceDetail invoiceDetail = inventoryInvoiceDetailRepo.save(
				InventoryInvoiceDetail.builder()
						.inventoryInvoice(inventoryInvoice)
						.product(product)
						.orderedQuantity(100) // Initial stock of 100 units per product
						.unitPrice(product.getCurrentPrice() / 2) // Cost is half of retail price
						.build()
			);
			
			// Create inventory consignment
			InventoryConsignment consignment = inventoryConsignmentRepo.save(
				InventoryConsignment.builder()
						.inventoryInvoiceDetail(invoiceDetail)
						.product(product)
						.supplier(supplier)
						.receivedQuantity(100)
						.build()
			);
			
			// Create inventory lot with expiry date 2 years from now
			inventoryLotRepo.save(InventoryLot.builder()
					.inventoryConsignment(consignment)
					.product(product)
					.availableQuantity(100)
					.productExpiryDate(LocalDate.now().plusYears(2))
					.build());
		}
		
		log.info("Successfully loaded inventory stock for {} products (100 units each)", products.size());
		
		// Create additional test invoices with different statuses
		createTestInvoices(staff, supplier, products);
	}
	
	private void createTestInvoices(Staff staff, Supplier supplier, List<Product> products) {
		// Create an AWAITING invoice for testing approval flow
		InventoryInvoice awaitingInvoice = inventoryInvoiceRepo.save(InventoryInvoice.builder()
				.staff(staff)
				.supplier(supplier)
				.note("Pending approval - Test order for hair products")
				.invoiceStatus(InventoryInvoiceStatus.AWAITING)
				.createdAt(LocalDateTime.now().minusDays(2))
				.build());
		
		// Add 3 products to awaiting invoice
		for (int i = 0; i < Math.min(3, products.size()); i++) {
			Product product = products.get(i);
			inventoryInvoiceDetailRepo.save(
				InventoryInvoiceDetail.builder()
						.inventoryInvoice(awaitingInvoice)
						.product(product)
						.orderedQuantity(50)
						.unitPrice(product.getCurrentPrice() / 2)
						.build()
			);
		}
		
		// Create a CANCELLED invoice for testing
		InventoryInvoice cancelledInvoice = inventoryInvoiceRepo.save(InventoryInvoice.builder()
				.staff(staff)
				.supplier(supplier)
				.note("Cancelled - Supplier out of stock")
				.invoiceStatus(InventoryInvoiceStatus.CANCELLED)
				.createdAt(LocalDateTime.now().minusDays(5))
				.build());
		
		// Add 2 products to cancelled invoice
		for (int i = 3; i < Math.min(5, products.size()); i++) {
			Product product = products.get(i);
			inventoryInvoiceDetailRepo.save(
				InventoryInvoiceDetail.builder()
						.inventoryInvoice(cancelledInvoice)
						.product(product)
						.orderedQuantity(30)
						.unitPrice(product.getCurrentPrice() / 2)
						.build()
			);
		}
		
		log.info("Successfully created test invoices: 1 AWAITING, 1 CANCELLED");
	}

	public void registerVouchers() {
		// Create voucher statuses
		VoucherStatus activeStatus = voucherStatusRepo.save(VoucherStatus.builder()
				.name("ACTIVE")
				.build());

		VoucherStatus inactiveStatus = voucherStatusRepo.save(VoucherStatus.builder()
				.name("INACTIVE")
				.build());

		VoucherStatus expiredStatus = voucherStatusRepo.save(VoucherStatus.builder()
				.name("EXPIRED")
				.build());

		// Create sample vouchers
		voucherRepo.save(Voucher.builder()
				.voucherName("New Year 2025")
				.voucherDescription("Special discount for New Year celebration 2025")
				.voucherCode("NEWYEAR2025")
				.discountType(DiscountType.AMOUNT)
				.discountAmount(50000)
				.effectiveFrom(LocalDateTime.of(2025, 1, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 1, 31, 23, 59))
				.maxUsage(100)
				.usedCount(0)
				.voucherStatus(activeStatus)
				.build());

		voucherRepo.save(Voucher.builder()
				.voucherName("Summer Sale")
				.voucherDescription("20% off for summer season")
				.voucherCode("SUMMER20")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(20)
				.effectiveFrom(LocalDateTime.of(2025, 6, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 8, 31, 23, 59))
				.maxUsage(500)
				.usedCount(25)
				.voucherStatus(inactiveStatus)
				.build());

		voucherRepo.save(Voucher.builder()
				.voucherName("First Time Customer")
				.voucherDescription("Welcome discount for first-time customers")
				.voucherCode("WELCOME10")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(10)
				.effectiveFrom(LocalDateTime.of(2025, 1, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 12, 31, 23, 59))
				.maxUsage(1000)
				.usedCount(150)
				.voucherStatus(activeStatus)
				.build());

		voucherRepo.save(Voucher.builder()
				.voucherName("VIP Member Exclusive")
				.voucherDescription("Exclusive discount for VIP members - 100,000 VND off")
				.voucherCode("VIP100K")
				.discountType(DiscountType.AMOUNT)
				.discountAmount(100000)
				.effectiveFrom(LocalDateTime.of(2025, 1, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 12, 31, 23, 59))
				.maxUsage(50)
				.usedCount(12)
				.voucherStatus(activeStatus)
				.build());

		voucherRepo.save(Voucher.builder()
				.voucherName("Weekend Special")
				.voucherDescription("15% discount for weekend appointments")
				.voucherCode("WEEKEND15")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(15)
				.effectiveFrom(LocalDateTime.of(2025, 3, 1, 0, 0))
				.effectiveTo(LocalDateTime.of(2025, 3, 31, 23, 59))
				.maxUsage(200)
				.usedCount(180)
				.voucherStatus(activeStatus)
				.build());

		voucherRepo.save(Voucher.builder()
				.voucherName("Black Friday 2024")
				.voucherDescription("Huge discount for Black Friday - 50% off")
				.voucherCode("BLACKFRIDAY50")
				.discountType(DiscountType.PERCENTAGE)
				.discountAmount(50)
				.effectiveFrom(LocalDateTime.of(2024, 11, 29, 0, 0))
				.effectiveTo(LocalDateTime.of(2024, 11, 29, 23, 59))
				.maxUsage(500)
				.usedCount(500)
				.voucherStatus(expiredStatus)
				.build());

		log.info("Successfully loaded 6 vouchers with 3 statuses");
	}

	public void registerProviders() {
		// Create supplier categories
		SupplierCategory hairProductsCategory = supplierCategoryRepo.save(SupplierCategory.builder()
				.name("Hair Products Supplier")
				.build());
		
		SupplierCategory skinCareCategory = supplierCategoryRepo.save(SupplierCategory.builder()
				.name("Skin Care Supplier")
				.build());
		
		SupplierCategory nailProductsCategory = supplierCategoryRepo.save(SupplierCategory.builder()
				.name("Nail Products Supplier")
				.build());
		
		SupplierCategory makeupCategory = supplierCategoryRepo.save(SupplierCategory.builder()
				.name("Makeup Products Supplier")
				.build());
		
		SupplierCategory equipmentCategory = supplierCategoryRepo.save(SupplierCategory.builder()
				.name("Salon Equipment Supplier")
				.build());

		// Hair Products Suppliers
		supplierRepo.save(Supplier.builder()
				.supplierName("L'Oréal Professional Vietnam")
				.supplierCategory(hairProductsCategory)
				.phoneNumber("0281234567")
				.email("contact@loreal-vietnam.com")
				.note("Premium hair care products, exclusive distributor")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("Wella Professionals")
				.supplierCategory(hairProductsCategory)
				.phoneNumber("0287654321")
				.email("sales@wella.vn")
				.note("Professional hair color and care products")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("Schwarzkopf Vietnam")
				.supplierCategory(hairProductsCategory)
				.phoneNumber("0283456789")
				.email("info@schwarzkopf.vn")
				.note("German quality hair products")
				.build());

		// Skin Care Suppliers
		supplierRepo.save(Supplier.builder()
				.supplierName("Dermalogica Vietnam")
				.supplierCategory(skinCareCategory)
				.phoneNumber("0289876543")
				.email("vietnam@dermalogica.com")
				.note("Professional skin care and treatments")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("The Ordinary Vietnam")
				.supplierCategory(skinCareCategory)
				.phoneNumber("0284567890")
				.email("contact@theordinary.vn")
				.note("Affordable clinical skincare solutions")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("La Roche-Posay")
				.supplierCategory(skinCareCategory)
				.phoneNumber("0286543210")
				.email("info@laroche-posay.vn")
				.note("Dermatological skincare products")
				.build());

		// Nail Products Suppliers
		supplierRepo.save(Supplier.builder()
				.supplierName("OPI Vietnam")
				.supplierCategory(nailProductsCategory)
				.phoneNumber("0285432109")
				.email("orders@opi-vietnam.com")
				.note("Premium nail lacquer and treatments")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("CND Vietnam")
				.supplierCategory(nailProductsCategory)
				.phoneNumber("0288765432")
				.email("sales@cnd.vn")
				.note("Shellac and professional nail products")
				.build());

		// Makeup Suppliers
		supplierRepo.save(Supplier.builder()
				.supplierName("MAC Cosmetics Vietnam")
				.supplierCategory(makeupCategory)
				.phoneNumber("0282345678")
				.email("pro@maccosmetics.vn")
				.note("Professional makeup products")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("Sephora Vietnam")
				.supplierCategory(makeupCategory)
				.phoneNumber("0287890123")
				.email("wholesale@sephora.vn")
				.note("Wide range of beauty and makeup products")
				.build());

		// Equipment Suppliers
		supplierRepo.save(Supplier.builder()
				.supplierName("Takara Belmont Vietnam")
				.supplierCategory(equipmentCategory)
				.phoneNumber("0283210987")
				.email("sales@takarabelmont.vn")
				.note("Salon furniture and equipment")
				.build());

		supplierRepo.save(Supplier.builder()
				.supplierName("Beauty Solutions Co.")
				.supplierCategory(equipmentCategory)
				.phoneNumber("0289012345")
				.email("info@beautysolutions.vn")
				.note("Salon tools and professional equipment")
				.build());

		log.info("Successfully loaded 12 providers across 5 categories");
	}

	public void registerJobPosting() {
		jobPostingRepo.save(JobPosting.builder()
				.jobPostingName("Hair Stylist")
				.jobPostingDescription("Become a hair stylist for our salon")
				.maxApplication(10)
				.effectiveFrom(LocalDate.now())
				.effectiveTo(LocalDate.now())
				.status(JobPostingStatus.ACTIVE)
				.build());

	}

	public void registerJobApplication() {
		jobPostingApplicationRepo.save(JobPostingApplication.builder()
				.jobPosting(jobPostingRepo.findById(1L).orElse(null))
				.applicantName("alice")
				.applicantDob(LocalDate.now())
				.applicantPhoneNumber("0999111222")
				.build());
	}

	public void registerLoyaltyLevel() {
		loyaltyLevelRepo.save(new LoyaltyLevel(null, "Bronze", 10000));
		// Silver
		loyaltyLevelRepo.save(new LoyaltyLevel(null, "Silver", 20000));
		// Gold
		loyaltyLevelRepo.save(new LoyaltyLevel(null, "Gold", 500000));
		// Platinum
		loyaltyLevelRepo.save(new LoyaltyLevel(null, "Platinum", 1000000));
	}





}
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
//		load();
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




		public void load() {
			ServiceCategory trángmàutạobóngphủmàuCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Tráng màu tạo bóng, phủ màu").build());
			ServiceCategory éptóccụpCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Ép Tóc Cụp/ Phồng").build());
			ServiceCategory nhuộmtócCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Nhuộm Tóc").build());
			ServiceCategory gộisấytạokiểuCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Gội sấy tạo kiểu").build());
			ServiceCategory hấptócchữatrịphụchồiCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Hấp tóc, chữa trị phục hồi").build());
			ServiceCategory uốntócCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Uốn tóc").build());
			ServiceCategory cắttócCategory = serviceCategoryRepo.save(ServiceCategory.builder().name("Cắt tóc").build());

			productRepo.save(Product.builder()
					.productName("Nutritive Moisturizing Sampoo +++3 Dầu gội siêu dưỡng")
					.currentPrice(600000)
					.productDescription("Mô tả Beaver Professional Nutritive Moisturizing Shampoo +++3 có công thức dịu nhẹ, nhẹ nhàng mở lớp biểu bì giúp làm sạch bụi bẩn bám trên tóc và da đầu, mang lại sự thông thoáng thoải mái sau mỗi lần gội  Dầu gội bão hòa với các dưỡng chất, dưỡng ẩm sâu giúp phục hồi các vùng tóc bị hư tổn cho tóc trở nên suôn mượt và vào nếp, chắc khỏe hơn  Thành phần không chứa chất bảo quản Paraben nên an toàn cho da đầu dù là nhạy cảm  Mùi thơm sang trọng mang lại sự tự tin hơn cho người dùng  Thông tin sản phẩm: Thương hiệu: Beaver – Đức Thành phần:  Aqua, Sodium Laureth Sulfate, Dimethicone, Parfum, Glycerin, Cocamidopropyl Betaine,.. Dung tích: 768ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng.  Hướng dẫn sử dụng: Làm ướt da đầu Lấy một lượng dầu gội đầu vừa đủ thoa đều lên tóc, sau đó massage nhẹ nhàng để sản phẩm ngấm vào tóc và da đầu Xả sạch lại với nước Gội lại thêm 1 lần nữa và lau khô")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Nutritive Repairing Conditioner +++3 Dầu xả siêu dưỡng")
					.currentPrice(600000)
					.productDescription("Mô tả Beaver Professional Nutritive Repairing Conditioner +++3 là loại dầu xả siêu dưỡng tóc. Beaver +++3 giúp cân bằng cấu trúc của mái tóc, tăng cường sự liên kết giữa các tế bào tóc ở mức độ phân tử, để hạn chế tình trạng thiếu ẩm của tóc  Kết cấu dạng kem siêu mềm, giúp các dưỡng chất dễ dàng thẩm thấu và nuôi dưỡng mái tóc trở nên chắc khỏe, mềm mại  Dầu xả siêu dưỡng tóc hỗ trợ giảm tình trạng gãy rụng, rối xù cho tóc hiệu quả  Lời khuyên từ các chuyên gia chăm sóc tóc hàng đầu thế giới, là bạn nên kết hợp đủ bộ gội + xả để nâng cao hiệu quả nuôi dưỡng chuyên sâu cho mái tóc  Thông tin sản phẩm: Thương hiệu: Beaver – Đức Thành phần:  Aqua, Sodium Laureth Sulfate, Dimethicone, Parfum, Glycerin, Cocamidopropyl Betaine,.. Dung tích: 768ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng.  Hướng dẫn sử dụng: Sau khi gội đầu với dầu gội siêu dưỡng Beaver Nutritive Moisturizing Shampoo Lấy 1 lượng dầu xả vừa đủ thoa đều lên thân và ngọn tóc Massage nhẹ nhàng từ 3 đến 5 phút Xả sạch lại với nước và tạo kiểu")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Collagen Complex Smooth Nourishing Conditioner Dầu xả dinh dưỡng")
					.currentPrice(650000)
					.productDescription("Collagen Complex Smooth Nourishing Conditioner Dầu xả dinh dưỡng NAPOLY là một sản phẩm dầu xả thuộc thương hiệu chăm sóc tóc NAPOLY. Sản phẩm này tập trung vào việc phục hồi và nuôi dưỡng tóc hư tổn.    Công dụng: Phục hồi tóc hư tổn: Giúp cải thiện tình trạng tóc khô xơ, chẻ ngọn do hóa chất hoặc nhiệt độ. Dưỡng ẩm và làm mềm mượt: Mang lại mái tóc mềm mại, bóng mượt và dễ chải. Cung cấp dinh dưỡng: Nuôi dưỡng tóc từ sâu bên trong, giúp tóc chắc khỏe hơn. Lưu hương: Thường có mùi thơm dễ chịu và giữ mùi lâu trên tóc.   Thông tin sản phẩm: Thương hiệu: NAPOLY  Thành phần chính: Sản phẩm chứa hợp chất ProVitamin B7 và collagen/biotin (thường thấy trong các sản phẩm cùng dòng của thương hiệu này), cung cấp dưỡng chất thấm sâu vào sợi tóc Dung tích: 800ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Collagen Complex Smooth Nourishing Sampoo Dầu gội dinh dưỡng")
					.currentPrice(600000)
					.productDescription("Sản phẩm bạn tìm kiếm có tên đầy đủ là Dầu gội dinh dưỡng Collagen Complex Smooth Nourishing Shampoo Napoly. Đây là một sản phẩm chăm sóc tóc được sử dụng rộng rãi, đặc biệt phổ biến tại thị trường Việt Nam.  Sản phẩm được biết đến với khả năng phục hồi tóc hư tổn, giúp tóc trở nên mềm mượt và thơm lâu.. Loại tóc phù hợp: Thường được khuyên dùng cho tóc khô xơ và hư tổn.  Dưới đây là một số thông tin chi tiết về sản phẩm: Thông tin sản phẩm: Thương hiệu: NAPOLY  Thành phần nổi bật: Chứa hợp chất ProVitamin B7 và biotin, cùng với collagen và tinh dầu argan (tùy phiên bản), cung cấp dưỡng chất thấm sâu và phục hồi tóc từ bên trong Dung tích: 800ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Tecna Tsuyo 03 Shiki Salon Technical Conditioner Dầu xả phục hồi chuyên sâu")
					.currentPrice(550000)
					.productDescription("Tecna Tsuyo 03 Shiki Salon Technical Conditioner là bước độp phá trong công nghệ Bio Enzyme và thành phẩn thảo dược thiên nhiên với cơ chế hoạt động tăng cường nuôi dưỡng, dưỡng ẩm chống oxy hóa.Giúp phục hồi và hàn gắn hư tổn, ngay lập tức  nuôi dưỡng tăng cường dộ đàn hồi giúp tóc mềm mại và sáng bóng tức thì. Đặc biệt, với độ PH thấp giúp cân bằng mái tóc và da đầu ngay sau khi nhuộm, tẩy,...Sản phẩm lý tưởng để khóa màu nhuộm và thư giãn da đầu.  Loại tóc: Phù hợp cho mọi loại tóc.  Đặc tính: Sản phẩm thuần chay (vegan) và không thử nghiệm trên động vật (cruelty-free). Công thức dịu nhẹ và không gây hại cho da tay. Có hương thơm nhẹ nhàng.  Thông tin sản phẩm: Thương hiệu: Sản phẩm của tập đoàn TECNA - VIA ARDEATINA, ROMA - ITALY  Sản xuất tại: Ý Dung tích: 500ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Tecna Tsuyo 01 Shiki Salon Technical Sampoo Dầu gội phục hồi chuyên sâu")
					.currentPrice(550000)
					.productDescription("Tecna Tsuyo 01 Shiki Salon Technical Sampoo là bước độp phá trong công nghệ Bio Enzyme và thành phẩn thảo dược thiên nhiên với cơ chế hoạt động tăng cường nuôi dưỡng, dưỡng ẩm chống oxy hóa.Giúp phục hồi và hàn gắn hư tổn, ngay lập tức  nuôi dưỡng tăng cường dộ đàn hồi giúp tóc mềm mại và sáng bóng cho tóc.  Đây là dầu gội kỹ thuật được thiết kế đặc biệt để sử dụng trước các dịch vụ kỹ thuật tại salon (chẳng hạn như nhuộm màu hoặc các liệu trình khác).  Loại tóc: Phù hợp cho mọi loại tóc.  Đặc tính: Sản phẩm thuần chay (vegan) và không thử nghiệm trên động vật (cruelty-free). Công thức dịu nhẹ và không gây hại cho da tay. Có hương thơm nhẹ nhàng.  Thông tin sản phẩm: Thương hiệu: Sản phẩm của tập đoàn TECNA - VIA ARDEATINA, ROMA - ITALY  Sản xuất tại: Ý Dung tích: 750ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Lisap Lisaplex Bond Saver Shampoo")
					.currentPrice(950000)
					.productDescription("Dầu gội Lisap Lisaplex Bond Saver Shampoo là sản phẩm chăm sóc tóc chuyên nghiệp của thương hiệu Lisap Milano (Ý), được thiết kế đặc biệt để phục hồi và bảo vệ các liên kết tóc hư tổn.   Đặc điểm nổi bật  Phục hồi liên kết tóc: Sản phẩm này giúp xây dựng lại các liên kết bên trong cấu trúc tóc, tăng cường độ chắc khỏe cho tóc hư tổn do xử lý hóa học (nhuộm, uốn, duỗi), tác động nhiệt (sấy, tạo kiểu) và các yếu tố môi trường.  Dưỡng ẩm và làm mềm mượt: Cung cấp độ ẩm cần thiết, giúp tóc mềm mại, bóng mượt và dễ tạo kiểu hơn.  Công thức an toàn: 100% thuần chay (vegan friendly), không chứa paraben, SLS/SLES, và có độ pH 6.5, an toàn cho da đầu và tóc.  Hiệu quả rõ rệt: Tóc được tái tạo rõ rệt, giảm gãy rụng, chẻ ngọn, trở nên đầy đặn, dày dặn và mềm mại khi chạm vào.   Thông tin sản phẩm: Thương hiệu: Lisap Milano (Ý) Thành phần chính: Chứa Phức hợp Protein thực vật (Vegetal Protein Complex), có tác dụng bảo vệ, sửa chữa và tái cấu trúc tóc từ trong ra ngoài mà không gây nặng tóc. Dung tích: 1000ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da  Cách sử dụng 1. Làm ướt tóc bằng nước ấm. 2, Lấy một lượng dầu gội vừa đủ thoa lên tóc và da đầu, tạo bọt và mát-xa nhẹ nhàng. 3. Để dầu gội trên tóc trong vài phút để các dưỡng chất thẩm thấu. 4. Xả kỹ tóc bằng nước sạch. 5. Để đạt hiệu quả tốt nhất, nên sử dụng tiếp tục với Dầu xả Lisaplex Bond Saver Conditioner hoặc Nước dưỡng Lamellar Water cùng dòng sản phẩm.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Lisap Lisaplex Bond Saver Conditioner")
					.currentPrice(1100000)
					.productDescription("Dầu xả Lisap Lisaplex Bond Saver Conditioner là loại dầu xả phục hồi chuyên nghiệp được thiết kế để củng cố và dưỡng ẩm cho tóc hư tổn, yếu hoặc đã qua xử lý hóa chất. Thành phần chính của nó là Phức hợp Protein Thực vật độc quyền có tác dụng xây dựng lại cấu trúc bên trong của tóc và bảo vệ bề mặt tóc.  Các tính năng và lợi ích chính :  Phục hồi liên kết: Được làm giàu bằng Phức hợp Protein Thực vật (hỗn hợp protein và axit amin tự nhiên), dầu xả này có tác dụng phục hồi các liên kết disulfide trong cấu trúc tóc, giúp củng cố tóc và giảm gãy rụng.  Dưỡng ẩm sâu: Nó cung cấp độ ẩm và dinh dưỡng hiệu quả, nhẹ nhàng cho mọi loại tóc mà không gây nặng tóc, điều này đặc biệt có lợi cho tóc mỏng và yếu.  Giảm chẻ ngọn và xoăn cứng: Công thức làm mượt lớp biểu bì tóc, giúp giảm sự xuất hiện của chẻ ngọn và chống xoăn cứng, mang lại mái tóc khỏe mạnh, mềm mại và bóng mượt.  Bảo vệ: Nó giúp tóc chống lại hư tổn do xử lý hóa chất (nhuộm màu, tẩy, duỗi), tác động của môi trường và các tác động cơ học như chải và sấy tóc.  Thông tin sản phẩm: Thương hiệu: Lisap Milano (Ý) Thành phần chính: Chứa Phức hợp Protein thực vật (Vegetal Protein Complex), không chứa paraben, SLS/SLES (Sodium Lauryl Sulfate/Sodium Laureth Sulfate) và có độ pH thấp là 3.5. Dung tích: 1000ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da  Cách sử dụng 1. Sau khi gội đầu, thoa dầu xả một lượng vừa đủ lên phần thân và ngọn tóc đã được gội sạch. 2. Để yên trong vài phút để các thành phần thấm sâu vào thân tóc. 3. Xả kỹ bằng nước. 4. Để có kết quả tốt nhất, hãy tiếp tục sử dụng các sản phẩm khác trong dòng Lisaplex Bond Saver, chẳng hạn như Kem dưỡng Lisaplex Bond Saver Cream.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Revlon Professional Magnet Anti-Pollution Micellar Cleanser, 1000 ml  Dầu gội chống tác nhân ô nhiễm")
					.currentPrice(1000000)
					.productDescription("Đây là một loại dầu gội (cleanser) chuyên nghiệp của thương hiệu Revlon, được thiết kế để chống lại các tác nhân ô nhiễm môi trường.  Công dụng chính: Làm sạch nhẹ nhàng tóc và da đầu khỏi các tạp chất và chất ô nhiễm từ môi trường. Giảm thiểu sự tích tụ các hạt ô nhiễm và trung hòa các tác động tiêu cực của nguồn nước kém chất lượng. Giúp chống lại các tác nhân gây hại từ môi trường ảnh hưởng đến độ bóng, độ mềm mại và sự chuyển động tự nhiên của tóc.  Loại tóc: Phù hợp với mọi loại tóc.  Thông tin sản phẩm: Thương hiệu: Revlon Professional. Công nghệ: Sử dụng công nghệ micellar  làm sạch hiệu quả mà vẫn dịu nhẹ, không làm mất đi độ ẩm tự nhiên của tóc và da đầu Dung tích: 1000ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Bolari Hair Styling Gel - Gel tạo kiểu Bolari")
					.currentPrice(400000)
					.productDescription("Gel tạo kiểu tóc Bolari (Bolari Hair Styling Gel) là một sản phẩm dạng gel hoặc kem gel chuyên biệt, chủ yếu được sử dụng để giữ nếp và tạo độ ẩm cho tóc uốn xoăn, bồng bềnh, bóng mượt. Sản phẩm được đánh giá cao vì khả năng giữ nếp mà không gây bết dính hay khô cứng tóc.   Đặc điểm và Công dụng chính Giữ nếp tóc uốn xoăn: Công dụng nổi bật nhất của gel Bolari là tạo ra các lọn tóc xoăn hoặc gợn sóng tự nhiên, rõ nét, bồng bềnh.  Dưỡng ẩm tức thì: Sản phẩm giúp bổ sung độ ẩm cho tóc, kiểm soát tình trạng tóc bông xù, khô rối.  Mềm mượt và bóng sáng: Gel giúp tóc trở nên mềm mại, óng ả và bóng mượt tự nhiên nhờ các thành phần dưỡng chất.  Không bết dính, không gàu: Người dùng đánh giá sản phẩm không gây cảm giác bết dính, nhờn rít hay tạo gàu khi sử dụng.  Mùi hương dễ chịu: Sản phẩm thường có mùi nước hoa hoặc mùi thơm dễ chịu  Thông tin sản phẩm: Thương hiệu: Bolari,  được Công ty TNHH XNK và TM An Phương nhập khẩu và phân phối độc quyền tại thị trường Việt Nam  Chứa các thành phần giàu dưỡng chất như: - Dầu Argan và các loại vitamin: Giúp nuôi dưỡng tóc, phục hồi tóc hư tổn.-  - Kết cấu gel sữa nhẹ: Giúp thẩm thấu nhanh vào tóc.  Dung tích: 300ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng  Hướng dẫn sử dụng cơ bản 1. Gel tạo kiểu Bolari có thể được sử dụng trên nền tóc ẩm hoặc khô. 2. Làm sạch tóc: Nên gội sạch đầu trước khi thoa gel để sản phẩm đạt hiệu quả tốt nhất. 3. Lấy một lượng gel vừa đủ: Cho một lượng gel vừa phải ra lòng bàn tay. 4. Thoa đều lên tóc: Xoa đều hai tay và bóp nhẹ nhàng lên các lọn tóc xoăn hoặc vùng tóc cần tạo kiểu. Bắt đầu từ phía sau và thoa đều ra phía trước. Tạo kiểu: Bóp tóc theo nếp xoăn mong muốn hoặc dùng lược phân bổ đều sản phẩm. Có thể dùng thêm nước nếu gel bị dính quá.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Jenoris Keratin Hair Mask")
					.currentPrice(650000)
					.productDescription("Mặt nạ tóc Keratin Jenoris 500ml là sản phẩm chăm sóc tóc chuyên sâu, được thiết kế đặc biệt để phục hồi tóc khô xơ, hư tổn do xử lý hóa chất (uốn, duỗi, nhuộm) hoặc tác động nhiệt.   Đặc điểm nổi bật Phục hồi chuyên sâu: Sản phẩm chứa Keratin thủy phân giúp tái tạo và củng cố cấu trúc sợi tóc từ bên trong, khôi phục độ đàn hồi và sức sống cho tóc. An toàn cho tóc: Mặt nạ này không chứa Sodium Chloride (muối), paraben và sulfate, lý tưởng để sử dụng cho tóc đã qua các liệu trình duỗi keratin hoặc tóc nhuộm mà không làm phai màu hay gây kích ứng da đầu. Hiệu quả salon tại nhà: Giúp kiểm soát tóc xơ rối, ngăn ngừa chẻ ngọn và mang lại mái tóc mềm mại, bóng khỏe như được chăm sóc tại salon.   Thông tin sản phẩm: Thương hiệu: Jenoris. Thành phần tự nhiên: Công thức độc quyền được làm giàu với dầu hạt hồ trăn (Pistachio Oil) và dầu hạt lưu ly (Borage Seed Oil), cung cấp hàm lượng cao Omega 3, 6 và 9, cùng với Vitamin E, giúp nuôi dưỡng sâu, tăng cường độ bóng mượt và độ mềm mại cho tóc. Dung tích: 500ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng Chỉ tiêu: Không gây kích ứng da  Hướng dẫn sử dụng 1. Gội đầu sạch và lau khô tóc bằng khăn. 2. Lấy một lượng mặt nạ vừa đủ thoa đều lên tóc, tập trung vào phần thân và ngọn tóc bị hư tổn. 3. Massage nhẹ nhàng và để yên trên tóc từ 3-5 phút (có thể để đến 12 phút đối với tóc hư tổn nặng). 4. Xả sạch lại bằng nước. 5. Để đạt hiệu quả tối ưu, nên sử dụng kết hợp với Dầu gội Keratin Jenoris.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Revlon Professional Magnet Anti-Pollution Rest Mask, 500 ml  Hấp bảo vệ tóc khỏi ô nhiễm")
					.currentPrice(1200000)
					.productDescription("Mặt nạ phục hồi chống ô nhiễm Revlon Magnet mang lại mái tóc khỏe mạnh và rực rỡ. Sản phẩm giúp chống lại tác động của kim loại và khoáng chất, khiến tóc bạn cảm thấy sạch, tươi mới và phục hồi sức sống.  Đặc điểm và Công dụng chính Bảo vệ toàn diện: Sản phẩm tạo ra một lớp màn chắn 360 độ chống lại các tác nhân gây hại từ môi trường như ô nhiễm và tia UV.  Chống lại kim loại nặng: Sử dụng công nghệ BondIN System+™ với các chất tạo chelate (chelating agents) giúp bắt giữ và loại bỏ các kim loại nặng có hại bám trên tóc, vốn có thể làm hỏng cấu trúc tóc, đặc biệt khi sử dụng dịch vụ hóa chất (nhuộm, tẩy).  Phục hồi và củng cố liên kết tóc: Chứa các thành phần như AHA (Alpha Hydroxy Acids), amide derivatives và amino acids giúp tạo và củng cố các liên kết bên trong sợi tóc, phục hồi độ chắc khỏe, độ đàn hồi và sự mềm mại tự nhiên cho tóc.  Ngăn ngừa gãy rụng và chẻ ngọn: Giúp làm mềm mượt lớp biểu bì tóc, mang lại mái tóc mềm mại, bóng mượt và khỏe mạnh hơn.  Phù hợp với mọi loại tóc: Có thể sử dụng cho tóc đã qua xử lý hóa chất hoặc tóc tự nhiên.   Thông tin sản phẩm: Thương hiệu: Revlon Professional. Dung tích: 500ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng  Hướng dẫn sử dụng 1. Sau khi gội đầu sạch (nên dùng kèm dầu gội Revlon Magnet Anti-Pollution), lau tóc ráo nước. 2. Lấy một lượng mặt nạ vừa đủ, thoa đều lên thân và ngọn tóc. 3. Để yên từ 3 đến 10 phút để dưỡng chất thẩm thấu sâu vào sợi tóc. 4. Xả sạch tóc với nước.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Pantino Argan Oil Nutrition and Smoothing Repairing Shampoo 01")
					.currentPrice(600000)
					.productDescription("Pantino Argan Oil Nutrition and Smoothing Repairing Shampoo 01 là một sản phẩm đặc trị gầu nấm , tóc bông xù, tóc gãy ngọn, táo cấu trúc tóc, kích tóc mọc lại, giữ màu tóc nhuộm  Dầu gội Pantino Argan Oil 01 là lựa chọn lý tưởng cho những ai có mái tóc: Khô, xơ rối, thiếu sức sống. Hư tổn do xử lý hóa chất (uốn, nhuộm, duỗi). Thường xuyên sử dụng nhiệt để tạo kiểu. Cần được chăm sóc chuyên sâu tại nhà như quy trình salon.  Thông tin sản phẩm: Xuất xứ: Fomula in England Nhập khẩu và phân phối : Patino Cosmetic Thành phần: Đặc trưng là mùi hương hoa cỏ, trái cây, vitamin B7, vitamin B8, Glycol Diskable Glycerin và Mica tạo bóng Dung tích: 1000ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng  Hướng dân sử dụng : Làm ướt tóc, thoa một lượng dầu gội vừa đủ lên tóc, massage nhẹ nhàng. Sau đó xả bằng nước sạch.")
					.activeStatus(true)
					.build());
			productRepo.save(Product.builder()
					.productName("Pantino Argan Oil Nutrition and Smoothing Repairing Conditioner 02")
					.currentPrice(700000)
					.productDescription("Pantino Argan Oil Nutrition and Smoothing Repairing Condition 02 là một sản phẩm đặc trị gầu nấm , tóc bông xù, tóc gãy ngọn, táo cấu trúc tóc, kích tóc mọc lại, giữ màu tóc nhuộm  Dầu xả Pantino Argan Oil 02 là lựa chọn lý tưởng cho những ai có mái tóc: Khô, xơ rối, thiếu sức sống. Hư tổn do xử lý hóa chất (uốn, nhuộm, duỗi). Thường xuyên sử dụng nhiệt để tạo kiểu. Cần được chăm sóc chuyên sâu tại nhà như quy trình salon.  Thông tin sản phẩm: Xuất xứ: Fomula in England Nhập khẩu và phân phối : Patino Cosmetic Thành phần: Đặc trưng là mùi hương hoa cỏ, trái cây, vitamin B7, vitamin B8, Glycol Diskable Glycerin và Mica tạo bóng Dung tích: 1000ml Bảo quản: Để nơi thoáng mát, nhiệt độ phòng  Hướng dân sử dụng : Sau khi gội sạch, thoa một lượng dầu xả vừa đủ lên tóc. Ủ 5 phút, massage nhẹ nhàng. Sau đó xả bằng nước sạch.")
					.activeStatus(true)
					.build());

			serviceRepo.save(Service.builder()
					.serviceName("Chuyên viên cao cấp")
					.serviceCategory(cắttócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(250000)
					.durationMinutes((short) 20)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Thiết kế mẫu tóc")
					.serviceCategory(cắttócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(300000)
					.durationMinutes((short) 30)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Cắt tóc nam")
					.serviceCategory(cắttócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(150000)
					.durationMinutes((short) 15)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Gội Thường")
					.serviceCategory(gộisấytạokiểuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(50000)
					.durationMinutes((short) 25)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Gội Đầu Cặp")
					.serviceCategory(gộisấytạokiểuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(70000)
					.durationMinutes((short) 25)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Gội Đặc Biệt")
					.serviceCategory(gộisấytạokiểuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(100000)
					.durationMinutes((short) 30)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Sấy Tạo Kiểu")
					.serviceCategory(gộisấytạokiểuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(50000)
					.durationMinutes((short) 15)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Hư Tổn Do Làm Hóa Chất - Tóc Ngắn")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(600000)
					.durationMinutes((short) 40)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Hư Tổn Do Làm Hóa Chất - Tóc Lỡ")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 40)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Hư Tổn Do Làm Hóa Chất - Tóc Dài")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 40)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Tóc Khô,Bị Mất Nước, Hư Tổn Nhẹ - Tóc Ngắn")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(200000)
					.durationMinutes((short) 25)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Tóc Khô,Bị Mất Nước, Hư Tổn Nhẹ - Tóc Lỡ")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(300000)
					.durationMinutes((short) 25)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Chữa Trị Tóc Khô,Bị Mất Nước, Hư Tổn Nhẹ - Tóc Dài")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(400000)
					.durationMinutes((short) 25)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Dinh Dưỡng Bọc Tóc Trước Và Sau Khi Làm Hóa Chất - Tóc Ngắn")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(400000)
					.durationMinutes((short) 15)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Dinh Dưỡng Bọc Tóc Trước Và Sau Khi Làm Hóa Chất - Tóc Lỡ")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(500000)
					.durationMinutes((short) 15)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Dinh Dưỡng Bọc Tóc Trước Và Sau Khi Làm Hóa Chất - Tóc Dài")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(600000)
					.durationMinutes((short) 15)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Hấp Phục Hồi")
					.serviceCategory(hấptócchữatrịphụchồiCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(300000)
					.durationMinutes((short) 30)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Ép Cụp/ Phồng - Tóc Ngắn")
					.serviceCategory(éptóccụpCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 120)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Ép Cụp/ Phồng - Tóc Lỡ")
					.serviceCategory(éptóccụpCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 140)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Ép Cụp/ Phồng - Tóc Dài")
					.serviceCategory(éptóccụpCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1200000)
					.durationMinutes((short) 160)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Tóc Ngắn")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(700000)
					.durationMinutes((short) 90)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Tóc Lỡ")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 100)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Tóc Dài")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 120)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Highlight")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(500000)
					.durationMinutes((short) 90)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Chấm Chân Thời Trang")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(400000)
					.durationMinutes((short) 60)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Nhuộm Chấm Chân Phủ Bạc")
					.serviceCategory(nhuộmtócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(300000)
					.durationMinutes((short) 60)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Bóng Thường - Tóc Ngắn")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(600000)
					.durationMinutes((short) 60)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Bóng Thường - Tóc Lỡ")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 80)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Bóng Thường - Tóc Dài")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 100)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Mericue, phủ mịn màu tóc - Tóc Ngắn")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 80)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Mericue, phủ mịn màu tóc - Tóc Lỡ")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 100)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Tráng Mericue, phủ mịn màu tóc - Tóc Dài")
					.serviceCategory(trángmàutạobóngphủmàuCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1200000)
					.durationMinutes((short) 120)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Nguội - Tóc Ngắn")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(800000)
					.durationMinutes((short) 90)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Nguội - Tóc Lỡ")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1000000)
					.durationMinutes((short) 100)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Nguội - Tóc Dài")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1200000)
					.durationMinutes((short) 120)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Máy - Tóc Ngắn")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1200000)
					.durationMinutes((short) 180)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Máy - Tóc Lỡ")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1400000)
					.durationMinutes((short) 210)
					.activeStatus(true)
					.build());
			serviceRepo.save(Service.builder()
					.serviceName("Uốn Máy - Tóc Dài")
					.serviceCategory(uốntócCategory)
					.serviceType(ServiceType.SINGLE)
					.servicePrice(1600000)
					.durationMinutes((short) 240)
					.activeStatus(true)
					.build());
		}
	}


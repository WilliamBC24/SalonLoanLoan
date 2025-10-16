package service.sllbackend.dev;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.StaffCurrentPosition;
import service.sllbackend.entity.StaffPosition;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;
import service.sllbackend.entity.Supplier;
import service.sllbackend.entity.SupplierCategory;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.repository.StaffPositionRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.ProductImageRepo;
import service.sllbackend.entity.ProductImage;
import service.sllbackend.repository.VoucherRepo;
import service.sllbackend.repository.VoucherStatusRepo;
import service.sllbackend.repository.SupplierRepo;
import service.sllbackend.repository.SupplierCategoryRepo;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
	private final UserAccountRepo userAccountRepo;
	private final StaffRepo staffRepo;
	private final StaffAccountRepo staffAccountRepo;
	private final StaffPositionRepo staffPositionRepo;
	private final StaffCurrentPositionRepo staffCurrentPositionRepo;
	private final PasswordEncoder passwordEncoder;
	private final ServiceRepo serviceRepo;
	private final ServiceCategoryRepo serviceCategoryRepo;
    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;
    private final VoucherRepo voucherRepo;
    private final VoucherStatusRepo voucherStatusRepo;
    private final SupplierRepo supplierRepo;
    private final SupplierCategoryRepo supplierCategoryRepo;

	@Override
	public void run(String... args) {
		log.info("############ \n            Loading initial data\n############");
		registerUser();
		registerStaff();
		registerServices();
		registerProducts();
        registerVouchers();
        registerProviders();
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
	}

	public void registerStaff() {
		String name = "admin";
		String role = "Administrator";
		String username = "admin";
		String rawPassword = "admin";
		String hashedPassword = passwordEncoder.encode(rawPassword);

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

		staffAccountRepo.save(StaffAccount.builder()
				.staff(staff)
				.username(username)
				.password(hashedPassword)
				.accountStatus(AccountStatus.ACTIVE)
				.build());
	}

	public void registerServices() {
		// Create service categories
		ServiceCategory hairCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Hair Care")
				.build());
		
		ServiceCategory nailsCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Nails")
				.build());
		
		ServiceCategory skinCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Skin Care")
				.build());
		
		ServiceCategory massageCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Massage & Spa")
				.build());
		
		ServiceCategory makeupCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Makeup")
				.build());

		// Hair Care Services (10 services)
		serviceRepo.save(Service.builder()
				.serviceName("Classic Haircut")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(150000)
				.durationMinutes((short) 30)
				.serviceDescription("Professional haircut with styling")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Coloring")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 90)
				.serviceDescription("Full hair coloring service with premium products")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Highlights")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 120)
				.serviceDescription("Partial or full highlights for stunning hair")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Perm")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(500000)
				.durationMinutes((short) 150)
				.serviceDescription("Long-lasting curls or waves")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Straightening")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(600000)
				.durationMinutes((short) 180)
				.serviceDescription("Keratin treatment for smooth, straight hair")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Deep Conditioning Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 45)
				.serviceDescription("Intensive hair repair and moisture treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Scalp Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Rejuvenating scalp massage and treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Extensions")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(800000)
				.durationMinutes((short) 180)
				.serviceDescription("Premium quality hair extensions application")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Blowdry and Style")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(120000)
				.durationMinutes((short) 40)
				.serviceDescription("Professional blowdry with styling")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Spa Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 90)
				.serviceDescription("Complete hair spa with relaxation")
				.activeStatus(true)
				.build());

		// Nails Services (7 services)
		serviceRepo.save(Service.builder()
				.serviceName("Basic Manicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(100000)
				.durationMinutes((short) 30)
				.serviceDescription("Classic manicure with polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Gel Manicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(180000)
				.durationMinutes((short) 45)
				.serviceDescription("Long-lasting gel nail polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Basic Pedicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(120000)
				.durationMinutes((short) 45)
				.serviceDescription("Foot care with polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Gel Pedicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Spa pedicure with gel polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Nail Art Design")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(150000)
				.durationMinutes((short) 60)
				.serviceDescription("Custom nail art and designs")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Acrylic Nails")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 90)
				.serviceDescription("Full set of acrylic nail extensions")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Nail Removal Service")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(80000)
				.durationMinutes((short) 30)
				.serviceDescription("Safe removal of gel or acrylic nails")
				.activeStatus(true)
				.build());

		// Skin Care Services (6 services)
		serviceRepo.save(Service.builder()
				.serviceName("Basic Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Deep cleansing facial treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Anti-Aging Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 90)
				.serviceDescription("Premium anti-aging treatment with collagen")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Acne Treatment")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Specialized treatment for acne-prone skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Brightening Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 75)
				.serviceDescription("Vitamin C facial for radiant skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hydrating Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 60)
				.serviceDescription("Deep moisture treatment for dry skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Microdermabrasion")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(450000)
				.durationMinutes((short) 60)
				.serviceDescription("Exfoliation treatment for smooth skin")
				.activeStatus(true)
				.build());

		// Massage & Spa Services (4 services)
		serviceRepo.save(Service.builder()
				.serviceName("Swedish Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 60)
				.serviceDescription("Relaxing full body massage")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Deep Tissue Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 60)
				.serviceDescription("Therapeutic massage for muscle tension")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hot Stone Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 75)
				.serviceDescription("Relaxing massage with heated stones")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Aromatherapy Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(380000)
				.durationMinutes((short) 60)
				.serviceDescription("Massage with essential oils")
				.activeStatus(true)
				.build());

		// Makeup Services (3 services)
		serviceRepo.save(Service.builder()
				.serviceName("Event Makeup")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Professional makeup for special events")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Bridal Makeup")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(500000)
				.durationMinutes((short) 90)
				.serviceDescription("Complete bridal makeup package")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Makeup Lesson")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Learn professional makeup techniques")
				.activeStatus(true)
				.build());

		log.info("Successfully loaded 30 services across 5 categories");
	}

	public void registerProducts() {
		// Hair Care Products (8 products)
		Product product1 = productRepo.save(Product.builder()
				.productName("Professional Shampoo")
				.currentPrice(250000)
				.productDescription("Premium salon-grade shampoo for all hair types")
				.activeStatus(true)
				.build());
		
		// Add image for Professional Shampoo
		productImageRepo.save(ProductImage.builder()
				.product(product1)
				.imagePath("/img/Shampo.png")
				.build());

		Product product2 = productRepo.save(Product.builder()
				.productName("Luxury Conditioner")
				.currentPrice(280000)
				.productDescription("Deep conditioning formula for silky smooth hair")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product2)
				.imagePath("/img/LuxuryConditioner.png")
				.build());

		Product product3 = productRepo.save(Product.builder()
				.productName("Hair Serum")
				.currentPrice(350000)
				.productDescription("Anti-frizz serum for shiny, manageable hair")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product3)
				.imagePath("/img/serum.png")
				.build());

		Product product4 = productRepo.save(Product.builder()
				.productName("Hair Oil Treatment")
				.currentPrice(320000)
				.productDescription("Nourishing oil for damaged hair repair")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product4)
				.imagePath("/img/HairOilTreatment.png")
				.build());

		Product product5 = productRepo.save(Product.builder()
				.productName("Styling Gel")
				.currentPrice(180000)
				.productDescription("Strong hold gel for long-lasting styles")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product5)
				.imagePath("/img/StylingGel.png")
				.build());

		Product product6 = productRepo.save(Product.builder()
				.productName("Hair Spray")
				.currentPrice(220000)
				.productDescription("Professional finishing spray for all-day hold")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product6)
				.imagePath("/img/HairSpray.png")
				.build());

		Product product7 = productRepo.save(Product.builder()
				.productName("Hair Mask")
				.currentPrice(400000)
				.productDescription("Intensive repair mask for damaged hair")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product7)
				.imagePath("/img/HairMask.png")
				.build());

		Product product8 = productRepo.save(Product.builder()
				.productName("Dry Shampoo")
				.currentPrice(200000)
				.productDescription("Quick refresh between washes")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product8)
				.imagePath("/img/DryShampoo.png")
				.build());

		// Skin Care Products (8 products)
		Product product9 = productRepo.save(Product.builder()
				.productName("Facial Cleanser")
				.currentPrice(300000)
				.productDescription("Gentle cleansing for all skin types")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product9)
				.imagePath("/img/FacialCleanser.png")
				.build());

		Product product10 = productRepo.save(Product.builder()
				.productName("Vitamin C Serum")
				.currentPrice(550000)
				.productDescription("Brightening serum with antioxidants")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product10)
				.imagePath("/img/VitaminCSerum.png")
				.build());

		Product product11 = productRepo.save(Product.builder()
				.productName("Hyaluronic Acid Moisturizer")
				.currentPrice(480000)
				.productDescription("Intense hydration for plump, youthful skin")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product11)
				.imagePath("/img/HyaluronicAcidMoisturizer.png")
				.build());

		Product product12 = productRepo.save(Product.builder()
				.productName("Retinol Night Cream")
				.currentPrice(620000)
				.productDescription("Anti-aging night treatment")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product12)
				.imagePath("/img/RetinolNightCream.png")
				.build());

		Product product13 = productRepo.save(Product.builder()
				.productName("SPF 50 Sunscreen")
				.currentPrice(350000)
				.productDescription("Broad spectrum sun protection")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product13)
				.imagePath("/img/SPF50Sunscreen.png")
				.build());

		Product product14 = productRepo.save(Product.builder()
				.productName("Exfoliating Scrub")
				.currentPrice(280000)
				.productDescription("Gentle exfoliation for smooth skin")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product14)
				.imagePath("/img/ExfoliatingScrub.png")
				.build());

		Product product15 = productRepo.save(Product.builder()
				.productName("Eye Cream")
				.currentPrice(420000)
				.productDescription("Reduces dark circles and fine lines")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product15)
				.imagePath("/img/EyeCream.png")
				.build());

		Product product16 = productRepo.save(Product.builder()
				.productName("Face Mask Set")
				.currentPrice(380000)
				.productDescription("5-piece variety mask collection")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product16)
				.imagePath("/img/FaceMaskSet.png")
				.build());

		// Nail Care Products (7 products)
		Product product17 = productRepo.save(Product.builder()
				.productName("Gel Polish Set")
				.currentPrice(450000)
				.productDescription("12 colors long-lasting gel polish")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product17)
				.imagePath("/img/GelPolishSet.png")
				.build());

		Product product18 = productRepo.save(Product.builder()
				.productName("Nail Strengthener")
				.currentPrice(250000)
				.productDescription("Protein treatment for weak nails")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product18)
				.imagePath("/img/NailStrengthener.png")
				.build());

		Product product19 = productRepo.save(Product.builder()
				.productName("Cuticle Oil")
				.currentPrice(180000)
				.productDescription("Nourishing oil for healthy cuticles")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product19)
				.imagePath("/img/CuticleOil.png")
				.build());

		Product product20 = productRepo.save(Product.builder()
				.productName("Top Coat")
				.currentPrice(200000)
				.productDescription("Quick-dry glossy finish")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product20)
				.imagePath("/img/TopCoat.png")
				.build());

		Product product21 = productRepo.save(Product.builder()
				.productName("Base Coat")
				.currentPrice(200000)
				.productDescription("Protective base for nail polish")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product21)
				.imagePath("/img/BaseCoat.png")
				.build());

		Product product22 = productRepo.save(Product.builder()
				.productName("Nail File Set")
				.currentPrice(150000)
				.productDescription("Professional quality nail files")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product22)
				.imagePath("/img/NailFileSet.png")
				.build());

		Product product23 = productRepo.save(Product.builder()
				.productName("Nail Art Kit")
				.currentPrice(500000)
				.productDescription("Complete nail art design tools")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product23)
				.imagePath("/img/NailArtKit.png")
				.build());

		// Makeup Products (7 products)
		Product product24 = productRepo.save(Product.builder()
				.productName("Foundation")
				.currentPrice(580000)
				.productDescription("Full coverage matte foundation")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product24)
				.imagePath("/img/Foundation.png")
				.build());

		Product product25 = productRepo.save(Product.builder()
				.productName("Eyeshadow Palette")
				.currentPrice(650000)
				.productDescription("24-color professional palette")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product25)
				.imagePath("/img/EyeshadowPalette.png")
				.build());

		Product product26 = productRepo.save(Product.builder()
				.productName("Mascara")
				.currentPrice(320000)
				.productDescription("Volumizing and lengthening mascara")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product26)
				.imagePath("/img/Mascara.png")
				.build());

		Product product27 = productRepo.save(Product.builder()
				.productName("Lipstick Set")
				.currentPrice(480000)
				.productDescription("6 shades long-lasting lipstick")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product27)
				.imagePath("/img/LipstickSet.png")
				.build());

		Product product28 = productRepo.save(Product.builder()
				.productName("Makeup Brushes")
				.currentPrice(550000)
				.productDescription("15-piece professional brush set")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product28)
				.imagePath("/img/MakeupBrushes.png")
				.build());

		Product product29 = productRepo.save(Product.builder()
				.productName("Setting Spray")
				.currentPrice(350000)
				.productDescription("All-day makeup setting spray")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product29)
				.imagePath("/img/SettingSpray.png")
				.build());

		Product product30 = productRepo.save(Product.builder()
				.productName("Makeup Remover")
				.currentPrice(280000)
				.productDescription("Gentle oil-free makeup remover")
				.activeStatus(true)
				.build());
		
		productImageRepo.save(ProductImage.builder()
				.product(product30)
				.imagePath("/img/MakeupRemover.png")
				.build());

		log.info("Successfully loaded 30 products");
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
				.voucherStatus(activeStatus)
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
}
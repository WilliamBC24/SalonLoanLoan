// Minimal dynamic population to avoid hardcoded content on the home page
// Adjust API_BASE if backend runs elsewhere
const API_BASE = 'http://localhost:8080';

const endpoints = {
  site: `${API_BASE}/api/site-info`, // site info, content, navigation
  services: `${API_BASE}/services`,
  products: `${API_BASE}/products`,
  blogs: `${API_BASE}/api/blogs`, // optional
  staff: `${API_BASE}/api/staff`, // optional
};

async function fetchJson(url) {
  try {
    const res = await fetch(url, { headers: { Accept: 'application/json' } });
    if (!res.ok) return null;
    const ct = res.headers.get('content-type') || '';
    if (!ct.includes('application/json')) return null;
    return await res.json();
  } catch {
    return null;
  }
}

function el(html) {
  const template = document.createElement('template');
  template.innerHTML = html.trim();
  return template.content.firstElementChild;
}

function setSiteInfo(info) {
  if (!info) return;

  // Top header contact info
  const email = document.getElementById('siteEmail');
  const phone = document.getElementById('sitePhone');
  if (email) email.innerHTML = `<i class="fa-solid fa-envelope"></i> ${info.contactInfo?.email ?? ''}`;
  if (phone) phone.innerHTML = `<i class="fa-solid fa-phone"></i> ${info.contactInfo?.phone ?? ''}`;

  // Navigation menu
  const mainMenu = document.getElementById('mainMenu');
  if (mainMenu && Array.isArray(info.navigation)) {
    mainMenu.innerHTML = '';
    info.navigation.forEach((item, index) => {
      const li = el(`<li class="nav-item">
        <a class="nav-link ${index === 0 ? 'active' : ''}" href="${item.href ?? '#'}">${item.label ?? ''}</a>
      </li>`);
      mainMenu.append(li);
    });
  }

  // Booking section
  const bookingTitle = document.getElementById('bookingTitle');
  const bookingSubtitle = document.getElementById('bookingSubtitle');
  const bookingInput = document.getElementById('bookingInput');
  const bookingButton = document.getElementById('bookingButton');

  if (bookingTitle) bookingTitle.textContent = info.booking?.title ?? 'Chào mừng đến với Hair Salon';
  if (bookingSubtitle) bookingSubtitle.textContent = info.booking?.subtitle ?? 'Đặt lịch để giữ chỗ';
  if (bookingInput) bookingInput.placeholder = info.booking?.inputPlaceholder ?? 'Nhập SĐT để đặt lịch';
  if (bookingButton) bookingButton.textContent = info.booking?.buttonText ?? 'Đặt Lịch';

  // Section titles
  const servicesTitle = document.getElementById('servicesTitle');
  const spaTitle = document.getElementById('spaTitle');
  const teamTitle = document.getElementById('teamTitle');
  const blogTitle = document.getElementById('blogTitle');

  if (servicesTitle) servicesTitle.textContent = info.sections?.services?.title ?? '';
  if (spaTitle) spaTitle.textContent = info.sections?.spa?.title ?? '';
  if (teamTitle) teamTitle.textContent = info.sections?.team?.title ?? '';
  if (blogTitle) blogTitle.textContent = info.sections?.blog?.title ?? '';

  // Section descriptions
  const teamDescription = document.getElementById('teamDescription');
  const blogDescription = document.getElementById('blogDescription');

  if (teamDescription) teamDescription.textContent = info.sections?.team?.description ?? '';
  if (blogDescription) blogDescription.textContent = info.sections?.blog?.description ?? '';

  // Footer content
  const footerBrandName = document.getElementById('footerBrandName');
  const footerAddresses = document.getElementById('footerAddresses');
  const footerSocials = document.getElementById('footerSocials');
  const workingHoursTitle = document.getElementById('workingHoursTitle');
  const workingHoursContent = document.getElementById('workingHoursContent');
  const contactTitle = document.getElementById('contactTitle');
  const contactEmail = document.getElementById('contactEmail');
  const contactPhone = document.getElementById('contactPhone');

  if (footerBrandName) footerBrandName.textContent = info.footer?.brandName ?? '';
  if (footerAddresses) footerAddresses.innerHTML = info.footer?.addresses?.map(addr => `${addr.label}: ${addr.address}`).join('<br>') ?? '';
  if (workingHoursTitle) workingHoursTitle.textContent = info.footer?.workingHours?.title ?? '';
  if (workingHoursContent) workingHoursContent.innerHTML = info.footer?.workingHours?.content?.map(hour => hour).join('<br>') ?? '';
  if (contactTitle) contactTitle.textContent = info.footer?.contact?.title ?? '';
  if (contactEmail) contactEmail.innerHTML = `<i class="fa-regular fa-envelope"></i> ${info.footer?.contact?.email ?? ''}`;
  if (contactPhone) contactPhone.innerHTML = `<i class="fa-solid fa-phone"></i> ${info.footer?.contact?.phone ?? ''}`;

  // Social links
  if (footerSocials && Array.isArray(info.footer?.socials)) {
    footerSocials.innerHTML = '';
    info.footer.socials.forEach(social => {
      const link = el(`<a href="${social.href ?? '#'}"><i class="${social.icon ?? ''}"></i></a>`);
      footerSocials.append(link);
    });
  }

  // Footer links
  const footerLinksTitle = document.getElementById('footerLinksTitle');
  const footerLinks = document.getElementById('footerLinks');
  if (footerLinksTitle) footerLinksTitle.textContent = info.footer?.links?.title ?? '';
  if (footerLinks && Array.isArray(info.footer?.links?.items)) {
    footerLinks.innerHTML = '';
    info.footer.links.items.forEach(link => {
      const li = el(`<li><a href="${link.href ?? '#'}">${link.label ?? ''}</a></li>`);
      footerLinks.append(li);
    });
  }

  // Footer bottom
  const privacyPolicy = document.getElementById('privacyPolicy');
  const termsOfUse = document.getElementById('termsOfUse');
  if (privacyPolicy) privacyPolicy.textContent = info.footer?.bottom?.privacy ?? '';
  if (termsOfUse) termsOfUse.textContent = info.footer?.bottom?.terms ?? '';

  // Page title
  const pageTitle = document.getElementById('pageTitle');
  if (pageTitle) pageTitle.textContent = info.pageTitle ?? 'Loan Loan Hair Salon';

  // Hero slides
  if (Array.isArray(info.heroSlides) && info.heroSlides.length > 0) {
    const slidesWrap = document.getElementById('heroSlides');
    const dotsWrap = document.getElementById('heroDots');
    slidesWrap.innerHTML = '';
    dotsWrap.innerHTML = '';
    info.heroSlides.forEach((s, i) => {
      slidesWrap.append(
        el(`<div class="carousel-item ${i === 0 ? 'active' : ''}">
              <img src="${s.image}" class="hero-img" alt="slide-${i}">
              <div class="hero-overlay"></div>
              <div class="hero-caption ${s.align ?? 'text-start'}">
                <h2 class="anim-1">${s.title ?? ''}</h2>
                <p class="anim-2">${s.subtitle ?? ''}</p>
                ${s.cta ? `<a href="${s.cta.href ?? '#'}" class="btn btn-light rounded-pill px-4 fw-semibold anim-3">${s.cta.label ?? 'Xem thêm'}</a>` : ''}
              </div>
            </div>`) );
      const dot = el(`<button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="${i}" class="${i===0?'active':''}"></button>`);
      dotsWrap.append(dot);
    });
  }
}

function renderServiceIcons(services) {
  const row = document.getElementById('servicesIconRow');
  if (!row) return;
  row.innerHTML = '';
  (services || []).slice(0, 5).forEach(s => {
    row.append(
      el(`<div class="icon-item">
            <button class="icon-circle"><img src="${s.icon ?? 'img/salon_1057369.png'}" alt=""></button>
            <p>${s.serviceName ?? ''}</p>
          </div>`)
    );
  });
}

function renderServiceMenu(services) {
  const menu = document.getElementById('serviceMenu');
  const detail = document.getElementById('serviceDetail');
  if (!menu || !detail) return;
  menu.innerHTML = '';
  const list = (services || []).slice(0, 6);
  list.forEach((s, idx) => {
    const btn = el(`<button class="service-pill ${idx===0?'active':''}">${s.serviceName ?? ''}</button>`);
    btn.addEventListener('click', () => {
      menu.querySelectorAll('.service-pill').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      renderServiceDetail(s);
    });
    menu.append(btn);
  });
  if (list.length) renderServiceDetail(list[0]);
}

function renderServiceDetail(s) {
  const detail = document.getElementById('serviceDetail');
  if (!detail) return;
  detail.innerHTML = '';
  detail.append(
    el(`<div class="service-image"><img src="${s.image ?? 'img/banner1.jpg'}" alt="${s.serviceName ?? ''}"></div>`),
    el(`<div class="service-info">
          <h3>${s.serviceName ?? ''}</h3>
          <p class="price">${s.servicePrice ? new Intl.NumberFormat('vi-VN').format(s.servicePrice) + ' VND' : ''}</p>
          <div class="stars">★★★★★</div>
          <p class="desc">${s.serviceDescription ?? ''}</p>
          <a href="/services" class="read-more">Đọc thêm</a>
        </div>`)
  );
}

function renderSpa(spaItems) {
  const grid = document.getElementById('spaGrid');
  if (!grid) return;
  grid.innerHTML = '';
  (spaItems || []).slice(0, 3).forEach(i => {
    grid.append(
      el(`<div class="col-md-4">
            <div class="spa-card">
              <img src="${i.image}" class="img-fluid" alt="">
              <span class="spa-chip">${i.label ?? ''}</span>
            </div>
          </div>`)
    );
  });
}

function renderStaff(staff) {
  const grid = document.getElementById('staffGrid');
  if (!grid) return;
  grid.innerHTML = '';
  (staff || []).slice(0, 3).forEach(p => {
    grid.append(el(`<div class="col-md-4"><img src="${p.image}" class="rounded-4 shadow w-100" alt="${p.name ?? ''}"></div>`));
  });
}

function renderBlogs(blogs) {
  const grid = document.getElementById('blogsGrid');
  if (!grid) return;
  grid.innerHTML = '';
  const list = blogs || [];
  if (!list.length) return;
  // Left big article
  const main = list[0];
  grid.append(el(`<div class="col-lg-8">
    <article class="blog-card h-100">
      <img src="${main.image}" class="blog-thumb" alt="">
      <div class="blog-body">
        <div class="blog-meta">
          <span class="author"><i class="fa-regular fa-user"></i> ${main.author ?? ''}</span>
          <span class="date"><i class="fa-regular fa-calendar"></i> ${main.date ?? ''}</span>
        </div>
        <h5>${main.title ?? ''}</h5>
        <a href="${main.href ?? '#'}" class="btn btn-sm btn-dark rounded-pill px-3">Read More</a>
      </div>
    </article>
  </div>`));
  // Right two articles
  const right = el(`<div class="col-lg-4 d-flex flex-column gap-4"></div>`);
  list.slice(1, 3).forEach(b => {
    right.append(el(`<article class="blog-mini">
        <div class="blog-mini-meta">
          <span class="author"><i class="fa-regular fa-user"></i> ${b.author ?? ''}</span>
          <span class="date"><i class="fa-regular fa-calendar"></i> ${b.date ?? ''}</span>
        </div>
        <h6>${b.title ?? ''}</h6>
        <a href="${b.href ?? '#'}" class="btn btn-sm btn-secondary rounded-pill px-3">Read More</a>
      </article>`));
  });
  grid.append(right);
}

function renderServicesGrid(services) {
  const grid = document.getElementById('servicesGrid');
  if (!grid) return;
  grid.innerHTML = '';
  (services || []).forEach(s => {
    grid.append(el(`<div class="col-md-4 col-sm-6">
      <div class="service-card shadow-sm">
        <img src="${s.image ?? '/img/default-service.jpg'}" alt="${s.serviceName ?? ''}">
        <div class="service-body">
          <h5>${s.serviceName ?? ''}</h5>
          <p class="price text-muted">Giá tiêu chuẩn: ${s.servicePrice ? new Intl.NumberFormat('vi-VN').format(s.servicePrice) + ' VND' : 'Liên hệ'}</p>
          <button class="btn btn-outline-dark rounded-pill px-4">Chọn</button>
        </div>
      </div>
    </div>`));
  });
}

function renderProductsGrid(products) {
  const grid = document.getElementById('productsGrid');
  if (!grid) return;
  grid.innerHTML = '';
  (products || []).forEach(p => {
    grid.append(el(`<div class="col-lg-4 col-md-6">
      <div class="product-card">
        <img src="${p.image ?? '/img/default-product.jpg'}" alt="${p.productName ?? ''}" class="product-image">
        <div class="product-body">
          <h5 class="product-title">${p.productName ?? ''}</h5>
          <p class="product-price">${p.price ? new Intl.NumberFormat('vi-VN').format(p.price) + ' VND' : 'Liên hệ'}</p>
          <div class="product-rating">
            <div class="stars">★★★★★</div>
          </div>
        </div>
      </div>
    </div>`));
  });
}

function renderTopSearches(searches) {
  const grid = document.getElementById('topSearchesGrid');
  if (!grid) return;
  grid.innerHTML = '';
  (searches || []).forEach(s => {
    grid.append(el(`<div class="col-md-3 col-sm-6">
      <div class="product-card">
        <img src="${s.image ?? '/img/default-search.jpg'}" alt="${s.name ?? ''}" class="product-image">
        <div class="product-body">
          <h5 class="product-title">${s.name ?? ''}</h5>
        </div>
      </div>
    </div>`));
  });
}


// Mock data for testing when API is not available
const mockSiteInfo = {
  pageTitle: "Loan Loan Hair Salon",
  contactInfo: {
    email: "loanloanhairspa@gmail.com",
    phone: "+84 0973.801.972"
  },
  navigation: [
    { label: "Dịch vụ", href: "#services" },
    { label: "Shop", href: "#shop" },
    { label: "Tuyển dụng", href: "#careers" },
    { label: "Giới thiệu", href: "#about" },
    { label: "Blogs", href: "#blogs" }
  ],
  booking: {
    title: "Chào mừng đến với Hair Salon",
    subtitle: "Đặt lịch để giữ chỗ",
    inputPlaceholder: "Nhập SĐT để đặt lịch",
    buttonText: "Đặt Lịch"
  },
  sections: {
    services: { title: "Dịch Vụ" },
    spa: { title: "Trung tâm làm đẹp và spa tóc" },
    team: {
      title: "Chuyên gia giàu kinh nghiệm tại Loan Loan Hair Spa",
      description: "Tại Loan Loan Hair Salon chúng tôi tự hào sở hữu đội ngũ chuyên gia trải nghiệm tận tâm và giàu kinh nghiệm. Được đào tạo bài bản trong lĩnh vực làm đẹp và chăm sóc tóc, các chuyên gia của chúng tôi không chỉ am hiểu về các xu hướng mới nhất mà còn luôn lắng nghe và thấu hiểu nhu cầu riêng biệt của từng khách hàng."
    },
    blog: {
      title: "Blogs",
      description: "💬 Hãy để Loan Loan Hair Spa chăm sóc mái tóc của bạn như chăm chính cảm xúc của bạn. Vì bạn xứng đáng với những điều tốt đẹp nhất!"
    }
  },
  footer: {
    brandName: "Loan Loan Hair Salon",
    addresses: [
      { label: "Cơ sở 1", address: "76 Nguyễn Thúc Kháng - Ba Đình - Hà Nội" },
      { label: "Cơ sở 2", address: "15/16 Huỳnh Thúc Kháng - Ba Đình - Hà Nội" }
    ],
    workingHours: {
      title: "Thời gian làm việc",
      content: ["Từ thứ 2 - thứ 7: 8h - 20h", "Chủ nhật: 8h - 18h"]
    },
    contact: {
      title: "Liên Hệ",
      email: "loanloanhairspa@gmail.com",
      phone: "+84 0973.801.972"
    },
    socials: [
      { href: "#", icon: "fa-brands fa-facebook-f" },
      { href: "#", icon: "fa-brands fa-instagram" },
      { href: "#", icon: "fa-brands fa-tiktok" }
    ],
    links: {
      title: "Resent Post",
      items: [
        { label: "About", href: "#about" },
        { label: "FAQ", href: "#faq" },
        { label: "Career", href: "#careers" },
        { label: "Customer", href: "#customer" },
        { label: "Services", href: "#services" }
      ]
    },
    bottom: {
      privacy: "Privacy Policy",
      terms: "Terms of Use"
    }
  },
  heroSlides: [
    {
      image: "img/banner1.jpg",
      title: "Chào mừng đến với Loan Loan Hair Salon",
      subtitle: "Nơi tạo nên vẻ đẹp tự nhiên và phong cách cá nhân",
      align: "text-start",
      cta: { label: "Khám phá ngay", href: "#services" }
    },
    {
      image: "img/banner2.jpg",
      title: "Dịch vụ chuyên nghiệp",
      subtitle: "Đội ngũ chuyên gia giàu kinh nghiệm",
      align: "text-center",
      cta: { label: "Đặt lịch", href: "#booking" }
    }
  ],
  spaCards: [
    { image: "img/spa1.jpg", label: "Spa tóc cao cấp" },
    { image: "img/spa2.jpg", label: "Massage da đầu" },
    { image: "img/spa3.jpg", label: "Chăm sóc tóc chuyên sâu" }
  ],
  staff: [
    { image: "img/staff1.jpg", name: "Chuyên gia A" },
    { image: "img/staff2.jpg", name: "Chuyên gia B" },
    { image: "img/staff3.jpg", name: "Chuyên gia C" }
  ],
  blogs: [
    {
      image: "img/blog1.jpg",
      title: "Xu hướng tóc hot nhất 2024",
      author: "Loan Loan Team",
      date: "15/01/2024",
      href: "#"
    },
    {
      image: "img/blog2.jpg",
      title: "Cách chăm sóc tóc tại nhà",
      author: "Chuyên gia A",
      date: "10/01/2024",
      href: "#"
    },
    {
      image: "img/blog3.jpg",
      title: "Lựa chọn kiểu tóc phù hợp",
      author: "Chuyên gia B",
      date: "05/01/2024",
      href: "#"
    }
  ],
  topSearches: [
    { name: "Kem tẩy da chết", image: "img/search1.jpg" },
    { name: "Sáp vuốt tóc", image: "img/search2.jpg" },
    { name: "Combo sáp vuốt tóc", image: "img/search3.jpg" },
    { name: "Kem giảm mụn", image: "img/search4.jpg" }
  ]
};

async function bootstrap() {
  // Try to fetch from API first, fallback to mock data
  let site = await fetchJson(endpoints.site);
  if (!site) {
    console.log('API not available, using mock data');
    site = mockSiteInfo;
  }
  setSiteInfo(site);

  // services
  const services = await fetchJson(endpoints.services);
  renderServiceIcons(services);
  renderServiceMenu(services);
  renderServicesGrid(services); // For services page

  // products
  const products = await fetchJson(endpoints.products);
  renderProductsGrid(products); // For products page
  renderTopSearches(site?.topSearches ?? []); // For products page

  // spa (use site data or mock)
  renderSpa(site?.spaCards ?? []);

  // staff/blogs
  renderStaff((await fetchJson(endpoints.staff)) ?? site?.staff ?? []);
  renderBlogs((await fetchJson(endpoints.blogs)) ?? site?.blogs ?? []);
  
  // Initialize page specific content
  initProductsPage();
  initServicesPage();
  initIndexPage();
}

// Initialize products page specific content
function initProductsPage() {
  // Set page title
  const pageTitle = document.getElementById('pageTitle');
  if (pageTitle) {
    pageTitle.textContent = pageTitle.textContent || 'Sản Phẩm – Loan Loan Hair Salon';
  }
  
  // Set search placeholder
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.placeholder = searchInput.placeholder || 'nhãn hiệu, sản phẩm';
  }
  
  // Set feature bar content
  const feature1 = document.getElementById('feature1');
  if (feature1) {
    feature1.textContent = feature1.textContent || 'Hỗ trợ giao hàng hoà tốc';
  }
  
  const feature2 = document.getElementById('feature2');
  if (feature2) {
    feature2.textContent = feature2.textContent || 'Hoàn tiền 100%';
  }
  
  const feature3 = document.getElementById('feature3');
  if (feature3) {
    feature3.textContent = feature3.textContent || 'Đổi trả hàng đơn giản thuận tiện';
  }
  
  // Set section titles
  const topSearchesTitle = document.getElementById('topSearchesTitle');
  if (topSearchesTitle) {
    topSearchesTitle.textContent = topSearchesTitle.textContent || 'Top Tìm Kiếm';
  }
  
  const productsTitle = document.getElementById('productsTitle');
  if (productsTitle) {
    productsTitle.textContent = productsTitle.textContent || 'Gợi ý hôm nay - Lựa chọn được mọi người ưa chuộng';
  }
}

// Initialize services page specific content
function initServicesPage() {
  // Set page title
  const pageTitle = document.getElementById('pageTitle');
  if (pageTitle) {
    pageTitle.textContent = pageTitle.textContent || 'Dịch Vụ – Loan Loan Hair Salon';
  }
  
  // Set search placeholder
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.placeholder = searchInput.placeholder || 'Dịch vụ, combo';
  }
  
  // Set services title
  const servicesTitle = document.getElementById('servicesTitle');
  if (servicesTitle) {
    servicesTitle.textContent = servicesTitle.textContent || 'Gói Dịch Vụ';
  }
}

// Initialize index page specific content
function initIndexPage() {
  // Set page title
  const pageTitle = document.getElementById('pageTitle');
  if (pageTitle) {
    pageTitle.textContent = pageTitle.textContent || 'Loan Loan Hair Salon';
  }
  
  // Set search placeholder
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.placeholder = searchInput.placeholder || 'Dịch vụ, combo';
  }
}

bootstrap();



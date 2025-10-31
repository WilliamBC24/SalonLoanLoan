# Staff Sidebar Fragment


Fragment sidebar dành riêng cho các trang staff, cung cấp navigation thống nhất và dễ bảo trì.


## Cách sử dụng


### 1. Import fragment vào trang HTML


```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:lang="${#locale.language}">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title th:text="#{staff.products.title} + ' | ' + #{app.name}">Staff - Manage Products | Salon Loan Loan</title>
   
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" />
    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=Playfair+Display:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="d-flex">
        <!-- Sidebar Fragment -->
        <div th:replace="fragments/staff-sidebar :: staff-sidebar"></div>


        <!-- Main Content -->
        <div class="main-content">
            <!-- Header Fragment -->
            <div th:replace="fragments/staff-sidebar :: staff-header"></div>


            <!-- Content -->
            <div class="content">
                <!-- Nội dung trang của bạn -->
            </div>
        </div>
    </div>


    <!-- Scripts Fragment -->
    <div th:replace="fragments/staff-sidebar :: staff-scripts"></div>
</body>
</html>
```


### 2. Các fragment có sẵn


#### `staff-sidebar`
- Sidebar navigation với logo
- Menu items với submenu
- Auto-expand active submenu
- Mobile responsive


#### `staff-header`
- Header với nút Back
- User info và dropdown menu
- Mobile menu toggle


#### `staff-scripts`
- JavaScript cho mobile menu toggle
- Submenu toggle functionality
- Auto-expand active submenu


### 3. Cấu trúc menu


Sidebar bao gồm các menu chính:


- **Manage Products** (Quản lý sản phẩm)
  - Product List
  - Add Product
  - Edit Product


- **Manage Services** (Quản lý dịch vụ)
  - Service List
  - Add Service
  - Edit Service


- **Manage Promotions** (Quản lý khuyến mãi)
  - Promotion List
  - Add Promotion


- **Manage Providers** (Quản lý nhà cung cấp)
  - Provider List
  - Add Provider


- **Profile** (Thông tin cá nhân)


### 4. Active state


Fragment tự động xác định trang hiện tại và đánh dấu active state:


```html
<!-- Tự động active khi URL chứa '/staff/products/list' -->
<a th:href="@{/staff/products/list}" class="nav-submenu-item"
   th:classappend="${#httpServletRequest.requestURI.contains('/staff/products/list')} ? 'active' : ''">
    <span th:text="#{staff.products.list}">Product List</span>
</a>
```


### 5. i18n Keys cần thiết


Đảm bảo có các keys sau trong `messages.properties` và `messages_vi.properties`:


```properties
# App name
app.name=Salon Loan Loan


# Staff sections
staff.products.title=Manage Products
staff.products.list=Product List
staff.products.create=Add Product
staff.products.edit.title=Edit Product


staff.services.title=Manage Services
staff.services.list=Service List
staff.services.create=Add Service
staff.services.edit.title=Edit Service


staff.promotions.title=Manage Promotions
staff.promotions.list=Promotion List
staff.promotions.create=Add Promotion


staff.providers.title=Manage Providers
staff.providers.list=Provider List
staff.providers.create=Add Provider


staff.profile.title=Profile
staff.profile.edit.title=Edit Profile


staff.welcome=Welcome, Staff


# Buttons
btn.back=Back
btn.logout=Logout
```


### 6. CSS


Fragment sử dụng `staff-sidebar.css` với các CSS variables để dễ tùy chỉnh:


```css
:root {
    --sidebar-width: 280px;
    --sidebar-bg: #2c3e50;
    --sidebar-text: #ecf0f1;
    --sidebar-hover: #34495e;
    --sidebar-active: #3498db;
    /* ... */
}
```


### 7. Responsive Design


- Desktop: Sidebar cố định bên trái
- Mobile: Sidebar ẩn/hiện với toggle button
- Tablet: Sidebar có thể thu gọn


### 8. Lợi ích


- **Tái sử dụng**: Một fragment cho tất cả trang staff
- **Đồng bộ**: Thay đổi một lần, áp dụng cho tất cả
- **Bảo trì**: Dễ dàng cập nhật menu và styling
- **Responsive**: Tự động adapt với mọi kích thước màn hình
- **i18n**: Hỗ trợ đa ngôn ngữ hoàn chỉnh
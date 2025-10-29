# Danh Sách Tất Cả Đường Dẫn (Endpoints) Của Hệ Thống

## 📋 Mục Lục
- [Endpoints Chung (Public)](#endpoints-chung-public)
- [Endpoints Người Dùng (User)](#endpoints-người-dùng-user)
- [Endpoints Nhân Viên (Staff)](#endpoints-nhân-viên-staff)
- [Endpoints Quản Trị (Admin)](#endpoints-quản-trị-admin)

---

## 🌐 Endpoints Chung (Public)

### HomeController (`/`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/` | Trang chủ - hiển thị 10 dịch vụ và 10 sản phẩm đầu tiên |         done

### AuthController (`/auth/`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/auth/staff/login` | Trang đăng nhập cho nhân viên |                   done
| GET | `/auth/staff/landing` | Trang landing cho nhân viên |                   done
| GET | `/auth/user/login` | Trang đăng nhập cho người dùng |                   done
| GET | `/auth/user/landing` | Trang landing cho người dùng |                   done
| GET | `/auth/user/register` | Trang đăng ký người dùng |                      done
| POST | `/auth/user/register/create` | Xử lý đăng ký tài khoản người dùng |    

### ServicesController (`/`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/services` | Danh sách tất cả dịch vụ (có filter theo types, categories, name) |     done      
| GET | `/services/{id}` | Chi tiết dịch vụ theo ID |                                         done

### ProductsController (`/`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/products` | Danh sách tất cả sản phẩm (có filter theo name, activeStatus) |           done
| GET | `/products/{id}` | Chi tiết sản phẩm theo ID |                                          done

### JobApplicationController (`/job`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/job/list` | Danh sách tuyển dụng (chỉ status ACTIVE) |
| GET | `/job/details/{id}` | Chi tiết bài tuyển dụng |
| POST | `/job/apply/{id}` | Nộp đơn ứng tuyển cho job posting |

---

## 👤 Endpoints Người Dùng (User)

### ProfileController
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/user/profile` | Xem thông tin profile người dùng |                    done
| GET | `/user/profile/edit` | Trang chỉnh sửa profile |                        done
| POST | `/user/profile/update` | Cập nhật thông tin profile |                  

### CartController (`/cart`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/cart` | Xem giỏ hàng |                                                              done
| POST | `/cart/add` | Thêm sản phẩm vào giỏ hàng (params: productId, amount) |               
| POST | `/cart/adjust` | Điều chỉnh số lượng sản phẩm (params: productId, amount) |
| POST | `/cart/api/update-quantity` | API cập nhật số lượng sản phẩm (trả về JSON) |

---

## 👔 Endpoints Nhân Viên (Staff)

### ProfileController
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/profile` | Xem thông tin profile nhân viên |                                        done
| GET | `/staff/profile/edit` | Trang chỉnh sửa profile nhân viên |                                 done            
| POST | `/staff/profile/update` | Cập nhật thông tin profile nhân viên |

### StaffProductController (`/staff/products`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/products/list` | Danh sách sản phẩm quản lý (filter: name, activeStatus) |      done
| GET | `/staff/products/create` | Form tạo sản phẩm mới |                                      done
| POST | `/staff/products/create` | Tạo sản phẩm mới |                                          
| GET | `/staff/products/edit/{id}` | Form chỉnh sửa sản phẩm |                                 done
| POST | `/staff/products/edit/{id}` | Cập nhật sản phẩm |

### StaffServiceController (`/staff/service`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/service/list` | Danh sách dịch vụ quản lý (filter: types, categories, name) |   done
| GET | `/staff/service/create` | Form tạo dịch vụ mới |                                        done
| POST | `/staff/service/create` | Tạo dịch vụ mới |
| GET | `/staff/service/edit/{id}` | Form chỉnh sửa dịch vụ |                                   done
| POST | `/staff/service/edit/{id}` | Cập nhật dịch vụ |

### StaffProviderController (`/staff/supplier`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/supplier/list` | Danh sách nhà cung cấp (filter: categories, name) |            done
| GET | `/staff/supplier/create` | Form tạo nhà cung cấp mới |                                  done
| POST | `/staff/supplier/create` | Tạo nhà cung cấp mới |
| GET | `/staff/supplier/edit/{id}` | Form chỉnh sửa nhà cung cấp |                             done
| POST | `/staff/supplier/edit/{id}` | Cập nhật nhà cung cấp |

### StaffPromotionController (`/staff/promotion`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/promotion/list` | Danh sách khuyến mãi |                                    done
| GET | `/staff/promotion/create` | Form tạo khuyến mãi mới |                               done
| POST | `/staff/promotion/create` | Tạo khuyến mãi mới |
| GET | `/staff/promotion/edit/{id}` | Form chỉnh sửa khuyến mãi |                          done
| POST | `/staff/promotion/edit/{id}` | Cập nhật khuyến mãi |

### StaffVoucherController (`/staff/voucher`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/voucher/list` | Danh sách voucher (filter: code, name, discountType, statusId) |            done
| GET | `/staff/voucher/create` | Form tạo voucher mới |                                                    done
| POST | `/staff/voucher/create` | Tạo voucher mới |
| GET | `/staff/voucher/edit/{id}` | Form chỉnh sửa voucher |                                               done
| POST | `/staff/voucher/edit/{id}` | Cập nhật voucher |

### StaffJobApplicationController (`/staff/job`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/staff/job/list` | Danh sách đơn ứng tuyển |                                                       done
| POST | `/staff/job/accept/{id}` | Chấp nhận đơn ứng tuyển |
| POST | `/staff/job/reject/{id}` | Từ chối đơn ứng tuyển |

---

## 🔐 Endpoints Quản Trị (Admin)

### AccountManagementController (`/admin/profiles`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/admin/profiles/` | Danh sách tất cả tài khoản (filter: username, activeStatus, staffOnly) |       
| GET | `/admin/profiles/user/edit/{username}` | Form chỉnh sửa tài khoản user |
| POST | `/admin/profiles/user/update/{username}` | Cập nhật tài khoản user |
| GET | `/admin/profiles/staff/edit/{username}` | Form chỉnh sửa tài khoản staff |
| POST | `/admin/profiles/staff/update/{username}` | Cập nhật tài khoản staff |

### JobPostingController (`/admin/job`)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/admin/job/list` | Danh sách bài tuyển dụng (filter: title, status) |
| GET | `/admin/job/create-form` | Form tạo bài tuyển dụng mới |
| POST | `/admin/job/create` | Tạo bài tuyển dụng mới |
| GET | `/admin/job/view/{id}` | Xem chi tiết bài tuyển dụng |
| POST | `/admin/job/edit/{id}` | Cập nhật bài tuyển dụng |

---

## 📊 Tổng Kết

### Phân Loại Theo Quyền Truy Cập:

**Public (Không cần đăng nhập):**
- Home, Services, Products
- Trang đăng ký, đăng nhập
- Xem danh sách job posting
- Xem chi tiết service/product

**User (Cần đăng nhập user):**
- Profile management
- Cart management
- Job applications

**Staff (Cần đăng nhập staff):**
- Profile management
- Product management
- Service management
- Supplier management
- Promotion management
- Voucher management
- Job application review

**Admin (Cần đăng nhập admin):**
- Account management
- Job posting management

### Tổng Số Endpoints: 78

- **GET endpoints:** 50
- **POST endpoints:** 28

### Filters & Query Parameters:

- **Services:** `?types=...&categories=...&name=...`
- **Products:** `?name=...&activeStatus=...`
- **Profiles (Admin):** `?username=...&activeStatus=...&staffOnly=...`
- **Job Postings:** `?title=...&status=...`
- **Vouchers:** `?code=...&name=...&discountType=...&statusId=...`


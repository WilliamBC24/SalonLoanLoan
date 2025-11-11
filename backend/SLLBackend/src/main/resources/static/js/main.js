document.addEventListener('DOMContentLoaded', () => {
  const body = document.body;
  const header = document.querySelector('.topbar');
  const backToTopBtn = document.getElementById('backToTop');

  /* Sticky header */
  const handleStickyHeader = () => {
    if (!header) return;
    if (window.scrollY > 60) {
      header.classList.add('is-sticky');
    } else {
      header.classList.remove('is-sticky');
    }
  };
  handleStickyHeader();
  window.addEventListener('scroll', handleStickyHeader, { passive: true });

  /* Back to top */
  if (backToTopBtn) {
    const toggleBackToTop = () => {
      if (window.scrollY > 400) {
        backToTopBtn.classList.add('is-visible');
      } else {
        backToTopBtn.classList.remove('is-visible');
      }
    };
    toggleBackToTop();
    window.addEventListener('scroll', toggleBackToTop, { passive: true });
    backToTopBtn.addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
  }

  /* Scroll animations */
  const animatedItems = document.querySelectorAll('[data-animate]');
  if (animatedItems.length) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            if (!entry.target.hasAttribute('data-animate-repeat')) {
              observer.unobserve(entry.target);
            }
          } else if (entry.target.hasAttribute('data-animate-repeat')) {
            entry.target.classList.remove('is-visible');
          }
        });
      },
      { threshold: 0.12 }
    );
    animatedItems.forEach((item) => observer.observe(item));
  }

  /* Slider / Carousel */
  const sliders = document.querySelectorAll('[data-slider]');
  sliders.forEach((slider) => {
    const track = slider.querySelector('.slider-track');
    const slides = Array.from(slider.querySelectorAll('.slide'));
    const prevBtn = slider.querySelector('.slider-btn.slider-prev');
    const nextBtn = slider.querySelector('.slider-btn.slider-next');
    
    if (!track || !slides.length) return;
    
    let index = 0;
    let isInitialized = false;
    let resizeTimer;

    const gapSize = () => {
      const styles = window.getComputedStyle(track);
      const gap = parseFloat(styles.gap || '0');
      return isNaN(gap) ? 0 : gap;
    };
    
    const getSlideWidth = () => {
      if (!slides.length) return 0;
      const firstSlide = slides[0];
      const rect = firstSlide.getBoundingClientRect();
      return rect.width || firstSlide.offsetWidth;
    };
    
    const getSlideWidthWithGap = () => {
      return getSlideWidth() + gapSize();
    };

    const visibleSlides = () => {
      if (!slides.length) return 1;
      const trackRect = track.getBoundingClientRect();
      const trackWidth = trackRect.width || track.offsetWidth;
      if (trackWidth <= 0) return 1;
      
      const slideW = getSlideWidthWithGap();
      if (slideW <= 0) return 1;
      
      const visible = Math.floor(trackWidth / slideW);
      return Math.max(1, Math.min(visible, slides.length));
    };

    const updateButtons = () => {
      if (!prevBtn || !nextBtn || !slides.length) return;
      
      const visible = visibleSlides();
      const maxIndex = Math.max(0, slides.length - visible);
      
      if (slides.length <= visible) {
        prevBtn.disabled = true;
        nextBtn.disabled = true;
        return;
      }
      
      prevBtn.disabled = index <= 0;
      nextBtn.disabled = index >= maxIndex;
    };

    const goToSlide = (targetIndex, smooth = true) => {
      if (!track || !slides.length) return;
      
      const visible = visibleSlides();
      const maxIndex = Math.max(0, slides.length - visible);
      index = Math.max(0, Math.min(targetIndex, maxIndex));
      
      const slideW = getSlideWidthWithGap();
      const offset = slideW * index;
      
      if (!smooth) {
        track.style.transition = 'none';
      } else {
        track.style.transition = '';
      }
      
      track.style.transform = `translateX(-${offset}px)`;
      if (prevBtn || nextBtn) {
        updateButtons();
      }
    };

    const initSlider = () => {
      if (isInitialized) return;
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          goToSlide(0);
          isInitialized = true;
        });
      });
    };

    if (prevBtn) {
      prevBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (!prevBtn.disabled) {
          goToSlide(index - 1);
        }
      });
    }

    if (nextBtn) {
      nextBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (!nextBtn.disabled) {
          goToSlide(index + 1);
        }
      });
    }

    const handleResize = () => {
      clearTimeout(resizeTimer);
      resizeTimer = setTimeout(() => {
        if (isInitialized) {
          goToSlide(index);
        }
      }, 150);
    };

    window.addEventListener('resize', handleResize, { passive: true });

    // Drag/Swipe functionality
    let isDragging = false;
    let startX = 0;
    let currentX = 0;
    let startTransform = 0;
    let currentTransform = 0;
    let lastX = 0;
    let lastTime = 0;
    let velocity = 0;
    let dragThreshold = 30; // Minimum distance to trigger slide change
    let velocityThreshold = 0.3; // Minimum velocity to trigger slide change

    const getEventX = (e) => {
      return e.type.includes('touch') ? e.touches[0]?.clientX || e.changedTouches[0]?.clientX : e.clientX;
    };

    const handleStart = (e) => {
      if (e.target.closest('a, button')) return; // Don't drag if clicking on links/buttons
      
      isDragging = true;
      startX = getEventX(e);
      lastX = startX;
      lastTime = Date.now();
      velocity = 0;
      track.classList.add('is-dragging');
      
      // Get current transform value
      const currentTransformValue = track.style.transform;
      const match = currentTransformValue.match(/translateX\((-?\d+\.?\d*)px\)/);
      startTransform = match ? parseFloat(match[1]) : 0;
      
      e.preventDefault();
    };

    const handleMove = (e) => {
      if (!isDragging) return;
      
      currentX = getEventX(e);
      const now = Date.now();
      const timeDiff = now - lastTime;
      
      // Calculate velocity for smooth momentum
      if (timeDiff > 0) {
        const distance = currentX - lastX;
        velocity = distance / timeDiff;
        lastX = currentX;
        lastTime = now;
      }
      
      const diffX = currentX - startX;
      // Kéo từ phải qua trái (diffX < 0) → muốn next slide → transform phải âm hơn (di chuyển sang trái)
      // Kéo từ trái qua phải (diffX > 0) → muốn prev slide → transform phải ít âm hơn (di chuyển sang phải)
      // startTransform là giá trị âm, diffX < 0 thì cộng vào sẽ làm transform âm hơn
      currentTransform = startTransform + diffX;
      
      // Calculate bounds
      const slideW = getSlideWidthWithGap();
      const visible = visibleSlides();
      const maxTransform = 0;
      const minTransform = -(slides.length - visible) * slideW;
      
      // Apply transform with bounds and add resistance at edges
      let boundedTransform = Math.max(minTransform, Math.min(maxTransform, currentTransform));
      
      // Add resistance when dragging beyond bounds
      if (currentTransform > maxTransform) {
        boundedTransform = maxTransform + (currentTransform - maxTransform) * 0.3;
      } else if (currentTransform < minTransform) {
        boundedTransform = minTransform + (currentTransform - minTransform) * 0.3;
      }
      
      track.style.transform = `translateX(${boundedTransform}px)`;
    };

    const handleEnd = (e) => {
      if (!isDragging) return;
      
      const diffX = currentX - startX; // Kéo từ phải qua trái (diffX < 0) → next slide, kéo từ trái qua phải (diffX > 0) → prev slide
      const wasDragging = Math.abs(diffX) > 5; // Small threshold to detect actual drag
      
      isDragging = false;
      track.classList.remove('is-dragging');
      
      // If it was just a click (not a drag), don't interfere with links/buttons
      if (!wasDragging) {
        return;
      }
      
      const slideW = getSlideWidthWithGap();
      const visible = visibleSlides();
      const absVelocity = Math.abs(velocity);
      
      // Determine if we should change slide based on distance or velocity
      const shouldChangeSlide = Math.abs(diffX) > dragThreshold || absVelocity > velocityThreshold;
      
      if (shouldChangeSlide) {
        // Kéo từ phải qua trái (diffX < 0) → slider di chuyển qua phải → next slide (index tăng)
        // Kéo từ trái qua phải (diffX > 0) → slider di chuyển về trái → prev slide (index giảm)
        if ((diffX < 0 || velocity < 0) && index < slides.length - visible) {
          goToSlide(index + 1);
        } else if ((diffX > 0 || velocity > 0) && index > 0) {
          goToSlide(index - 1);
        } else {
          goToSlide(index);
        }
      } else {
        // Snap back to current slide
        goToSlide(index);
      }
    };

    // Mouse events
    track.addEventListener('mousedown', handleStart);
    document.addEventListener('mousemove', handleMove);
    document.addEventListener('mouseup', handleEnd);

    // Touch events
    track.addEventListener('touchstart', handleStart, { passive: false });
    track.addEventListener('touchmove', handleMove, { passive: false });
    track.addEventListener('touchend', handleEnd);

    const images = slider.querySelectorAll('img');
    if (images.length) {
      let loaded = 0;
      const totalImages = images.length;
      
      const checkAllLoaded = () => {
        if (loaded === totalImages) {
          initSlider();
        }
      };
      
      images.forEach((img) => {
        if (img.complete) {
          loaded++;
          checkAllLoaded();
        } else {
          img.addEventListener('load', checkAllLoaded, { once: true });
          img.addEventListener('error', checkAllLoaded, { once: true });
        }
      });
    } else {
      initSlider();
    }
  });

  /* Hero slider with autoplay, arrows, dots */
  const hero = document.querySelector('[data-hero-slider]');
  if (hero) {
    const slidesWrap = hero.querySelector('.hero-slides');
    const slides = Array.from(hero.querySelectorAll('.hero-slide'));
    const prev = hero.querySelector('.hero-arrow.prev');
    const next = hero.querySelector('.hero-arrow.next');
    const dotsWrap = hero.querySelector('.hero-dots');
    const autoplay = hero.getAttribute('data-autoplay') === 'true';
    const interval = parseInt(hero.getAttribute('data-interval') || '5000', 10);
    let index = 0;
    let timer;

    // create dots
    slides.forEach((_, i) => {
      const b = document.createElement('button');
      b.type = 'button';
      b.setAttribute('aria-label', `Go to slide ${i + 1}`);
      dotsWrap.appendChild(b);
    });
    const dots = Array.from(dotsWrap.querySelectorAll('button'));

    const goTo = (i) => {
      index = (i + slides.length) % slides.length;
      
      // Reset tất cả slides
      slides.forEach((s, k) => {
        const isActive = k === index;
        s.classList.toggle('is-active', isActive);
        
        // Reset animation cho slide không active
        if (!isActive) {
          const title = s.querySelector('.hero-title');
          const subtitle = s.querySelector('.hero-subtitle');
          const actions = s.querySelector('.hero-actions');
          
          if (title) {
            title.style.opacity = '0';
            title.style.transform = 'translateY(30px)';
          }
          if (subtitle) {
            subtitle.style.opacity = '0';
            subtitle.style.transform = 'translateY(20px)';
          }
          if (actions) {
            actions.style.opacity = '0';
            actions.style.transform = 'translateY(20px)';
          }
        } else {
          // Trigger animation cho slide active
          requestAnimationFrame(() => {
            const title = s.querySelector('.hero-title');
            const subtitle = s.querySelector('.hero-subtitle');
            const actions = s.querySelector('.hero-actions');
            
            if (title) {
              title.style.opacity = '';
              title.style.transform = '';
            }
            if (subtitle) {
              subtitle.style.opacity = '';
              subtitle.style.transform = '';
            }
            if (actions) {
              actions.style.opacity = '';
              actions.style.transform = '';
            }
          });
        }
      });
      
      dots.forEach((d, k) => d.classList.toggle('is-active', k === index));
    };
    const nextSlide = () => goTo(index + 1);
    const prevSlide = () => goTo(index - 1);

    prev && prev.addEventListener('click', () => { prevSlide(); resetTimer(); });
    next && next.addEventListener('click', () => { nextSlide(); resetTimer(); });
    dots.forEach((d, i) => d.addEventListener('click', () => { goTo(i); resetTimer(); }));

    const startTimer = () => {
      if (autoplay) {
        timer = setInterval(nextSlide, interval);
      }
    };
    const stopTimer = () => { if (timer) clearInterval(timer); };
    const resetTimer = () => { stopTimer(); startTimer(); };

    hero.addEventListener('mouseenter', stopTimer);
    hero.addEventListener('mouseleave', startTimer);
    goTo(0);
    startTimer();
  }

  /* Lightbox / Gallery */
  const lightboxLinks = document.querySelectorAll('[data-lightbox]');
  if (lightboxLinks.length) {
    const overlay = document.createElement('div');
    overlay.className = 'lightbox-overlay';
    overlay.innerHTML = `
      <div class="lightbox-content" role="dialog" aria-modal="true">
        <button type="button" class="lightbox-close" aria-label="Đóng">&times;</button>
        <img src="" alt="" class="lightbox-image" />
      </div>
    `;
    document.body.appendChild(overlay);

    const img = overlay.querySelector('.lightbox-image');
    const closeBtn = overlay.querySelector('.lightbox-close');

    const closeLightbox = () => overlay.classList.remove('is-visible');
    closeBtn.addEventListener('click', closeLightbox);
    overlay.addEventListener('click', (event) => {
      if (event.target === overlay) closeLightbox();
    });
    document.addEventListener('keydown', (event) => {
      if (event.key === 'Escape' && overlay.classList.contains('is-visible')) {
        closeLightbox();
      }
    });

    lightboxLinks.forEach((link) => {
      link.addEventListener('click', (event) => {
        event.preventDefault();
        if (img) {
          img.src = link.href;
          img.alt = link.getAttribute('data-title') || '';
        }
        overlay.classList.add('is-visible');
      });
    });
  }

  /* Counter with waypoint */
  const counterItems = document.querySelectorAll('[data-count]');
  if (counterItems.length) {
    const counterObserver = new IntersectionObserver(
      (entries, observer) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          const el = entry.target;
          const end = parseInt(el.dataset.count, 10);
          const suffix = el.dataset.suffix || '';
          const duration = 1400;
          const startTime = performance.now();

          const tick = (now) => {
            const progress = Math.min((now - startTime) / duration, 1);
            const value = Math.floor(progress * end);
            el.textContent = value.toLocaleString('vi-VN') + suffix;
            if (progress < 1) {
              requestAnimationFrame(tick);
            }
          };

          requestAnimationFrame(tick);
          observer.unobserve(el);
        });
      },
      { threshold: 0.4 }
    );
    counterItems.forEach((item) => counterObserver.observe(item));
  }

  /* Newsletter form validation & submit */
  const newsletterForm = document.getElementById('newsletterForm');
  const newsletterFeedback = document.getElementById('newsletterFeedback');
  if (newsletterForm) {
    newsletterForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const formData = new FormData(newsletterForm);
      const email = formData.get('email');

      if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        newsletterFeedback.textContent = 'Vui lòng nhập email hợp lệ.';
        newsletterFeedback.className = 'newsletter-feedback error';
        return;
      }

      newsletterFeedback.textContent = 'Đang gửi...';
      newsletterFeedback.className = 'newsletter-feedback pending';

      try {
        // Thay thế endpoint thật nếu backend có hỗ trợ
        await new Promise((resolve) => setTimeout(resolve, 1000));
        newsletterFeedback.textContent = 'Đăng ký nhận tin thành công! Cảm ơn bạn.';
        newsletterFeedback.className = 'newsletter-feedback success';
        newsletterForm.reset();
      } catch (err) {
        console.error(err);
        newsletterFeedback.textContent = 'Có lỗi xảy ra, vui lòng thử lại sau.';
        newsletterFeedback.className = 'newsletter-feedback error';
      }
    });
  }

  /* Accordion / Tabs (generic handler) */
  document.querySelectorAll('[data-accordion]').forEach((accordion) => {
    accordion.addEventListener('click', (event) => {
      const trigger = event.target.closest('[data-accordion-trigger]');
      if (!trigger) return;
      const panelId = trigger.getAttribute('aria-controls');
      const panel = panelId ? document.getElementById(panelId) : trigger.nextElementSibling;
      const expanded = trigger.getAttribute('aria-expanded') === 'true';
      trigger.setAttribute('aria-expanded', String(!expanded));
      if (panel) {
        panel.hidden = expanded;
      }
    });
  });

  /* Smooth scroll for anchor links */
  document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
    anchor.addEventListener('click', (event) => {
      const targetId = anchor.getAttribute('href').slice(1);
      const target = document.getElementById(targetId);
      if (target) {
        event.preventDefault();
        target.scrollIntoView({ behavior: 'smooth' });
      }
    });
  });

  /* Basic analytics hooks (custom events) */
  body.addEventListener('click', (event) => {
    const target = event.target.closest('[data-track]');
    if (!target || typeof gtag !== 'function') return;
    const category = target.dataset.trackCategory || 'UI';
    const action = target.dataset.trackAction || 'click';
    const label = target.dataset.trackLabel || target.textContent.trim();
    gtag('event', action, { event_category: category, event_label: label });
  });

  /* Sidebar Toggle */
  const sidebar = document.getElementById('sidebar');
  const sidebarToggle = document.getElementById('sidebarToggle');
  const sidebarClose = document.getElementById('sidebarClose');
  const sidebarOverlay = document.getElementById('sidebarOverlay');

  const openSidebar = () => {
    if (sidebar) sidebar.classList.add('is-open');
    if (sidebarOverlay) sidebarOverlay.classList.add('is-visible');
    document.body.style.overflow = 'hidden';
  };

  const closeSidebar = () => {
    if (sidebar) sidebar.classList.remove('is-open');
    if (sidebarOverlay) sidebarOverlay.classList.remove('is-visible');
    document.body.style.overflow = '';
  };

  if (sidebarToggle) {
    sidebarToggle.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      openSidebar();
    });
  }

  if (sidebarClose) {
    sidebarClose.addEventListener('click', closeSidebar);
  }

  if (sidebarOverlay) {
    sidebarOverlay.addEventListener('click', closeSidebar);
  }

  // Close sidebar on Escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && sidebar && sidebar.classList.contains('is-open')) {
      closeSidebar();
    }
  });

  /* Cart Badge Update */
  const updateCartBadge = async () => {
    try {
      const response = await fetch('/cart/api/count');
      if (response.ok) {
        const data = await response.json();
        const badge = document.querySelector('[data-cart-count]');
        if (badge) {
          badge.textContent = data.count || 0;
        }
      }
    } catch (error) {
      console.error('Error updating cart badge:', error);
    }
  };

  // Update cart badge on page load
  updateCartBadge();

  /* Language Switcher */
  const langToggle = document.getElementById('langToggle');
  const langDropdown = document.getElementById('langDropdown');
  const langSwitcher = document.querySelector('.language-switcher');
  const currentLang = document.getElementById('currentLang');
  const langOptions = document.querySelectorAll('.lang-option');

  // Get current language from URL or default to 'vi'
  const getCurrentLang = () => {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('lang') || 'vi';
  };

  // Update UI based on current language
  const updateLangUI = () => {
    const lang = getCurrentLang();
    currentLang.textContent = lang.toUpperCase();
    
    // Update active state
    langOptions.forEach(option => {
      if (option.dataset.lang === lang) {
        option.classList.add('is-active');
      } else {
        option.classList.remove('is-active');
      }
    });
  };

  // Switch language
  const switchLanguage = (lang) => {
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
  };

  // Toggle dropdown
  if (langToggle && langDropdown) {
    langToggle.addEventListener('click', (e) => {
      e.stopPropagation();
      langSwitcher.classList.toggle('is-open');
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', (e) => {
      if (!langSwitcher.contains(e.target)) {
        langSwitcher.classList.remove('is-open');
      }
    });

    // Handle language selection
    langOptions.forEach(option => {
      option.addEventListener('click', (e) => {
        e.preventDefault();
        const lang = option.dataset.lang;
        switchLanguage(lang);
      });
    });
  }

  // Initialize language UI
  updateLangUI();
});


-- Script to add product images to existing products
-- This script should be run after the application has created the initial products

-- Insert images for existing products (assuming they have IDs 1-30)
INSERT INTO product_image (product_id, image_path) VALUES
(1, '/img/Shampo.png'),
(2, '/img/LuxuryConditioner.png'),
(3, '/img/serum.png'),
(4, '/img/HairOilTreatment.png'),
(5, '/img/StylingGel.png'),
(6, '/img/HairSpray.png'),
(7, '/img/HairMask.png'),
(8, '/img/DryShampoo.png'),
(9, '/img/FacialCleanser.png'),
(10, '/img/VitaminCSerum.png'),
(11, '/img/HyaluronicAcidMoisturizer.png'),
(12, '/img/RetinolNightCream.png'),
(13, '/img/SPF50Sunscreen.png'),
(14, '/img/ExfoliatingScrub.png'),
(15, '/img/EyeCream.png'),
(16, '/img/FaceMaskSet.png'),
(17, '/img/GelPolishSet.png'),
(18, '/img/NailStrengthener.png'),
(19, '/img/CuticleOil.png'),
(20, '/img/TopCoat.png'),
(21, '/img/BaseCoat.png'),
(22, '/img/NailFileSet.png'),
(23, '/img/NailArtKit.png'),
(24, '/img/Foundation.png'),
(25, '/img/EyeshadowPalette.png'),
(26, '/img/Mascara.png'),
(27, '/img/LipstickSet.png'),
(28, '/img/MakeupBrushes.png'),
(29, '/img/SettingSpray.png'),
(30, '/img/MakeupRemover.png'),
ON DUPLICATE KEY UPDATE image_path = VALUES(image_path);

-- Note: This script assumes the products exist with IDs 1-30
-- If your product IDs are different, please adjust accordingly


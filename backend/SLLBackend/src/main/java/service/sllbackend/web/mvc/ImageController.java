package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import service.sllbackend.service.ImageStorageService;

import java.io.IOException;

@Controller
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    
    private final ImageStorageService imageStorageService;
    
    @GetMapping("/{category}/{filename:.+}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String category,
            @PathVariable String filename) {
        
        try {
            String imagePath = category + "/" + filename;
            byte[] imageContent = imageStorageService.getImageContent(imagePath);
            String contentType = imageStorageService.getContentType(imagePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("max-age=86400"); // Cache for 1 day
            
            return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
        } catch (SecurityException e) {
            log.warn("Security violation accessing image: {}/{}", category, filename);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IOException e) {
            log.debug("Image not found: {}/{}", category, filename);
            return ResponseEntity.notFound().build();
        }
    }
}

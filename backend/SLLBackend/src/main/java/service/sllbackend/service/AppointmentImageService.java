package service.sllbackend.service;

import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.BeforeAppointmentImage;

import java.util.List;

public interface AppointmentImageService {
    
    /**
     * Add a before-service image to an appointment.
     * Only allowed when appointment status is COMPLETED.
     * 
     * @param appointmentId the appointment ID
     * @param imagePath the path/URL of the image
     * @return the created BeforeAppointmentImage
     * @throws IllegalStateException if appointment status is not COMPLETED
     * @throws IllegalArgumentException if appointment not found
     */
    BeforeAppointmentImage addBeforeImage(Integer appointmentId, String imagePath);
    
    /**
     * Add an after-service image to an appointment.
     * Only allowed when appointment status is COMPLETED.
     * 
     * @param appointmentId the appointment ID
     * @param imagePath the path/URL of the image
     * @return the created AfterAppointmentImage
     * @throws IllegalStateException if appointment status is not COMPLETED
     * @throws IllegalArgumentException if appointment not found
     */
    AfterAppointmentImage addAfterImage(Integer appointmentId, String imagePath);
    
    /**
     * Get all before-service images for an appointment.
     * 
     * @param appointmentId the appointment ID
     * @return list of BeforeAppointmentImage
     */
    List<BeforeAppointmentImage> getBeforeImages(Integer appointmentId);
    
    /**
     * Get all after-service images for an appointment.
     * 
     * @param appointmentId the appointment ID
     * @return list of AfterAppointmentImage
     */
    List<AfterAppointmentImage> getAfterImages(Integer appointmentId);
    
    /**
     * Delete a before-service image by ID.
     * 
     * @param imageId the image ID
     */
    void deleteBeforeImage(Integer imageId);
    
    /**
     * Delete an after-service image by ID.
     * 
     * @param imageId the image ID
     */
    void deleteAfterImage(Integer imageId);
}

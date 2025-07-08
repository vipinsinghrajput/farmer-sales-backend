package com.farmerapp.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class ImageUploadService {


//    @Autowired
//    private Cloudinary cloudinary;
//
//    public String uploadImage(MultipartFile file) throws IOException {
//        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
//        return (String) uploadResult.get("secure_url"); // return image URL
//    }
	
	
	  @Autowired
	    private Cloudinary cloudinary; // Configure Cloudinary Bean

	    public String uploadImage(MultipartFile file) {
	        try {
	            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
	            return uploadResult.get("secure_url").toString();
	        } catch (IOException e) {
	            throw new RuntimeException("Image upload failed", e);
	        }
	    }
}

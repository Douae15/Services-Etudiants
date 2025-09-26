package com.ensa.servicesetudiants.service;

import com.ensa.servicesetudiants.entity.Admin;
import com.ensa.servicesetudiants.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(Integer id) {
        return adminRepository.findById(id);
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void deleteAdmin(Integer id) {
        adminRepository.deleteById(id);
    }

    public boolean login(String email, String mdp) {
        return adminRepository.findByEmail(email)
                .map(admin -> admin.getPassword().equals(mdp)) 
                .orElse(false);
    }
    
}

package com.alibou.security.repositories;

import com.alibou.security.models.Otp;
import com.alibou.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpRepository extends JpaRepository<Otp,Long> {

    List<Otp> findByUser(User existingUser);

}

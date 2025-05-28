package com.example.project_module6.controller;



import com.example.project_module6.model.RatingPoints;
import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IUserRepository;
import com.example.project_module6.service.IRatingPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.OpenOption;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rating-points")
@CrossOrigin(value = "*")
public class RatingPointsController {
    @Autowired
    private IRatingPointsService ratingPointsService;
    @Autowired
    private IUserRepository userRepository;
    @GetMapping("")
    public ResponseEntity<?> getRatingPoints(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null){
            return new ResponseEntity<>("Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails =(UserDetails) authentication.getPrincipal();
        Optional<Users> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()){
            return new ResponseEntity<>("User này không tồn tại", HttpStatus.BAD_REQUEST);
        }
        Users users = ratingPointsService.getAccumulatedPoints(user.get().getId());
        return new ResponseEntity<>(users,HttpStatus.OK);
    }
    @GetMapping("/get-all")
    public ResponseEntity<List<?>> getAllRatingPoints(){
        List<RatingPoints> ratingPointsList = ratingPointsService.getAllRatingPoints();
        if (ratingPointsList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ratingPointsList,HttpStatus.OK);
    }
}

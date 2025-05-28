package com.example.project_module6.repository;

import com.example.project_module6.model.RatingPoints;
import com.example.project_module6.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRatingPointsRepository extends JpaRepository<RatingPoints,Integer> {
        @Query(value = """

                select u.* from rating_points rp join users u on u.id=rp.user_id where rp.user_id =?1
                       """,nativeQuery = true)
        Users getAccumulatedPoints(int idUser);

    @Query(value = """
                  select* from rating_points rp order by rp.accumulated_points desc 
                   """,nativeQuery = true)
    List<RatingPoints> getAllRatingPoints();
}

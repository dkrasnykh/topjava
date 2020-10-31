package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("UPDATE Meal m SET m.dateTime = :datetime, m.calories= :calories, m.description=:desc where m.id=:id and m.user.id=:userId")
    int save(@Param("id") Integer id, @Param("userId") int userId, @Param("datetime") LocalDateTime datetime, @Param("calories") int calories, @Param("desc") String desc);

    Meal getMealByIdAndUser(int id, User user);

    List<Meal> findAllByUserOrderByDateTimeDesc(User user);

    List<Meal> findAllByUserAndDateTimeGreaterThanEqualAndDateTimeBeforeOrderByDateTimeDesc(User user, LocalDateTime startDateTime, LocalDateTime endDateTime);
}

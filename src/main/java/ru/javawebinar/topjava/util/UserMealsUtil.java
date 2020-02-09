package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsFromStream = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFromStream.forEach(System.out::println);

        System.out.println("\n");
        List<UserMealWithExcess> mealsFromCycles = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsFromCycles.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate,Integer> dateCaloriesMap = new HashMap<>();
        List<UserMealWithExcess> withExcessList = new ArrayList<>();

        meals.forEach(userMeal -> {
            if(dateCaloriesMap.containsKey(userMeal.getDateTime().toLocalDate())){
                dateCaloriesMap.put(userMeal.getDateTime().toLocalDate(),
                        userMeal.getCalories()+dateCaloriesMap.get(userMeal.getDateTime().toLocalDate()));
            }else {
                dateCaloriesMap.put(userMeal.getDateTime().toLocalDate(),userMeal.getCalories());
            }

        });
        meals.forEach(meal->{
            if(TimeUtil.isBetweenInclusive(meal.getDateTime().toLocalTime(),startTime,endTime)){
                withExcessList.add(new UserMealWithExcess(meal.getDateTime(),meal.getDescription(),meal.getCalories(),
                        dateCaloriesMap.get(meal.getDateTime().toLocalDate())>caloriesPerDay));
            }
        });

        return withExcessList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals,
                                                             LocalTime startTime,
                                                             LocalTime endTime,
                                                             int caloriesPerDay) {
        Map<LocalDate,Integer> calPerDateMap = meals.stream().collect(Collectors.groupingBy(meal->meal.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(meal1->TimeUtil.isBetweenInclusive(meal1.getDateTime().toLocalTime(),startTime,endTime))
                .map(meal->{
                    boolean isExceeded = calPerDateMap.get(meal.getDateTime().toLocalDate())>caloriesPerDay;

             return new UserMealWithExcess(meal.getDateTime(),meal.getDescription(),meal.getCalories(),isExceeded);
         }).collect(Collectors.toList());
    }
}

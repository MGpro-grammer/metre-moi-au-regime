package be.esi.prj.dto;

import be.esi.prj.enumeration.ActivityLevel;
import be.esi.prj.enumeration.Gender;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a user profile.
 * Immutable record class used for transferring user data between application layers.
 * Contains comprehensive information about a user including personal details,
 * physical characteristics, weight management goals, and activity level.
 *
 * @param userId        Unique identifier for the user
 * @param email         User's email address for authentication and communication
 * @param password      User's password for authentication
 * @param name          User's full name
 * @param age           User's age in years
 * @param gender        User's gender (from Gender enumeration)
 * @param height        User's height (typically in centimeters)
 * @param weight        User's current weight (typically in kilograms)
 * @param goalWeight    User's target weight for their fitness program
 * @param startDate     Start date of the user's fitness or nutrition program
 * @param endDate       Target end date for the user's fitness or nutrition program
 * @param activityLevel User's general physical activity level (from ActivityLevel enumeration)
 */

public record UserDto(
    Integer userId,
    String email,
    String password,
    String name,
    Integer age,
    Gender gender,
    Double height,
    Double weight,
    Double goalWeight,
    LocalDate startDate,
    LocalDate endDate,
    ActivityLevel activityLevel
) {}
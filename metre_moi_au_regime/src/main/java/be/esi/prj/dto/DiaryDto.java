package be.esi.prj.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a daily diary entry.
 * Immutable record class used for transferring diary data between application layers.
 * Contains essential information about a user's daily record including identification,
 * associated user, date of entry, and recorded weight for that day.
 *
 * @param diaryId        Unique identifier for the diary entry
 * @param userId         Identifier of the user who owns this diary entry
 * @param date           Date of the diary entry
 * @param recordedWeight Weight recorded by the user for this diary entry
 */
public record DiaryDto(
    Integer diaryId,
    Integer userId,
    LocalDate date,
    Double recordedWeight
) {}
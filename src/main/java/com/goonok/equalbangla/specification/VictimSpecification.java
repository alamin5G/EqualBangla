package com.goonok.equalbangla.specification;


import com.goonok.equalbangla.model.Victim;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.List;

public class VictimSpecification {

    public static Specification<Victim> filterByCriteria(
            String fullName, List<String> incidentType, LocalDate startDate, LocalDate endDate,
            List<String> district, String policeStation, Integer ageFrom, Integer ageTo,
            List<String> gender, String occupation, String verificationStatus) {

        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Name filtering (using LIKE for partial match)
            if (fullName != null && !fullName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("fullName"), "%" + fullName + "%"));
            }

            // Multiple incident types (IN query)
            if (incidentType != null && !incidentType.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("incidentType").in(incidentType));
            }

            if (startDate != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("incidentDate"), startDate));
            }

            if (endDate != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("incidentDate"), endDate));
            }

            // Multiple districts (IN query)
            if (district != null && !district.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("district").in(district));
            }

            if (policeStation != null && !policeStation.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("policeStation"), policeStation));
            }

            if (ageFrom != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("age"), ageFrom));
            }

            if (ageTo != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("age"), ageTo));
            }

            // Multiple genders (IN query)
            if (gender != null && !gender.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("gender").in(gender));
            }

            if (occupation != null && !occupation.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("occupation"), occupation));
            }

            if (verificationStatus != null && !verificationStatus.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("verificationStatus"), verificationStatus));
            }

            return predicate;
        };
    }

    public static Specification<Victim> hasIncidentType(String incidentType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("incidentType"), incidentType);
    }

    public static Specification<Victim> incidentAfter(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("incidentDate"), startDate);
    }

    public static Specification<Victim> incidentBefore(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("incidentDate"), endDate);
    }

    // You can add more filtering specifications as needed, like filtering by location, gender, etc.

}

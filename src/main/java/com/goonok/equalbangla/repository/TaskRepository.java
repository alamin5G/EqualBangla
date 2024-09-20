package com.goonok.equalbangla.repository;

import com.goonok.equalbangla.model.Admin;
import com.goonok.equalbangla.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedAdmin(Admin admin);
}

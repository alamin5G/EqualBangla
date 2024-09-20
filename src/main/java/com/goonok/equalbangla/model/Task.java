package com.goonok.equalbangla.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Task extends LogEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "victim_id")
    private Victim victim; // The case the task is related to

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_admin_id")
    private Admin assignedAdmin; // The admin responsible for this task

    private String description;  // Task description

    @Column(name = "task_status")
    private String taskStatus = "PENDING";  // PENDING,  COMPLETED
}


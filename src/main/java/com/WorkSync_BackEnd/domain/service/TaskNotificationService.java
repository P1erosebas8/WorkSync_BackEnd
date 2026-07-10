package com.WorkSync_BackEnd.domain.service;
import com.WorkSync_BackEnd.domain.model.Task;
import com.WorkSync_BackEnd.domain.model.User;
import com.WorkSync_BackEnd.domain.repository.TaskRepository;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class TaskNotificationService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Ejecuta todos los dias a las 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void notifyUpcomingDeadlines() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        // We need all tasks due tomorrow that are not completed.
        // Assuming we have to fetch all and filter for now, to keep it simple.
        // In a real scenario, use a specific repository method.
        // For now, let's just log or skip if getByProject is the only one available.
    }

    public void notifyReassignment(Task task) {
        if (task.getAssigneeId() != null) {
            Optional<User> user = userRepository.getById(task.getAssigneeId());
            user.ifPresent(u -> {
                String subject = "WorkSync - Nueva Tarea Asignada: " + task.getTitle();
                String body = "<h3>Hola " + u.getName() + "</h3>" +
                              "<p>Se te ha asignado una nueva tarea en la plataforma:</p>" +
                              "<ul><li><b>Tarea:</b> " + task.getTitle() + "</li>" +
                              "<li><b>Prioridad:</b> " + task.getPriority() + "</li>" +
                              "<li><b>Vencimiento:</b> " + (task.getDueDate() != null ? task.getDueDate().toString() : "Sin fecha") + "</li></ul>" +
                              "<p>Por favor, revisa el panel para más detalles.</p>";
                emailService.sendEmail(u.getEmail(), subject, body);
            });
        }
    }

    public void notifyStatusChange(Task task, String oldStatus) {
        // Send email if blocked or rejected
        if (task.getStatus() == EstadoTarea.BLOQUEADO || task.getStatus() == EstadoTarea.EN_REVISION) {
            if (task.getAssigneeId() != null) {
                userRepository.getById(task.getAssigneeId()).ifPresent(u -> {
                    String subject = "WorkSync - Atención requerida en tarea: " + task.getTitle();
                    String body = "<h3>Hola " + u.getName() + "</h3>" +
                                  "<p>La tarea <b>" + task.getTitle() + "</b> ha cambiado a estado <strong style='color:red'>" + task.getStatus() + "</strong>.</p>" +
                                  "<p>Es posible que requiera tu atención o la del administrador.</p>";
                    emailService.sendEmail(u.getEmail(), subject, body);
                });
            }
        }
    }
}

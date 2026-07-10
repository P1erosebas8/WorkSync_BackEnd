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
                String body = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #333; border-radius: 8px; overflow: hidden; background-color: #1a1a1a; color: #fff;\">"
                        +
                        "<div style=\"background-color: #dc2626; padding: 20px; text-align: center;\">" +
                        "<h2 style=\"color: #fff; margin: 0;\">Nueva Tarea Asignada</h2>" +
                        "</div>" +
                        "<div style=\"padding: 30px; background-color: #242424;\">" +
                        "<h3 style=\"color: #fff;\">Hola " + u.getName() + " 👋</h3>" +
                        "<p style=\"color: #ccc; line-height: 1.5;\">Se te ha asignado una nueva tarea en la plataforma WorkSync. Aquí tienes los detalles:</p>"
                        +
                        "<div style=\"background-color: #1a1a1a; padding: 15px; border-radius: 6px; border: 1px solid #333; margin: 20px 0;\">"
                        +
                        "<h4 style=\"margin-top: 0; color: #dc2626;\">" + task.getTitle() + "</h4>" +
                        "<p style=\"color: #aaa; font-size: 14px;\"><b>Prioridad:</b> " + task.getPriority() + "<br>" +
                        "<b>Vencimiento:</b> "
                        + (task.getDueDate() != null ? task.getDueDate().toString() : "Sin fecha") + "</p>" +
                        "</div>" +
                        "<p style=\"color: #ccc; font-size: 13px;\">Por favor, ingresa a la plataforma para comenzar a trabajar en ella.</p>"
                        +
                        "<a href=\"https://work-sync-front-end.vercel.app\" style=\"display: inline-block; padding: 10px 20px; background-color: #dc2626; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 10px;\">Ir a WorkSync</a>"
                        +
                        "</div>" +
                        "<div style=\"background-color: #1a1a1a; padding: 15px; text-align: center; border-top: 1px solid #333; color: #666; font-size: 12px;\">"
                        +
                        "&copy; " + LocalDate.now().getYear() + " WorkSync Platform. Todos los derechos reservados." +
                        "</div>" +
                        "</div>";
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
                    String body = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #333; border-radius: 8px; overflow: hidden; background-color: #1a1a1a; color: #fff;\">"
                            +
                            "<div style=\"background-color: #f59e0b; padding: 20px; text-align: center;\">" +
                            "<h2 style=\"color: #fff; margin: 0;\">Atención Requerida</h2>" +
                            "</div>" +
                            "<div style=\"padding: 30px; background-color: #242424;\">" +
                            "<h3 style=\"color: #fff;\">Hola " + u.getName() + "</h3>" +
                            "<p style=\"color: #ccc; line-height: 1.5;\">La tarea <b>" + task.getTitle()
                            + "</b> ha cambiado a un estado que requiere revisión.</p>" +
                            "<div style=\"background-color: #1a1a1a; padding: 15px; border-radius: 6px; border: 1px solid #333; margin: 20px 0;\">"
                            +
                            "<p style=\"color: #aaa; margin: 0;\">Estado Actual: <strong style=\"color: #f59e0b; font-size: 16px;\">"
                            + task.getStatus() + "</strong></p>" +
                            "</div>" +
                            "<p style=\"color: #ccc; font-size: 13px;\">Por favor, revisa los detalles o comunícate con tu administrador.</p>"
                            +
                            "<a href=\"https://worksync-frontend.vercel.app\" style=\"display: inline-block; padding: 10px 20px; background-color: #f59e0b; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 10px;\">Revisar Tarea</a>"
                            +
                            "</div>" +
                            "<div style=\"background-color: #1a1a1a; padding: 15px; text-align: center; border-top: 1px solid #333; color: #666; font-size: 12px;\">"
                            +
                            "&copy; " + LocalDate.now().getYear() + " WorkSync Platform. Todos los derechos reservados."
                            +
                            "</div>" +
                            "</div>";
                    emailService.sendEmail(u.getEmail(), subject, body);
                });
            }
        }
    }
}

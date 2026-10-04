package ru.itmo.vehiclelab.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.itmo.vehiclelab.service.VehicleNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(VehicleNotFoundException exception, HttpServletRequest request, Model model) {
        return error(model, 404, "Объект не найден", exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(Exception exception, HttpServletRequest request, Model model) {
        String message = exception instanceof IllegalArgumentException
                ? exception.getMessage() : "Проверьте обязательные поля, типы и числовые значения.";
        return error(model, 400, "Некорректный запрос", message, request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String dataConflict(DataIntegrityViolationException exception, HttpServletRequest request, Model model) {
        return error(model, 409, "Данные не сохранены",
                "База данных отклонила значения. Проверьте обязательные поля и ограничения.",
                request.getRequestURI());
    }

    private String error(Model model, int status, String title, String message, String path) {
        model.addAttribute("status", status);
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("path", path);
        return "error";
    }
}

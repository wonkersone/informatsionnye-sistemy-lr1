package ru.itmo.vehiclelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VehicleLab1Application {

	public static void main(String[] args) {
        var log = org.slf4j.LoggerFactory.getLogger("lab");
        try {
            SpringApplication.run(VehicleLab1Application.class, args);
        } catch (RuntimeException ex) {
            log.error("{} Подробности: logs/application.log", startupError(ex), ex);
            System.exit(1);
        }
	}

    private static String startupError(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof org.springframework.boot.web.server.PortInUseException port) {
                return "Порт " + port.getPort() + " занят. Остановите предыдущий запуск приложения.";
            }
            if (cause instanceof java.sql.SQLException sql && sql.getSQLState() != null) {
                if (sql.getSQLState().startsWith("28")) return "База данных отклонила логин или пароль.";
                if (sql.getSQLState().startsWith("08")) return "Нет подключения к базе данных. Проверьте PostgreSQL и адрес подключения.";
            }
        }
        return "Не удалось запустить приложение. Проверьте настройки базы данных и конфигурацию.";
    }

}

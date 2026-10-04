package ru.itmo.vehiclelab.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;
import ru.itmo.vehiclelab.service.SpecialOperationsService;

import java.util.Optional;

@Controller
@RequestMapping("/operations")
public class SpecialOperationsController {

    private final SpecialOperationsService service;

    public SpecialOperationsController(SpecialOperationsService service) {
        this.service = service;
    }

    @ModelAttribute("vehicleTypes")
    public VehicleType[] vehicleTypes() { return VehicleType.values(); }

    @ModelAttribute("fuelTypes")
    public FuelType[] fuelTypes() { return FuelType.values(); }

    @GetMapping
    public String operations() {
        return "operations/index";
    }

    @PostMapping("/delete-by-fuel")
    public String deleteByFuel(@RequestParam FuelType fuelType, RedirectAttributes redirectAttributes) {
        long deleted = service.deleteAllByFuelType(fuelType);
        redirectAttributes.addFlashAttribute("success", "Удалено объектов: " + deleted + ".");
        return "redirect:/operations";
    }

    @PostMapping("/delete-one-by-consumption")
    public String deleteOneByConsumption(@RequestParam Double fuelConsumption,
                                         RedirectAttributes redirectAttributes) {
        requireFinite(fuelConsumption, "Расход топлива");
        Optional<Vehicle> deleted = service.deleteOneByFuelConsumption(fuelConsumption);
        redirectAttributes.addFlashAttribute(deleted.isPresent() ? "success" : "warning",
                deleted.map(vehicle -> "Удалён объект #" + vehicle.getId() + ".")
                        .orElse("Объект с таким расходом топлива не найден."));
        return "redirect:/operations";
    }

    @GetMapping("/count")
    public String count(@RequestParam Double fuelConsumption, Model model) {
        requireFinite(fuelConsumption, "Расход топлива");
        model.addAttribute("countResult", service.countByFuelConsumptionGreaterThan(fuelConsumption));
        model.addAttribute("countThreshold", fuelConsumption);
        return "operations/index";
    }

    @GetMapping("/by-type")
    public String byType(@RequestParam VehicleType type, Model model) {
        model.addAttribute("resultVehicles", service.findByType(type));
        model.addAttribute("resultTitle", "Транспортные средства типа «" + type.getDisplayName() + "»");
        return "operations/index";
    }

    @GetMapping("/by-power")
    public String byPower(@RequestParam Integer min, @RequestParam Integer max, Model model) {
        model.addAttribute("resultVehicles", service.findByEnginePowerRange(min, max));
        model.addAttribute("resultTitle", "Мощность двигателя от " + min + " до " + max);
        return "operations/index";
    }

    private void requireFinite(Double value, String fieldName) {
        if (value == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " должен быть конечным числом");
        }
    }
}

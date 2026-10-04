package ru.itmo.vehiclelab.web;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;
import ru.itmo.vehiclelab.service.VehicleService;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;
    private final ru.itmo.vehiclelab.service.CoordinatesService coordinatesService;

    public VehicleController(VehicleService service, ru.itmo.vehiclelab.service.CoordinatesService coordinatesService) {
        this.service = service;
        this.coordinatesService = coordinatesService;
    }

    @ModelAttribute("dialog")
    public boolean dialog(@RequestParam(defaultValue = "false") boolean dialog) { return dialog; }

    @ModelAttribute("availableCoordinates")
    public java.util.List<ru.itmo.vehiclelab.domain.Coordinates> availableCoordinates() { return coordinatesService.all(); }

    @ModelAttribute("vehicleTypes")
    public VehicleType[] vehicleTypes() {
        return VehicleType.values();
    }

    @ModelAttribute("fuelTypes")
    public FuelType[] fuelTypes() {
        return FuelType.values();
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sort,
                       @RequestParam(defaultValue = "ASC") String direction,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) VehicleType type,
                       @RequestParam(required = false) FuelType fuelType,
                       Model model) {
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<Vehicle> vehicles = service.findPage(name, type, fuelType, page, size, sort, sortDirection);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("name", name == null ? "" : name);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedFuelType", fuelType);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", sortDirection.name());
        return "vehicles/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Integer id, Model model) {
        model.addAttribute("vehicle", service.get(id));
        return "vehicles/details";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vehicleForm", new VehicleForm());
        prepareForm(model, "Новое транспортное средство", "/vehicles", "Создать");
        return "vehicles/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vehicleForm") VehicleForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareForm(model, "Новое транспортное средство", "/vehicles", "Создать");
            return "vehicles/form";
        }
        Vehicle created = service.create(form);
        redirectAttributes.addFlashAttribute("success", "Объект создан. Присвоен ID " + created.getId() + ".");
        return "redirect:/vehicles/" + created.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("vehicleForm", service.toForm(id));
        model.addAttribute("vehicleId", id);
        prepareForm(model, "Изменение объекта #" + id, "/vehicles/" + id, "Сохранить изменения");
        return "vehicles/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("vehicleForm") VehicleForm form,
                         BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleId", id);
            prepareForm(model, "Изменение объекта #" + id, "/vehicles/" + id, "Сохранить изменения");
            return "vehicles/form";
        }
        service.update(id, form);
        redirectAttributes.addFlashAttribute("success", "Изменения сохранены.");
        return "redirect:/vehicles/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("success", "Объект #" + id + " удалён.");
        return "redirect:/vehicles";
    }

    private void prepareForm(Model model, String title, String action, String submitLabel) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute("submitLabel", submitLabel);
    }
}

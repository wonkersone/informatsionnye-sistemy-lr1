package ru.itmo.vehiclelab.web;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmo.vehiclelab.service.CoordinatesService;
@Controller
@RequestMapping("/coordinates")
public class CoordinatesController {
    private final CoordinatesService service;
    public CoordinatesController(CoordinatesService service) { this.service=service; }
    @GetMapping
    public String list(Model model) {
        var coordinates = service.all();
        model.addAttribute("coordinates",coordinates);
        model.addAttribute("linkedCounts",coordinates.stream().collect(java.util.stream.Collectors.toMap(c->c.getId(),c->service.linkedCount(c.getId()))));
        return "coordinates";
    }
    @PostMapping
    public String create(@RequestParam Double x, @RequestParam Float y, RedirectAttributes flash) {
        service.create(x,y); flash.addFlashAttribute("success","Координаты созданы"); return "redirect:/coordinates";
    }
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,@RequestParam Double x,@RequestParam Float y,RedirectAttributes flash) {
        service.update(id,x,y); flash.addFlashAttribute("success","Координаты обновлены"); return "redirect:/coordinates";
    }
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,@RequestParam(required=false) Integer replacementId,RedirectAttributes flash) {
        service.delete(id,replacementId); flash.addFlashAttribute("success","Координаты удалены"); return "redirect:/coordinates";
    }
}

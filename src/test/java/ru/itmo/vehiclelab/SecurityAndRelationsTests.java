package ru.itmo.vehiclelab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.vehiclelab.domain.*;
import ru.itmo.vehiclelab.repository.*;
import ru.itmo.vehiclelab.service.*;
import ru.itmo.vehiclelab.web.VehicleForm;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAndRelationsTests {
    @Autowired MockMvc mvc;
    @Autowired CoordinatesService coordinates;
    @Autowired VehicleService vehicles;
    @Autowired VehicleRepository vehicleRepository;
    @Autowired CoordinatesRepository coordinateRepository;

    @BeforeEach
    void clean() { vehicleRepository.deleteAll(); coordinateRepository.deleteAll(); }

    @Test
    void requiresAuthenticationAndCsrf() throws Exception {
        mvc.perform(get("/vehicles")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/coordinates").with(user("student")).param("x","1").param("y","2")).andExpect(status().isForbidden());
        mvc.perform(get("/vehicles").with(user("student"))).andExpect(status().isOk());
        mvc.perform(get("/coordinates").with(user("teacher"))).andExpect(status().isOk());
    }

    @Test
    void acceptsLoginAndRejectsBadPassword() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("username","student").param("password","student123"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/vehicles"));
        mvc.perform(post("/login").with(csrf()).param("username","student").param("password","wrong"))
            .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void sharesCoordinatesAndReassignsLinksBeforeDelete() {
        Coordinates original = coordinates.create(10.0, 20.0f);
        Coordinates replacement = coordinates.create(30.0, 40.0f);
        Vehicle first = vehicles.create(form(original.getId()));
        Vehicle second = vehicles.create(form(original.getId()));
        assertThat(vehicles.get(first.getId()).getCoordinates().getId()).isEqualTo(original.getId());
        assertThatThrownBy(() -> coordinates.delete(original.getId(), null)).isInstanceOf(IllegalArgumentException.class);
        assertThat(coordinateRepository.existsById(original.getId())).isTrue();
        coordinates.update(original.getId(), 12.0, 24.0f);
        assertThat(vehicles.get(second.getId()).getCoordinates().getX()).isEqualTo(12.0);
        coordinates.delete(original.getId(), replacement.getId());
        assertThat(coordinateRepository.existsById(original.getId())).isFalse();
        assertThat(vehicles.get(first.getId()).getCoordinates().getId()).isEqualTo(replacement.getId());
        assertThat(vehicles.get(second.getId()).getCoordinates().getId()).isEqualTo(replacement.getId());
    }

    @Test
    void validatesInputOnServer() throws Exception {
        mvc.perform(post("/vehicles").with(user("student")).with(csrf())
            .param("name","").param("coordinateX","10").param("coordinateY","409")
            .param("type","BOAT").param("enginePower","0").param("numberOfWheels","2")
            .param("capacity","1").param("distanceTravelled","1").param("fuelConsumption","1").param("fuelType","DIESEL"))
            .andExpect(status().isOk()).andExpect(view().name("vehicles/form"))
            .andExpect(model().attributeHasFieldErrors("vehicleForm","name","coordinateY","enginePower"));
        assertThat(vehicleRepository.count()).isZero();
        assertThatThrownBy(() -> coordinates.create(Double.NaN, 10f)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> coordinates.create(1.0, 409f)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void filtersExactlyAndPaginates() {
        Coordinates point = coordinates.create(1.0,2f);
        for(int i=0;i<12;i++) vehicles.create(form(point.getId()));
        assertThat(vehicles.findPage("Лод",null,null,0,5,"name",org.springframework.data.domain.Sort.Direction.ASC).getTotalElements()).isZero();
        var page = vehicles.findPage("Лодка",null,null,1,5,"name",org.springframework.data.domain.Sort.Direction.ASC);
        assertThat(page.getTotalElements()).isEqualTo(12);
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    private VehicleForm form(Integer pointId) {
        VehicleForm form = new VehicleForm();
        form.setName("Лодка"); form.setCoordinatesId(pointId); form.setType(VehicleType.BOAT);
        form.setEnginePower(10); form.setNumberOfWheels(1); form.setCapacity(2.0);
        form.setDistanceTravelled(1); form.setFuelConsumption(3.0); form.setFuelType(FuelType.MANPOWER);
        return form;
    }
}

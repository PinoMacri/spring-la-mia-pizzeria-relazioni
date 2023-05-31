package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
public class PizzaController {
	@Autowired
	private PizzaService pizzaService;

	@GetMapping("/")
	public String home(Model model) {
		return "home";
	}

	@GetMapping("/pizze")
	public String index(Model model) {
		Optional<List<Pizza>> optionalPizze = Optional.of(pizzaService.findAll());

		if (optionalPizze.isPresent()) {
			model.addAttribute("pizze", optionalPizze.get());
		} else {
			model.addAttribute("message", "Non ci sono pizze");
		}

		return "index";
	}

	@GetMapping("/pizze/{id}")
	public String getPizza(Model model, @PathVariable("id") int id) {
		Pizza pizza = getPizzaById(id);
		model.addAttribute("pizza", pizza);
		return "show";
	}

	private Pizza getPizzaById(int id) {
		List<Pizza> pizze = pizzaService.findAll();
		Pizza singolaPizza = null;
		for (Pizza pizza : pizze)
			if (pizza.getId() == id)
				singolaPizza = pizza;
		return singolaPizza;
	}

	@PostMapping("/pizze/nome")
	public String getPizzaNome(@RequestParam(required = false) String nome, Model model) {
		List<Pizza> pizze = pizzaService.findByNome(nome);
		model.addAttribute("pizze", pizze);
		model.addAttribute("nome", nome);
		return "index";
	}

	
	
	
	
	
	@GetMapping("pizze/create")
	public String create(Model model) {
		model.addAttribute("pizza", new Pizza());
		return "create";
	}
	@PostMapping("/pizze/store")
	public String store(Model model, @Valid @ModelAttribute Pizza pizza, BindingResult bindingResult ) {
		if(bindingResult.hasErrors()) {
			for (ObjectError err : bindingResult.getAllErrors())
				System.err.println("errore: " + err.getDefaultMessage());
			model.addAttribute("pizza", pizza);
			model.addAttribute("errors", bindingResult);
			return "create";
		}
		pizzaService.save(pizza);
		return "redirect:/pizze";
	}
	
	
	
	@GetMapping("/pizze/edit/{id}")
	public String edit(Model model, @PathVariable int id) {
		Optional<Pizza> pizzaOpt = pizzaService.findById(id);
		Pizza pizza = pizzaOpt.get();
		model.addAttribute("pizza", pizza);
		return "edit";
	}

	@PostMapping("/pizze/update/{id}")
	public String update(
	        Model model,
	        @PathVariable int id,
	        @Valid @ModelAttribute("pizza") Pizza pizza,
	        BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        for (ObjectError err : bindingResult.getAllErrors()) {
	            System.err.println("errore: " + err.getDefaultMessage());
	        }
	        model.addAttribute("errors", bindingResult);
	        return "edit";
	    }
	    Pizza existingPizza = getPizzaById(id);
	    if (existingPizza == null) {
	        return "redirect:/pizze";
	    }
	    existingPizza.setNome(pizza.getNome());
	    existingPizza.setDescrizione(pizza.getDescrizione());
	    existingPizza.setFoto(pizza.getFoto());
	    existingPizza.setPrezzo(pizza.getPrezzo());
	    pizzaService.save(existingPizza);
	    return "redirect:/pizze";
	}


	
	
	
	
	
	
	
	@GetMapping("/pizze/delete/{id}")
	public String delete(@PathVariable int id) {
		Optional<Pizza> pizzaOpt = pizzaService.findById(id);
		Pizza pizza = pizzaOpt.get();
		pizzaService.delete(pizza);
		return "redirect:/pizze";
	}

	
}

package com.example.midterm_application.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.midterm_application.data.model.Coffee;

import org.junit.Test;

import java.util.List;

public class CoffeeRepositoryTest {
    @Test
    public void emptySearchReturnsAllCoffees() {
        assertEquals(CoffeeRepository.getAllCoffees().size(), CoffeeRepository.searchByName("").size());
        assertEquals(CoffeeRepository.getAllCoffees().size(), CoffeeRepository.searchByName("   ").size());
    }

    @Test
    public void searchMatchesCaseInsensitivePartialName() {
        List<Coffee> results = CoffeeRepository.searchByName("MER");

        assertEquals(1, results.size());
        assertEquals("Americano", results.get(0).getName());
    }

    @Test
    public void searchReturnsEmptyListWhenNoCoffeeMatches() {
        assertTrue(CoffeeRepository.searchByName("tea").isEmpty());
    }

    @Test
    public void searchDoesNotModifyCatalog() {
        int originalSize = CoffeeRepository.getAllCoffees().size();

        CoffeeRepository.searchByName("mocha");

        assertEquals(originalSize, CoffeeRepository.getAllCoffees().size());
    }
}

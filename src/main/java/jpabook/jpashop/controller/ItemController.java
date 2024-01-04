package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.dto.BookForm;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createItemForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String createItem(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.addItem(book);

        return "redirect:/items";
    }

    @GetMapping("/items")
    public String findItems(Model model) {
        List<Item> itemList = itemService.findAll();
        model.addAttribute("items", itemList);

        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId, Model model) {
        Book book = new Book();
        Book item = (Book) itemService.findOne(itemId);
        book.setId(item.getId());
        book.setName(item.getName());
        book.setPrice(item.getPrice());
        book.setStockQuantity(item.getStockQuantity());
        book.setAuthor(item.getAuthor());
        book.setIsbn(item.getIsbn());

        model.addAttribute("form", book);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {

        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }

}

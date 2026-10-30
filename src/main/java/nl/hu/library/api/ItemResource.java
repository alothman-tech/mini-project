package nl.hu.library.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.library.api.Dtos.ItemDto;
import nl.hu.library.domain.Book;
import nl.hu.library.domain.LibraryItem;
import nl.hu.library.domain.Magazine;
import nl.hu.library.domain.Role;
import nl.hu.library.persistence.DataAccessException;
import nl.hu.library.persistence.ItemRepository;
import nl.hu.library.security.Secured;

import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {
    private final ItemRepository items = new ItemRepository();

    @GET
    @Secured
    public List<ItemDto> all() {
        return items.findAll().stream().map(ItemDto::from).toList();
    }

    @GET
    @Path("/{id}")
    @Secured
    public ItemDto one(@PathParam("id") int id) {
        return ItemDto.from(find(id));
    }

    @POST
    @Secured(Role.LIBRARIAN)
    public Response create(ItemDto dto) {
        int id = items.insert(build(0, dto));
        return Response.status(201).entity(ItemDto.from(find(id))).build();
    }

    @PUT
    @Path("/{id}")
    @Secured(Role.LIBRARIAN)
    public ItemDto update(@PathParam("id") int id, ItemDto dto) {
        LibraryItem existing = find(id);
        if (dto != null && dto.type() != null && !dto.type().equalsIgnoreCase(ItemDto.typeOf(existing))) {
            throw new ApiException(400, "Het type van een item kan niet gewijzigd worden");
        }
        LibraryItem updated = build(id, new ItemDto(id, ItemDto.typeOf(existing), dto == null ? null : dto.title(),
                dto == null ? null : dto.year(), dto == null ? null : dto.author(),
                dto == null ? null : dto.issueNumber(), null));
        items.update(updated);
        return ItemDto.from(find(id));
    }

    @DELETE
    @Path("/{id}")
    @Secured(Role.LIBRARIAN)
    public Response delete(@PathParam("id") int id) {
        try {
            if (items.delete(id) == 0) throw new ApiException(404, "Item niet gevonden");
        } catch (DataAccessException e) {
            if ("23503".equals(e.getSqlState())) { // foreign key: er bestaan leningen voor dit item
                throw new ApiException(409, "Item heeft leningen en kan niet verwijderd worden");
            }
            throw e;
        }
        return Response.noContent().build();
    }

    private LibraryItem find(int id) {
        LibraryItem item = items.findById(id);
        if (item == null) throw new ApiException(404, "Item niet gevonden");
        return item;
    }

    private static LibraryItem build(int id, ItemDto d) {
        if (d == null || d.type() == null) throw new ApiException(400, "type ontbreekt (BOOK of MAGAZINE)");
        if (d.title() == null || d.title().isBlank()) throw new ApiException(400, "title is verplicht");
        if (d.year() == null) throw new ApiException(400, "year is verplicht");
        switch (d.type().toUpperCase()) {
            case "BOOK":
                if (d.author() == null || d.author().isBlank()) throw new ApiException(400, "author is verplicht voor een boek");
                return new Book(id, d.title().trim(), d.year(), d.author().trim());
            case "MAGAZINE":
                if (d.issueNumber() == null) throw new ApiException(400, "issueNumber is verplicht voor een tijdschrift");
                return new Magazine(id, d.title().trim(), d.year(), d.issueNumber());
            default:
                throw new ApiException(400, "type moet BOOK of MAGAZINE zijn");
        }
    }
}

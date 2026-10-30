package nl.hu.library.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import nl.hu.library.api.Dtos.LoanDto;
import nl.hu.library.api.Dtos.LoanRequest;
import nl.hu.library.domain.LibraryItem;
import nl.hu.library.domain.Loan;
import nl.hu.library.domain.Role;
import nl.hu.library.persistence.DataAccessException;
import nl.hu.library.persistence.ItemRepository;
import nl.hu.library.persistence.LoanRepository;
import nl.hu.library.security.AuthUser;
import nl.hu.library.security.Secured;

import java.util.List;

@Path("/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {
    private final LoanRepository loans = new LoanRepository();
    private final ItemRepository items = new ItemRepository();

    /** Student: eigen leningen. Librarian: alle leningen. */
    @GET
    @Secured
    public List<LoanDto> list(@Context SecurityContext sc) {
        AuthUser user = (AuthUser) sc.getUserPrincipal();
        List<Loan> result = user.role() == Role.LIBRARIAN ? loans.findAll() : loans.findByUser(user.id());
        return result.stream().map(LoanDto::from).toList();
    }

    @POST
    @Secured(Role.STUDENT)
    public Response borrow(LoanRequest request, @Context SecurityContext sc) {
        AuthUser user = (AuthUser) sc.getUserPrincipal();
        if (request == null || request.itemId() == null) throw new ApiException(400, "itemId is verplicht");

        LibraryItem item = items.findById(request.itemId());
        if (item == null) throw new ApiException(404, "Item niet gevonden");
        try {
            item.borrow(); // domeinregel: een uitgeleend item kan niet nogmaals geleend worden
        } catch (IllegalStateException e) {
            throw new ApiException(409, e.getMessage());
        }

        int loanId;
        try {
            loanId = loans.insert(user.id(), item.getId());
        } catch (DataAccessException e) {
            if ("23505".equals(e.getSqlState())) throw new ApiException(409, "Item is al uitgeleend");
            throw e;
        }
        return Response.status(201).entity(LoanDto.from(loans.findById(loanId))).build();
    }

    @PUT
    @Path("/{id}/return")
    @Secured(Role.STUDENT)
    public LoanDto giveBack(@PathParam("id") int id, @Context SecurityContext sc) {
        AuthUser user = (AuthUser) sc.getUserPrincipal();
        Loan loan = loans.findById(id);
        if (loan == null) throw new ApiException(404, "Lening niet gevonden");
        if (loan.getUserId() != user.id()) throw new ApiException(403, "Dit is niet jouw lening");
        if (loan.isReturned()) throw new ApiException(409, "Al teruggebracht");

        LibraryItem item = items.findById(loan.getItemId());
        try {
            item.returnItem();
        } catch (IllegalStateException e) {
            throw new ApiException(409, e.getMessage());
        }
        loans.markReturned(id);
        return LoanDto.from(loans.findById(id));
    }
}
